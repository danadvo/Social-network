package bgu.spl.net.api.bidi.msgs;

/**
 * class represents POST message , opcode 5
 */
public class Post implements Message {
    private String content;

    public Post(String content){
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    @Override
    public short getOpcode() {
        return 5;
    }
}
