package bgu.spl.net.api.bidi;

import bgu.spl.net.srv.ConnectionHandler;


public interface Connections<T> {

    void sumbit(int connectionId, ConnectionHandler msg);

    boolean send(int connectionId, T msg);

    void broadcast(T msg);

    void disconnect(int connectionId);
}
