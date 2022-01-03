package bgu.spl.net.api;

import bgu.spl.net.api.bidi.Connections;
import bgu.spl.net.srv.bidi.ConnectionHandler;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;

public class connectionImpl<T> implements Connections<T> {

    private final ConcurrentHashMap<Integer, ConnectionHandler<T>> idToConnectionHandler = new ConcurrentHashMap<>();
    private ConcurrentLinkedDeque<T> messagesLog; //TODO check!
    private final ConcurrentHashMap<String,User> usernameToUserImpl = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Integer, User> idToUser = new ConcurrentHashMap<>();
    private static class singeltonHolder
    {
        private static connectionImpl instance = new connectionImpl<>();
    }
    public static connectionImpl getInstance()
    {
        return singeltonHolder.instance;
    }


    /*
    ACK 10 STAT
    STAT
    STAT

     */
    @Override
    public boolean send(int connectionId, T msg) {
        ConnectionHandler<T> connectionHandler = idToConnectionHandler.get(connectionId);
        if (connectionHandler==null)
            return false;
        connectionHandler.send(msg);
        return true;
    }

    @Override
    public void broadcast(T msg) {


    }

    @Override
    public void disconnect(int connectionId) {
        idToConnectionHandler.remove(connectionId);
    }


    // adds handler to hashmap
    public void putHandler(int id, ConnectionHandler<T> handler){
        idToConnectionHandler.putIfAbsent(id, handler);

    }


    // ------------------------------------ helper functions -------------------------


    //returns user object with given username, returns null if there is no such username
    public User getUser(String username)
    {
        return usernameToUserImpl.getOrDefault(username,null);
    }

    public User getConnectedUser(int id) {
        return idToUser.get(id);
    }



    // ------------------------------------ Op Functions ------------------------------------------
    public boolean register (User user){
        System.out.println("connections");
        if(usernameToUserImpl.get(user.getUsername()) == null)
            System.out.println("the user not registered yet");
        else
            System.out.println("the user is  already registered");
        usernameToUserImpl.putIfAbsent(user.getUsername(),user);
        System.out.println("connections end --------------------------");
        return true;
    }


    public void login(int id, User user) {
        System.out.println("connections");
        if(idToUser.get(id) == null)
            System.out.println("the user not loged-in yet");
        else
            System.out.println("the user is already connected");
        idToUser.putIfAbsent(id, user);
        System.out.println("connections end --------------------------");


    }

    public boolean logout(int id) {
        User myUser = idToUser.get(id);
        if (myUser == null) {
            System.out.println("the user not connected");
            return false;
        }
        else {
            myUser.setConnectedHandlerID(-1);
            idToUser.remove(id);
            System.out.println("user connectionHendler id after logout : " + myUser.getConnectedHandlerID());
            if(idToUser.get(id) == null)
                System.out.println("user was removed from the map");
            return true;
        }
    }


    public boolean followOrUn(int myID, String username, String followOrUn) {
        User otherUser = usernameToUserImpl.get(username);
        User myUser = idToUser.get(myID);
        if (otherUser == null || myUser == null)
            return false;


        // follow case
        if(followOrUn.equals("0")){
            if (myUser.getFollowing().contains(otherUser) || //im already following the other user
                    myUser.getBlockedUsers().contains(otherUser)|| //my user blocked the other user
                    otherUser.getBlockedUsers().contains(myUser)) //other user blocked my user
                return false;
            else {
                myUser.addFollower(otherUser);
                otherUser.addFollowing(myUser);
                return true;
            }
        // unfollow case
        } else {
            if (!myUser.getFollowing().contains(otherUser))
                return false;
            else{
                myUser.removeFollowing(otherUser);
                otherUser.removerFollower(myUser);
                return true;
            }
        }
    }

    public LinkedList<String> logStat (int id){
        if(idToUser.get(id) == null)
            return null;

        LinkedList<String> ans = new LinkedList<>();
        for (User user : usernameToUserImpl.values()){
            if(user.getConnectedHandlerID() == id)  continue;
            ans.add(user.getData());
        }
        return  ans;
    }


    //STAT call needs int id && LinkedList of String
    public LinkedList<String> stat(int id, LinkedList<String> usernamesList){
        if(idToUser.get(id) == null)
            return null;
        LinkedList<String> statsList = new LinkedList<>();
        for (String username : usernamesList) {
            User user = usernameToUserImpl.get(username);
            if (user == null)
                return null;
            statsList.add(user.getData());
        }

        return statsList;
    }

}

