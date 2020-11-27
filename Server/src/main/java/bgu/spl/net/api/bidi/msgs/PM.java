package bgu.spl.net.api.bidi.msgs;

/**
 * class represents PM message , opcode 6
 */
public class PM implements Message {
    private String username;
    private String content;

    public PM(String username, String content){
        this.username = username;
        this.content = content;
    }

    @Override
    public short getOpcode() {
        return 6;
    }

    public String getUsername() {
        return username;
    }

    public String getContent() {
        return content;
    }
}
