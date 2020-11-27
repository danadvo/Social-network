package bgu.spl.net.api.bidi.msgs;

/**
 * class represents STATS message , opcode 8
 */
public class Stats implements Message {
    private String username;

    public Stats(String username){
        this.username = username;
    }

    public short getOpcode() {
        return 8;
    }

    public String getUsername() {
        return username;
    }
}
