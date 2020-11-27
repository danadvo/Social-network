package bgu.spl.net.api.bidi.msgs;

/**
 * class represents NOTIFICATION message , opcode 9
 */
public class Notification implements Message {
    private char type;
    private String postingUser;
    private String content;

    public Notification(char type, String postingUser, String content){
        this.type = type;
        this.postingUser = postingUser;
        this.content = content;
    }

    @Override
    public short getOpcode() {
        return 9;
    }

    public char getType() {
        return type;
    }

    public String getPostingUser() {
        return postingUser;
    }

    public String getContent() {
        return content;
    }
}
