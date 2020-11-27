package bgu.spl.net.api.bidi.msgs;

/**
 * class represents LOGOUT message , opcode 3
 */
public class Logout implements Message {
    public Logout(){}

    @Override
    public short getOpcode() {
        return 3;
    }
}
