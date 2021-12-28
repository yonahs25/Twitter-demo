package bgu.spl.net.api;

import bgu.spl.net.srv.bidi.ConnectionHandler;

import java.util.concurrent.ConcurrentLinkedDeque;

public class User {

    private ConnectionHandler connectionHandler;



    private ConcurrentLinkedDeque<User> followers = new ConcurrentLinkedDeque<>();
    private ConcurrentLinkedDeque<User> following = new ConcurrentLinkedDeque<>();
    private String username;
    private String password;
    private String birthDay;
    private boolean connected;
    private int age;
    private int ID;
    private int amountOfPosts = 0;

    public User(String username, String password, String birthDay) {
        this.username = username;
        this.password = password;
        this.birthDay = birthDay;
        age = calculateAge(birthDay);
        connected = false;
    }

    public void setConnectionHandler(ConnectionHandler connectionHandler) {
        this.connectionHandler = connectionHandler;
    }
    private int calculateAge(String birthDay){
        // TODO calculateAge
        return 0;
    }
    public int getAge(){
        return age;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public boolean isConected() {
        return connected;
    }

    public void setConected(boolean connected) {
        this.connected = connected;
    }

    public void addFolower(User user){

    }
}
