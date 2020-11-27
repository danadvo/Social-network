package bgu.spl.net.api.bidi;

import bgu.spl.net.api.bidi.msgs.Notification;
import bgu.spl.net.api.bidi.msgs.Register;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * class represents the data base- keeps all of the data structures and does help operations as part of the process
 */
public class Database {
    private ConcurrentHashMap<String, User> RegisteredUsers;
    private ConcurrentHashMap<Integer, String> loggedIn;
    private ConcurrentHashMap<String, LinkedBlockingQueue<String>> posts;
    private ConcurrentHashMap<String, LinkedBlockingQueue<Notification>> offlineMessages;
    private LinkedBlockingQueue<String> orderedUsers;
    private Object lockRegister;

    /**
     * constructor
     */
    public Database(){
        loggedIn = new ConcurrentHashMap<>();
        RegisteredUsers = new ConcurrentHashMap<>();
        posts = new ConcurrentHashMap<>();
        offlineMessages = new ConcurrentHashMap<>();
        orderedUsers = new LinkedBlockingQueue<>();
        lockRegister = new Object();
    }

    public ConcurrentHashMap<String, User> getRegisteredUsers() {
        return RegisteredUsers;
    }

    public ConcurrentHashMap<Integer, String> getloggedUsers() {
        return loggedIn;
    }

    public ConcurrentHashMap<String, LinkedBlockingQueue<String>> getPosts() {
        return posts;
    }


    /**
     * adding client to registered list if isn't registered already
     *
     * synchronization avoids the registration of the same client twice by 2 different threads
     *
     * @param username String username of the user needs to be registered
     * @param password String password of the user needs to be registered
     * @return boolean true -if the client added successfully to the registered list, false-otherwise
     */
    public boolean registerUser(String username, String password){
        synchronized (lockRegister) {
            if (!RegisteredUsers.containsKey(username)) {
                RegisteredUsers.put(username,new User(username,password));
                orderedUsers.add(username);
                posts.put(username, new LinkedBlockingQueue<>());
                offlineMessages.put(username, new LinkedBlockingQueue<>());
                return true;
            }
        }
        return false;
    }

    /**
     * login the given user if wasn't logged in before and sends him notifications of messages he got when was logout
     *
     * synchronization avoids the logging in of the same client twice by 2 different threads which will cause
     * logging in of user that is already logged in
     *
     * @param username String username of the user needs to be login
     * @param password String password of the user needs to be login
     * @param id int the id of the user needs to be login
     * @return LinkedBlockingQueue<Notification> notifications of messages the user got when was logout
     */
    public LinkedBlockingQueue<Notification> loginUser(String username, String password, int id){
        if (RegisteredUsers.containsKey(username)) {
            if ((RegisteredUsers.get(username).getPassword()).equals(password)) {
                synchronized (RegisteredUsers.get(username)) {
                    if (!loggedIn.containsKey(id) && !RegisteredUsers.get(username).isLoggedIn()) {
                        RegisteredUsers.get(username).setId(id);
                        RegisteredUsers.get(username).login();
                        loggedIn.put(id, username);
                        return offlineMessages.get(username);
                    }
                }
            }
        }
        return null;
    }

    /**
     * removes the user from logged in map if exists there, and retrieves the user, otherwise returns null
     *
     * @param id int  the id of the user that should logout
     * @return {@User} the user we should logout
     */
    public User logoutUser(int id){
        if (loggedIn.containsKey(id)) {
            User user = RegisteredUsers.get(loggedIn.get(id));
            user.setId(-1);
            user.logout();
            loggedIn.remove(id);
            return user;
        }
        return null;
    }

