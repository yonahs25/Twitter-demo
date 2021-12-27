package bgu.spl.net.api.bidi;

import java.util.concurrent.ConcurrentLinkedDeque;

public class User {

    private ConcurrentLinkedDeque<User> followers = new ConcurrentLinkedDeque<>();
    private ConcurrentLinkedDeque<User> following = new ConcurrentLinkedDeque<>();
    private int age;
    private int amountOfPosts;

    public User(int age, int amountOfPosts) {
        this.age = age;
        this.amountOfPosts = amountOfPosts;
    }
}
