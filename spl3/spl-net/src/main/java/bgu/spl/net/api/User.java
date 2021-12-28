package bgu.spl.net.api;

import bgu.spl.net.srv.bidi.ConnectionHandler;

import java.util.concurrent.ConcurrentLinkedDeque;

public class User {

    private ConnectionHandler connectionHandler;



    private ConcurrentLinkedDeque<User> followers = new ConcurrentLinkedDeque<>();
    private ConcurrentLinkedDeque<User> following = new ConcurrentLinkedDeque<>();
    private String username;
    private String password;
    private int age;
    private int ID;
    private int amountOfPosts = 0;

    public User(String username, String password, int age) {
        this.username = username;
        this.password = password;
        this.age = age;
    }

    public void setConnectionHandler(ConnectionHandler connectionHandler) {
        this.connectionHandler = connectionHandler;
    }
}
