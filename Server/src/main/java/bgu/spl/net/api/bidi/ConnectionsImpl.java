package bgu.spl.net.api.bidi;

import bgu.spl.net.srv.ConnectionHandler;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionsImpl<T> implements Connections<T> {
    private ConcurrentHashMap<Integer, ConnectionHandler> clients;


    public ConnectionsImpl(){
        clients = new ConcurrentHashMap<>();
    }

    public void sumbit(int connectionId, ConnectionHandler connHandler){
        clients.put(connectionId, connHandler);
    }

    /**
     * sends the message to the connection id in the connections list
     *
     * @param connectionId int the id of the client
     * @param msg the message that should be sent
     * @return boolean true if sent successfully, false otherwise
     */
    public boolean send(int connectionId, T msg) {
        if (clients.get(connectionId) != null) {
            clients.get(connectionId).send(msg);
            return true;
        }
        return false;
    }

    @Override
    public void broadcast(T msg) {
        for (ConnectionHandler client : clients.values())
            client.send(msg);
    }

    /**
     * remove the client from connections list
     *
     * @param connectionId int the id of client that should be removed
     */
    public void disconnect(int connectionId) {
        clients.remove(connectionId);
    }
}
