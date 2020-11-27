package bgu.spl.net.srv;

import bgu.spl.net.api.MessageEncoderDecoderImpl;
import bgu.spl.net.api.bidi.BidiMessagingProtocolImpl;
import bgu.spl.net.api.bidi.Database;


public class Main {

    public static void main(String[] args){
        Database data = new Database();

        Server.threadPerClient(7777,()-> new BidiMessagingProtocolImpl<>(data), ()-> new MessageEncoderDecoderImpl<>()).serve();
    }
}
