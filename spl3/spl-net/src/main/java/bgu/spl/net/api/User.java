package bgu.spl.net.api;

import bgu.spl.net.srv.bidi.ConnectionHandler;

import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.atomic.AtomicInteger;

public class User {

    private ConnectionHandler connectionHandler;
    //TODO make connctionhendlerid atomicinteger
    private AtomicInteger connectedHandlerID;
    private ConcurrentLinkedDeque<User> followers = new ConcurrentLinkedDeque<>();
    private ConcurrentLinkedDeque<User> following = new ConcurrentLinkedDeque<>();
    private ConcurrentLinkedDeque<User> blockedUsers = new ConcurrentLinkedDeque<>();
    private ConcurrentLinkedDeque<String> pendingMessages = new ConcurrentLinkedDeque<>();
    private String username;
    private String password;
    private String birthDay;
    private boolean connected;
    private int age;
    private int amountOfPosts = 0;

    public User(String username, String password, String birthDay) {
        this.username = username;
        this.password = password;
        this.birthDay = birthDay;
        age = calculateAge(birthDay);
        connected = false;
        connectionHandler=null;
//        connectedHandlerID.compareAndSet(-1) ;
    }

    public int getConnectedHandlerID() {
        return connectedHandlerID.get();
    }

    public void setConnectedHandlerID(int connectedHandlerID) {
//        TODO
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

    public boolean isConnected() {
       return connectedHandlerID.get() != -1;
    }

    public ConnectionHandler getConnectionHandler() {
        return connectionHandler;
    }


    public ConcurrentLinkedDeque<User> getFollowers() {
        return followers;
    }

    public ConcurrentLinkedDeque<User> getFollowing() {
        return following;
    }

    public ConcurrentLinkedDeque<User> getBlockedUsers() {
        return blockedUsers;
    }

    public void addFollower (User user){
        followers.add(user);
    }

    public void addFollowing(User user){
        following.add(user);
    }

    public void removerFollower(User user) {followers.remove(user);}

    public void removeFollowing(User user) {following.remove(user);}

    public ConcurrentLinkedDeque<String> getPendingMessages() {
        return pendingMessages;
    }

    public void addToPendingMessages (String message) {pendingMessages.add(message);}


}
