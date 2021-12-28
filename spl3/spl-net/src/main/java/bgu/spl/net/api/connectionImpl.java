package bgu.spl.net.api;

import bgu.spl.net.api.bidi.Connections;
import bgu.spl.net.srv.bidi.ConnectionHandler;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;

public class connectionImpl<T> implements Connections<T> {

    private ConcurrentHashMap<Integer, ConnectionHandler> IdToConnectionHandler = new ConcurrentHashMap<>();
    private ConcurrentLinkedDeque<Object> messagesLog; //TODO check!
    private ConcurrentHashMap<String,User> usernameToUserImpl = new ConcurrentHashMap<>();


    @Override
    public boolean send(int connectionId, T msg) {
        return false;
    }

    @Override
    public void broadcast(T msg) {

    }

    @Override
    public void disconnect(int connectionId) {

    }
    public boolean register (String id, String password, int age){

        return true;
    }
    public void putHandler(){

    }

    public int findUser(String user){



        return -1;
    }
}

