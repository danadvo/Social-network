package bgu.spl.net.api.bidi.msgs;

/**
 * class represents USERLIST message , opcode 7
 */
public class Userlist implements Message {

    public Userlist(){}

    @Override
    public short getOpcode() {
        return 7;
    }
}
