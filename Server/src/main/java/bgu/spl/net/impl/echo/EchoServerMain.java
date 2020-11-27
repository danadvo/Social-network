package bgu.spl.net.impl.echo;

import bgu.spl.net.srv.Server;

public class EchoServerMain {
    public static void main(String[] args) {
// you can use any server...

        System.out.println("do you want a BLOCKING server?");
        boolean blocking = true;
        if (blocking) {

            System.out.println("SERVER STARTED - Thread Per Client");
            Server.threadPerClient(
                    7777, //port
                    EchoProtocol::new, //protocol factory
                    LineMessageEncoderDecoder::new //message encoder decoder factory
                                  ).serve();
        }
        else {
            System.out.println("SERVER STARTED - Reactor");
            Server.reactor(
                    Runtime.getRuntime().availableProcessors(),
                    7777, //port
                    EchoProtocol::new, //protocol factory
                    LineMessageEncoderDecoder::new //message encoder decoder factory
                          ).serve();
        }
    }
}
