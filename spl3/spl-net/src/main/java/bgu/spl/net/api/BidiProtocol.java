package bgu.spl.net.api;

import bgu.spl.net.api.bidi.BidiMessagingProtocol;
import bgu.spl.net.api.bidi.Connections;
import bgu.spl.net.srv.bidi.ConnectionHandler;

public class BidiProtocol<T> implements BidiMessagingProtocol<T> {


//    private ConnectionHandler mine;
    private connectionImpl connections;
//    private T arg;


    @Override
    public void start(int connectionId, Connections<T> connections) {
        

    }

    @Override
    public void process(T message) {
        // get 2 first bytes as string
        int x = 1;
        switch (x){
            case (01):
        }

    }

    @Override
    public boolean shouldTerminate() {
        return false;
    }
}
