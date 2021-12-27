package bgu.spl.net.api;

import java.util.concurrent.ConcurrentLinkedDeque;

public class User {

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
}
