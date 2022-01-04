package bgu.spl.net.api;

import java.time.LocalDate;
import java.time.Period;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.atomic.AtomicInteger;

public class User {

    private AtomicInteger connectedHandlerID;
    private final ConcurrentLinkedDeque<User> myFollowers = new ConcurrentLinkedDeque<>();
    private final ConcurrentLinkedDeque<User> iFollowing = new ConcurrentLinkedDeque<>();
    private final ConcurrentLinkedDeque<User> blockedUsers = new ConcurrentLinkedDeque<>();
    private final ConcurrentLinkedDeque<String> pendingMessages = new ConcurrentLinkedDeque<>();
    private final String username;
    private final String password;
    private final int age;
    private int amountOfPosts = 0;

    public User(String username, String password, String birthDay) {
        this.username = username;
        this.password = password;
        age = calculateAge(birthDay);
        connectedHandlerID = new AtomicInteger(-1);
    }

    public int getConnectedHandlerID() {
        return connectedHandlerID.get();
    }

    public void setConnectedHandlerID(int connectedHandlerID) {
        int oldVal;
        int newVal;
        do {
            oldVal = this.connectedHandlerID.get();
            newVal = connectedHandlerID;
        } while (!this.connectedHandlerID.compareAndSet(oldVal, newVal));
    }

    public boolean isConnected() {
        return connectedHandlerID.get() != -1;
    }

    public boolean tryLogIn(int ID){ //this may work
        return connectedHandlerID.compareAndSet(-1,ID);
    }

    private int calculateAge(String birthDay){
        int day = Integer.parseInt(birthDay.substring(0,2));
        int month = Integer.parseInt(birthDay.substring(3,5));
        int year = Integer.parseInt(birthDay.substring(6));
        LocalDate now = LocalDate.now();
        LocalDate age = LocalDate.of(year, month, day);
        return Period.between(age,now).getYears();
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public ConcurrentLinkedDeque<User> getMyFollowers() {
        return myFollowers;
    }

    public ConcurrentLinkedDeque<User> getFromIFollowing() {
        return iFollowing;
    }

    public ConcurrentLinkedDeque<User> getBlockedUsers() {
        return blockedUsers;
    }

    public void addToMyFollower (User user){
        myFollowers.add(user);
    }

    public void addToIFollowing(User user){
        iFollowing.add(user);
    }

    public void removerFromMyFollower(User user) {myFollowers.remove(user);}

    public void removeFromIFollowing(User user) {iFollowing.remove(user);}

    public ConcurrentLinkedDeque<String> getPendingMessages() {
        return pendingMessages;
    }

    public void addToPendingMessages (String message) {pendingMessages.add(message);}

    public void incrementPostsCount() {amountOfPosts++;}

    public void incrementPmCount() {}

    public String getData() {
        return (age + " " + amountOfPosts + " " + myFollowers.size() + " " + iFollowing.size());
    }

    public void addToBlockingList(User userToBlock){
        blockedUsers.add(userToBlock);
    }


}