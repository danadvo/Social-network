package bgu.spl.net.api.bidi.msgs;

/**
 * class represents ERROR message , opcode 11
 */
public class ErrorMsg implements Message {
    private Short msgOpcode;

    public ErrorMsg(Short opcode){
        msgOpcode = opcode;
    }

    @Override
    public short getOpcode() {
        return 11;
    }

    public Short getMsgOpcode() {
        return msgOpcode;
    }

    public void setMsgOpcode(Short msgOpcode) {
        this.msgOpcode = msgOpcode;
    }
}
