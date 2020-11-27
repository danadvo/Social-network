package bgu.spl.net.impl.echo;

import bgu.spl.net.api.bidi.BidiMessagingProtocol;
import bgu.spl.net.api.bidi.Connections;

import java.time.LocalDateTime;

public class EchoProtocol implements BidiMessagingProtocol<String> {

    private boolean shouldTerminate = false;
    private int id;
    private Connections<String> connections;

    @Override
    public void start(int connectionId, Connections<String> connections) {
        this.id = connectionId;
        this.connections = connections;
        System.out.println("id: " + id + " connected succesfully");
    }


    @Override
    public void process(String msg) {
        shouldTerminate = "bye".equals(msg);
        String answer = createEcho(msg);
        System.out.println("[" + LocalDateTime.now() + "] got: " + msg);
        connections.send(id, answer);
        if (shouldTerminate) connections.disconnect(id);
    }

    private String createEcho(String message) {
        String echoPart = message.substring(Math.max(message.length() - 2, 0), message.length());
        return message + " .. " + echoPart + " .. " + echoPart + " ..";
    }

    @Override
    public boolean shouldTerminate() {
        return shouldTerminate;
    }

}
