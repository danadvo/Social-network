package bgu.spl.net.impl.rci;

import bgu.spl.net.api.MessagingProtocol;
import bgu.spl.net.api.bidi.BidiMessagingProtocol;
import bgu.spl.net.api.bidi.BidiMessagingProtocolImpl;

import java.io.Serializable;

public class RemoteCommandInvocationProtocol<T> implements MessagingProtocol<Serializable> {

    private T arg;

    public RemoteCommandInvocationProtocol(T arg) {
        this.arg = arg;
    }
    
    @Override
    public Serializable process(Serializable msg) {
        return ((Command) msg).execute(arg);
    }

    @Override
    public boolean shouldTerminate() {
        return false;
    }

}
