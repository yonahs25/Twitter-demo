package bgu.spl.net.api;

import bgu.spl.net.api.bidi.Connections;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;

public class connectionImpl<T> implements Connections {

    private ConcurrentHashMap<Integer, Integer> users; //TODO check this shit 2nd is connection handler
    private ConcurrentLinkedDeque<Object> messagesLog; //TODO check!
    @Override
    public boolean send(int connectionId, Object msg) {
        return false;
    }

    @Override
    public void broadcast(Object msg) {

    }

    @Override
    public void disconnect(int connectionId) {

    }
}
