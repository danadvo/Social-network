package bgu.spl.net.api.bidi.msgs;

/**
 * class represents ACK message , opcode 10
 */
public class ACK implements Message {
    private short msgOpcode;

    public ACK(short opcode){
        msgOpcode = opcode;
    }

    @Override
    public short getOpcode() {
        return 10;
    }

    public short getMsgOpcode() {
        return msgOpcode;
    }
}
