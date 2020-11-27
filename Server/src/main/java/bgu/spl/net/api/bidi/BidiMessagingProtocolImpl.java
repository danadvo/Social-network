package bgu.spl.net.api.bidi;

import bgu.spl.net.api.bidi.msgs.*;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

public class BidiMessagingProtocolImpl<T extends Message> implements BidiMessagingProtocol<Message> {
    private boolean shouldTerminate;
    private int ownerId;
    private Database database;
    private Connections<Message> connections;

    public BidiMessagingProtocolImpl(Database database){
        shouldTerminate = false;
        this.database = database;
    }

    @Override
    public void start(int connectionId, Connections<Message> connections) {
        ownerId = connectionId;
        this.connections = connections;
    }

    @Override
    public void process(Message message) {
         switch (message.getOpcode()){
             case 1:
                 Register reg = (Register) message;
                 response (database.registerUser(reg.getUsername(),reg.getPassword()),message.getOpcode());
                 break;
             case 2:
                 Login login = (Login) message;
                 loginResponse(login);
                 break;
             case 3:
                 User user = database.logoutUser(ownerId);
                 logoutResponse(user, message);
                 break;
             case 4:
                Follow follow = (Follow) message;
                usersResponse(database.followUser(ownerId, follow.getFollow(), follow.getUserNameList()), message.getOpcode());
                break;
             case 5:
                 Post post = (Post) message;
                 postResponse(post);
                 break;
             case 6:
                PM pm = (PM) message;
                pmResponse(pm);
                break;
             case 7:
                usersResponse(database.getUserlist(ownerId),message.getOpcode());
                break;
             case 8:
                Stats stats = (Stats) message;
                statsResponse(stats,message.getOpcode());
                break;
         }
    }

    @Override
    public boolean shouldTerminate() {
        return shouldTerminate;
    }

    /**
     *sends ACK if status is true and ERROR if status is false
     * @param status boolean condition
     * @param msgOpcode Short message opcode
     */
    public void response(boolean status, Short msgOpcode){
        if(status) {
            connections.send(ownerId, new ACK(msgOpcode));
        } else {
            connections.send(ownerId, new ErrorMsg(msgOpcode));
        }
    }

    /**
     *sends ACK in response if the client was not logged in before and is registered  , sends the client notifications
     * he got when was logged out if exists
     *sends ERROR if the client is already logged or not registered
     *
     * @param login {@Login} message
     */
    public void loginResponse(Login login) {
        LinkedBlockingQueue<Notification> offMsgs = database.loginUser(login.getUsername(), login.getPassword(), ownerId);
        response(offMsgs != null, login.getOpcode());
        if (offMsgs != null) {
            while (!offMsgs.isEmpty())
                connections.send(ownerId, offMsgs.poll());
        }
    }

    /**
     *sends ACK if the user is logged in and disconnects him from the server, ERROR otherwise
     *
     * synchronization avoids the possibility to get notifications after logging out
     *
     * @param user {@User} the user to log out
     * @param message {@Logout} message
     */
    public void logoutResponse(User user, Message message){
        if (user != null) {
            synchronized (user) {
                connections.send(ownerId, new ACK(message.getOpcode()));
                connections.disconnect(ownerId);
                shouldTerminate = true;
            }
        } else {
            connections.send(ownerId,new ErrorMsg(message.getOpcode()));
        }
    }

    /**
     * sends an ACK if the list of registered users is not empty or null, ERROR otherwise
     *
     * @param successUsers List<String> the registered list of users
     * @param msgOpcode Short the opcode of USERS message (7)
     */
    public void usersResponse(List<String> successUsers, Short msgOpcode){
        if(successUsers == null || successUsers.isEmpty())
            connections.send(ownerId, new ErrorMsg(msgOpcode));
        else
            connections.send(ownerId, new UsersACK(msgOpcode, (short)successUsers.size(), successUsers));
    }

    /**
     * sends ERROR if the needed user is not registered or the user who sent the ask is not logged in
     * sends ACK otherwise
     *
     * @param stats {@Stats} message that should be sent
     * @param msgOpcode Short the OP code of stats massage
     */
    public void statsResponse(Stats stats, Short msgOpcode){
        User user = database.getStats(ownerId,stats.getUsername());
        if (user != null)
            connections.send(ownerId, new StatsACK(msgOpcode, (short)user.getNumOfPosts(),
                    (short)user.getNumOfFollowers(), (short)user.getNumOfFollowings()));
        else
            connections.send(ownerId, new ErrorMsg(msgOpcode));
    }

    /**
     * sends response to the client that sent the post (ACK/ERROR)
     * sends notification to the followers if they are logged in , if not- adds it to their offline messages queue
     *
     * synchronization avoids the recipient from logging in to the system while sending a post-
     * which may cause the post to get lost and not get to the recipient
     *
     * @param post {@Post} that should be sent
     */
    public void postResponse(Post post){
        Notification notifyPost = database.postMessage(ownerId, post.getContent());
        response(notifyPost != null, post.getOpcode());
        if (notifyPost != null){
            String username = database.getloggedUsers().get(ownerId);
            LinkedBlockingQueue<String> postRecipients = database.getPostRecipients(username, post.getContent());
            while(!postRecipients.isEmpty()){
                User recipient = database.getRegisteredUsers().get(postRecipients.poll());
                synchronized (recipient){
                    if (!connections.send(recipient.getId(),notifyPost))
                        database.addOffMsg(recipient.getUsername(),notifyPost);
                }
            }
        }
    }

    /**
     * sends response to the client that sent the PM (ACK/ERROR)
     * sends the PM message if user is logged in , if not- adds it to user's offline messages queue
     *
     * synchronization avoids the recipient from logging in to the system while sending a PM-
     * which may cause the pm to get lost and not get to the recipient
     *
     * @param pm {@PM} message that should be sent
     */
    public void pmResponse(PM pm){
        Notification notifyPm = database.pmMessage(ownerId, pm.getUsername(), pm.getContent());
        response(notifyPm != null, pm.getOpcode());
        if (notifyPm != null){
            User recipient = database.getRegisteredUsers().get(pm.getUsername());
            synchronized (recipient){
                if (!connections.send(recipient.getId(),notifyPm))
                    database.addOffMsg(recipient.getUsername(),notifyPm);
            }
        }
    }


}