    /**
     * adds the user that sent the command to the followers list of the users in the list , if he wasn't there before
     *
     * @param id int the id of the user that sent the command
     * @param followUnfollow char  0-follow 1- unfollow
     * @param followers list of usernames that the sender should follow/unfollow
     * @return List<String> list of the users that the sender was added successfully to their followers list
     */
    public List<String> followUser(int id, char followUnfollow, List<String> followers) {
        List<String> successFollowers = new LinkedList<>();
        if (loggedIn.containsKey(id)) {
            User user = RegisteredUsers.get(loggedIn.get(id));
            switch (followUnfollow) {
                case 0:
                    for (String toFollow : followers) {
                        if (RegisteredUsers.containsKey(toFollow) && !user.getFollowers().contains(toFollow)) {
                            user.getFollowers().add(toFollow);
                            user.addNumOfFollowers();
                            RegisteredUsers.get(toFollow).getFollowings().add(user.getUsername());
                            RegisteredUsers.get(toFollow).addNumOfFollowings();
                            successFollowers.add(toFollow);
                        }
                    }
                    break;

                case 1:
                    for (String toUnfollow : followers) {
                        if (RegisteredUsers.containsKey(toUnfollow) && user.getFollowers().contains(toUnfollow)) {
                            user.getFollowers().remove(toUnfollow);
                            user.reduceNumOfFollowers();
                            RegisteredUsers.get(toUnfollow).reduceNumOfFollowings();
                            RegisteredUsers.get(toUnfollow).getFollowings().remove(user.getUsername());
                            successFollowers.add(toUnfollow);
                        }
                    }
                    break;
            }
        }
        return successFollowers;
    }

    /**
     * retrieves notification of the post that was published , if the sender is not logged in - retrieves null
     * adds the post to the posts map
     *
     * @param id int id of the publisher of the post
     * @param content String the content of the post
     * @return {@Notification} includes the name of the publisher and content of the post
     */
    public Notification postMessage(int id, String content){
        if (loggedIn.containsKey(id)){
            String username = loggedIn.get(id);
            posts.get(username).add(content);
            RegisteredUsers.get(username).addPost();
            return new Notification('1',username,content);
        }
        return null;
    }

    /**
     * retrieves the queue of recipients that were tagged in the post
     *
     * @param username String the username of post publisher
     * @param content String the content of the post
     * @return LinkedBlockingQueue<String> recipients
     */
    public LinkedBlockingQueue<String> getPostRecipients(String username, String content){
        LinkedBlockingQueue<String> recipients = new LinkedBlockingQueue<>();
        for (String following : RegisteredUsers.get(username).getFollowings())
            recipients.add(following);
        addTaggedRecipients(recipients, content);
        return recipients;
    }

    /**
     * adds the recipients that were tagged ( @username ) in the post to recipients queue if weren't there before
     *
     * @param recipients LinkedBlockingQueue<String> the queue of recipients that should get notification
     * @param content String the content of the message
     */
    public void addTaggedRecipients(LinkedBlockingQueue<String> recipients, String content){
        int index = 0;
        String tagged;
        while (content.indexOf('@',index) != -1){
            index = content.indexOf('@', index);
            if (content.indexOf(" ",index) != -1)
                tagged = content.substring(index+1, content.indexOf(" ",index));
            else
                tagged = content.substring(index+1);
            if (RegisteredUsers.containsKey(tagged) && !recipients.contains(tagged))
                recipients.add(tagged);
            index++;
        }
    }

    /**
     * retrieves notification of the PM that was sent and adds it to the posts map
     * retrieves null if the sender not logged in or the recipient not registered
     *
     * @param id int id of the user that sent the post
     * @param recipient String recipient that should get the PM
     * @param content String content of the PM
     * @return {@Notification} of the sender username and the content of the post
     */
    public Notification pmMessage(int id, String recipient, String content){
        if (loggedIn.containsKey(id) && RegisteredUsers.containsKey(recipient)){
            String username = loggedIn.get(id);
            posts.get(username).add(content);
            return new Notification('0', username, content);
        }
        return null;
    }

    /**
     * adds an notification of message that was sent to logged our user to the offline messages queue
     *
     * @param username String username of the user that the notification was sent to
     * @param msg {@Notification} that should be sent to the logged off user
     */
    public void addOffMsg(String username, Notification msg){
        offlineMessages.get(username).add(msg);
    }

    public List<String> getUserlist(int id){
        List<String> usernameList = null;
        if (loggedIn.containsKey(id)) {
            usernameList = new LinkedList<>();
            usernameList.addAll(orderedUsers);
        }
        return usernameList;
    }

    /**
     * retrieves the needed user if he is logged in and registered, null otherwise
     *
     * @param id int the id of the given user
     * @param username the username of the given user
     * @return {@User} the needed user
     */
    public User getStats(int id, String username){
        User user = null;
        if (loggedIn.containsKey(id)) {
            if (RegisteredUsers.containsKey(username)) {
                user = RegisteredUsers.get(username);
            }
        }
        return user;
    }


}
