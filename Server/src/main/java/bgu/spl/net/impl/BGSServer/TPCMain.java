package bgu.spl.net.impl.BGSServer;

import bgu.spl.net.api.MessageEncoderDecoderImpl;
import bgu.spl.net.api.bidi.BidiMessagingProtocolImpl;
import bgu.spl.net.api.bidi.Database;
import bgu.spl.net.srv.Server;

import static java.lang.Integer.parseInt;


public class TPCMain {
    public static void main (String args[]){
        Database data = new Database();

        Server.threadPerClient(parseInt(args[0]),
                ()-> new BidiMessagingProtocolImpl<>(data),()->new  MessageEncoderDecoderImpl<>()).serve();
    }
}
