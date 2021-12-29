package bgu.spl.net.api;

import bgu.spl.net.api.bidi.Connections;
import bgu.spl.net.srv.bidi.ConnectionHandler;

import java.net.IDN;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;

public class connectionImpl<T> implements Connections<T> {

    private ConcurrentHashMap<Integer, ConnectionHandler> idToConnectionHandler = new ConcurrentHashMap<>();
    private ConcurrentLinkedDeque<T> messagesLog; //TODO check!
    private ConcurrentHashMap<String,User> usernameToUserImpl = new ConcurrentHashMap<>();
    private static class singeltonHolder
    {
        private static connectionImpl instance = new connectionImpl<>();
    }
    public static connectionImpl getInstance()
    {
        return singeltonHolder.instance;
    }

    @Override
    public boolean send(int connectionId, T msg) {
        return false;
    }

    @Override
    public void broadcast(T msg) {

    }

    @Override
    public void disconnect(int connectionId) {
        idToConnectionHandler.remove(connectionId);
    }
    public boolean register (User user){
        usernameToUserImpl.putIfAbsent(user.getUsername(),user);
        return true;
    }


    // adds handler to hashmap
    public void putHandler(int id, ConnectionHandler handler){
        idToConnectionHandler.putIfAbsent(id, handler);

    }

    // meant to check if some1 is logged in to user
    public int findIdFromUser(String username){
        User user = getUser(username);
        if (user != null)
            return user.getConnectedHandlerID();

        return  -2;
    }

    //returns user object with given username, returns null if there is no such username
    public User getUser(String username)
    {
        return usernameToUserImpl.getOrDefault(username,null);
    }


}

