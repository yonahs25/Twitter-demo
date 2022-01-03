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
        System.out.println("connections ------------------------");
        if(usernameToUserImpl.get(user.getUsername()) == null)
            System.out.println("the user not registered yet");
        usernameToUserImpl.putIfAbsent(user.getUsername(),user);
        System.out.println("connections end --------------------------");
        return true;
    }


    public void login(int id, User user) {
        System.out.println("connections ------------------------");
        if(idToUser.get(id) == null)
            System.out.println("the user not loged-in yet");
        else
            System.out.println("the user is already connected");
        idToUser.putIfAbsent(id, user);
        System.out.println("connections end --------------------------");


    }

    public boolean logout(int id) {
        System.out.println("connections ------------------------");
        User myUser = idToUser.get(id);
        if (myUser == null) {
            System.out.println("user not loged in");
            return false;
        }
        else {
            myUser.setConnectedHandlerID(-1);
            idToUser.remove(id);
            System.out.println("user connectionHandler id after removing : " + myUser.getConnectedHandlerID());
            if (idToUser.get(id)==null)
                System.out.println("user removed from the map");
            System.out.println("connections end --------------------------");
            return true;
        }
    }


    public boolean followOrUn(int myID, String username, String followOrUn) {
        System.out.println("connections ----------------------------");
        User otherUser = usernameToUserImpl.get(username);
        User myUser = idToUser.get(myID);
        if (otherUser == null || myUser == null || otherUser == myUser)
            return false;


        // follow case
        if(followOrUn.equals("0")){
            System.out.println("follow case");
            if (myUser.getFromIFollowing().contains(otherUser) || //im already following the other user
                    myUser.getBlockedUsers().contains(otherUser)|| //my user blocked the other user
                    otherUser.getBlockedUsers().contains(myUser)) { //other user blocked my user
                System.out.println("cant follow");
                return false;
            }
            else {
                System.out.println("can follow");
                otherUser.addToMyFollower(myUser);
                myUser.addToIFollowing(otherUser);
                if(myUser.getFromIFollowing().contains(otherUser))
                    System.out.println(otherUser.getUsername() + " is added to the follwing list of " + myUser.getUsername());
                if (otherUser.getMyFollowers().contains(myUser))
                    System.out.println(myUser.getUsername() + " is added to the followers list of " + otherUser.getUsername());
                return true;
            }
            // unfollow case
        } else {
            System.out.println("unfollow case");
            if (!myUser.getFromIFollowing().contains(otherUser)) {
                System.out.println("cant unfollow, i am not following him");
                return false;
            }
            else{
                myUser.removeFromIFollowing(otherUser);
                if(!myUser.getFromIFollowing().contains(otherUser))
                    System.out.println("i am not following " + otherUser.getUsername() + " anymore");
                otherUser.removerFromMyFollower(myUser);
                if (!otherUser.getMyFollowers().contains(myUser))
                    System.out.println(myUser.getUsername() + " is not following " + otherUser.getUsername() + " anymore");
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
