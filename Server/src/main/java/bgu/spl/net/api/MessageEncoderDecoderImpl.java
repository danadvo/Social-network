package bgu.spl.net.api;

import bgu.spl.net.api.bidi.msgs.*;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class MessageEncoderDecoderImpl<T extends Message> implements MessageEncoderDecoder<Message> {
    private byte[] bytes = new byte[1 << 10];
    private int len = 0;
    private short opcode = 0;
    private boolean halfDone = false;
    private int counter = 0;
    private String[] params = null;
    private String[] followers = null;

    public MessageEncoderDecoderImpl(){}

    @Override
    public Message decodeNextByte(byte nextByte) {
        switch (opcode){
            case 0:
                pushByte(nextByte);
                break;


            case 1:
                if (iParams(2,nextByte)) {
                    Message msg = new Register(params[0], params[1]);
                    reset();
                    return msg;
                }
                break;


            case 2:
                if (iParams(2,nextByte)){
                    Message msg = new Login(params[0],params[1]);
                    reset();
                    return msg;
                }
                break;


            case 4:
                if(followParams(nextByte)) {
                    Message msg = new Follow(params[0].charAt(0),Short.valueOf(params[1]),followers);
                    reset();
                    return msg;
                }
                break;


            case 5:
                if (iParams(1,nextByte)){
                    Message msg = new Post(params[0]);
                    reset();
                    return msg;
                }
                break;


            case 6:
                if(iParams(2,nextByte)){
                    Message msg = new PM(params[0],params[1]);
                    reset();
                    return msg;
                }
                break;

            case 8:
                if (iParams(1,nextByte)){
                    Message msg = new Stats(params[0]);
                    reset();
                    return msg;
                }
                break;
        }
        if (opcode == 0 && len == 2) {
            opcode = bytesToShort(bytes);
            len =0;
            if (opcode == 3) {
                reset();
                return new Logout();
            }
            else if (opcode == 7) {
                reset();
                return new Userlist();
            }
        }
        return null;
    }

    @Override
    public byte[] encode(Message message) {
        ByteBuffer buff;
        switch(message.getOpcode()) {

            case 9:
                Notification not = (Notification) message;
                buff = ByteBuffer.allocate(5 + not.getPostingUser().getBytes().length + not.getContent().getBytes().length);
                buff.put(shortToBytes(not.getOpcode()));
                if(not.getType()=='0')
                    buff.put((byte)0);
                else if(not.getType() == '1')
                    buff.put((byte)1);
                buff.put(not.getPostingUser().getBytes());
                buff.put((byte)'\0');
                buff.put(not.getContent().getBytes());
                buff.put((byte)'\0');
                return buff.array();

            case 10:
                if (message instanceof StatsACK){
                    StatsACK ack = (StatsACK) message;
                    buff = ByteBuffer.allocate(10);
                    buff.put(shortToBytes(ack.getOpcode()));
                    buff.put(shortToBytes(ack.getMsgOpcode()));
                    buff.put(shortToBytes(ack.getNumPosts()));
                    buff.put(shortToBytes(ack.getNumOfFollowers()));
                    buff.put(shortToBytes(ack.getNumOfFollowings()));
                    return buff.array();
                }
                else if (message instanceof UsersACK){
                    int count = 6;
                    UsersACK ack = (UsersACK) message;
                    for (String s : ack.getUsernamesList())
                        count = count + s.getBytes().length + 1;
                    buff = ByteBuffer.allocate(count);
                    buff.put(shortToBytes(ack.getOpcode()));
                    buff.put(shortToBytes(ack.getMsgOpcode()));
                    buff.put(shortToBytes(ack.getNumOfUsers()));
                    for (String s : ack.getUsernamesList()) {
                        buff.put(s.getBytes());
                        buff.put((byte)'\0');
                    }
                    return buff.array();
                }
                else{
                    ACK ack = (ACK) message;
                    buff = ByteBuffer.allocate(4);
                    buff.put(shortToBytes(ack.getOpcode()));
                    buff.put(shortToBytes(ack.getMsgOpcode()));
                    return buff.array();
                }

                case 11:
                    ErrorMsg error = (ErrorMsg) message;
                    buff = ByteBuffer.allocate(4);
                    buff.put(shortToBytes(error.getOpcode()));
                    buff.put(shortToBytes(error.getMsgOpcode()));
                    return buff.array();
        }
        return null;
    }

    private void reset() {
        len = 0;
        opcode = 0;
        counter = 0;
        halfDone = false;
        params = null;
        followers = null;
    }

    private void pushByte(byte nextByte){
        if (len >= bytes.length){
            bytes = Arrays.copyOf(bytes, len * 2);
        }
        bytes[len++] = nextByte;
    }

    private boolean iParams(int i, byte nextByte) {
        if (params == null)
            params = new String[i];
        if (counter < i){
            if (nextByte == '\0'){
                params[counter] = new String(bytes,0,len,StandardCharsets.UTF_8);
                counter++;
                len = 0;
            }
            else {
                pushByte(nextByte);
            }
        }
        if (counter == i)
            return true;
        return false;
    }

    private boolean followParams(byte nextByte) {
        if (params == null)
            params = new String[2];

        if (!halfDone && len <= 3) {
            pushByte(nextByte);
            if (len == 1) {
                params[counter] = String.valueOf((char) nextByte);
                counter++;
            }
            if (len == 3) {
                short numOfUsers = bytesToShort(new byte[]{bytes[1], bytes[2]});
                params[counter] = String.valueOf(numOfUsers);
                counter = 0;
                halfDone = true;
                len = 0;
            }
        } else {
            if (nextByte != '\0') {
                pushByte(nextByte);
            } else {
                if (followers == null)
                    followers = new String[Short.valueOf(params[1])];
                followers[counter] = new String(bytes, 0, len, StandardCharsets.UTF_8);
                counter++;
                len = 0;
            }
            if (counter == Integer.valueOf(params[1])){
                return true;
            }
        }
        return false;
    }
    public short bytesToShort(byte[] byteArr) {
        short result = (short)((byteArr[0] & 0xff) << 8);
        result += (short)(byteArr[1] & 0xff);
        return result;
    }

    public byte[] shortToBytes(short num) {
        byte[] bytesArr = new byte[2];
        bytesArr[0] = (byte)((num >> 8) & 0xFF);
        bytesArr[1] = (byte)(num & 0xFF);
        return bytesArr;
    }
}
