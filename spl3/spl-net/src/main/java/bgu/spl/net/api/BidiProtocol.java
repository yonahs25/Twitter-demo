package bgu.spl.net.api;

import bgu.spl.net.api.bidi.BidiMessagingProtocol;
import bgu.spl.net.api.bidi.Connections;
import bgu.spl.net.api.bidi.messageStructure;
import bgu.spl.net.srv.BaseServer;

import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentLinkedDeque;

public class BidiProtocol implements BidiMessagingProtocol<String> {


    private int connectionHandlerId=-1;
    private connectionImpl<String> connections=null;
    private boolean terminate=false;



    @Override
    public void start(int connectionId, Connections<String> connections) {
        this.connectionHandlerId = connectionId;
        this.connections = (connectionImpl<String>) connections;
        terminate = false;

    }

    @Override
    public void process(String message) {

        char zeroChar = 0;
        int start = 2;
        int end = 2;
        String opcode;
        LinkedList<String> parameters = new LinkedList<>();
        opcode = message.substring(0, 2);

        while (end < message.length())
        {
            if (message.charAt(end) == zeroChar )
            {
                parameters.add(message.substring(start, end));
                end++;
                start = end;
            }
            else if ( end == message.length()-1)
            {
                parameters.add(message.substring(start, end+1));
                end++;
            }
            else
            {
                end++;
            }
        }

        switch (opcode) {
            case ("01"):
                register(parameters.get(0), parameters.get(1), parameters.get(2));
                break;

            case ("02"):
                login(parameters.get(0), parameters.get(1), parameters.get(2));
                break;

            case ("03"):
                logout();
                break;

            case ("04"):
                String ForU = parameters.get(0).substring(0,1);
                String user  = parameters.get(0).substring(1);
                followUnfollow(ForU ,user);
                break;

            case ("05"):
                post(parameters.get(0));
                break;

            case ("06"):
                pm(parameters.get(0), parameters.get(1), parameters.get(2));
                break;

            case ("07"):
                logstat();
                break;

            case ("08"):
                stat(parameters.get(0));
                break;

            case ("12"):
                block(parameters.get(0));
                break;
        }
    }

    @Override
    public boolean shouldTerminate() {
        return terminate;
    }

    private void register(String username, String password, String birthday) {
        User user = connections.getUser(username);
        if (user == null) {//think
            User newUser = new User(username, password, birthday);
            connections.register(newUser);
            String message = "10" + "01";
            connections.send(connectionHandlerId, message);
        } else {
            String error = "11" + "01";
            connections.send(connectionHandlerId, error);
        }
    }


    private void login(String username, String password, String captcha) {
        User user = connections.getUser(username);
        // check if there is such user in system
        if (user != null)
        {
            // check if password is right, no1 is logged in , and captcha is right
            if (!user.getPassword().equals(password) || user.isConnected() || Integer.parseInt(captcha) != 1)
            {
                String error = "11" + "02";
                connections.send(connectionHandlerId, error);
            }
            else
            {
                // trying to log in, to avoid double log in
                if (user.tryLogIn(connectionHandlerId))
                {
                    connections.login(connectionHandlerId, user);
                    ConcurrentLinkedDeque<String> pendingMessages = user.getPendingMessages();
                    String ack = "1002";
                    connections.send(connectionHandlerId, ack);
                    // sending to client all pending messages
                    while (!pendingMessages.isEmpty())
                    {
                        connections.send(connectionHandlerId, pendingMessages.remove());
                    }
                }
                else
                {
                    String error = "1102";
                    connections.send(connectionHandlerId,error);
                }

                /*
                connections.login(connectionHandlerId, user);
                user.setConnectedHandlerID(connectionHandlerId);
                ConcurrentLinkedDeque<String> pendingMessages = user.getPendingMessages();
                while (user.getConnectedHandlerID() != -1 && !pendingMessages.isEmpty()) {
                    connections.send(connectionHandlerId, pendingMessages.remove());
                }
                */
            }
        }
        else
        {
            String error = "11" + "02";
            connections.send(connectionHandlerId, error);
        }

    }

    private void logout() {
        boolean ans = connections.logout(connectionHandlerId);
        if (!ans) {
            String error = "11" + "03";
            connections.send(connectionHandlerId, error);
        } else {
            String message = "10" + "03";
            connections.send(connectionHandlerId, message);
        }
        terminate = true;
    }

    private void followUnfollow(String followOrUnfollow, String username)
    {
        boolean ans = connections.followOrUn(connectionHandlerId, username, followOrUnfollow);
        if (!ans)
        {
            String error = "11" + "04";
            connections.send(connectionHandlerId, error);
        }
        else
        {
            String message = "10" + "04" + username;
            connections.send(connectionHandlerId,message);
        }
    }


    private void post(String content)
    {
        User user = connections.getConnectedUser(connectionHandlerId);
        if (user != null)
        {
            LinkedList<String> usersStrings = new LinkedList<>();
            boolean stop = false;

            while(!stop)
            {
                int indexOf = content.indexOf("@");
                if(indexOf != -1) {
                    int space = content.indexOf(" ", indexOf);
                    usersStrings.add(content.substring(indexOf+1, space));
                    content = content.substring(0, indexOf) + content.substring(space+1);
                }
                else
                stop = true;
            }

            if (user.getConnectedHandlerID() != -1)
            {
                //TODO check if need to filter the content
                messageStructure.getInstance().insertMessage(content);
                String messageString = "09" + "1" + user.getUsername() + "\0" + content + "\0" ;
                ConcurrentLinkedDeque<User> followers = user.getFollowers();
                for (User u : followers)
                {
                    int connectedId = u.getConnectedHandlerID();
                    if (connectedId != -1)
                    {
                        connections.send(connectedId, messageString);
                    }
                    else
                    {
                        u.addToPendingMessages(messageString);
                    }
                }
                for (String name : usersStrings)
                {
                    User user1 = connections.getUser(name);
                    if (user1 != null && !user.getBlockedUsers().contains(user1) && !user1.getBlockedUsers().contains(user))
                    {
                        if (user1.getConnectedHandlerID() != -1)
                        {
                            connections.send(user1.getConnectedHandlerID(), messageString);
                        }
                        else
                        {
                            user1.addToPendingMessages(messageString);
                        }
                    }
                }
                //increment the number of posts the user posted
                user.incrementPostsCount();
                String ack = "10" + "05"; // TODO check
                connections.send(connectionHandlerId,ack);
            }
            else
            {
                String error = "11" + "05";
                connections.send(connectionHandlerId, error);
            }
        }
    }

    private void pm(String username, String content, String dataAndTime)
    {
        User user = connections.getConnectedUser(connectionHandlerId);
        if (user != null)
        {
            if (user.getConnectedHandlerID() != -1)
            {
                User receivingUser = connections.getUser(username);
                if (receivingUser != null)
                {
                    //need to check the content
                    // the user must follow the receivingUser
                    ConcurrentLinkedDeque<User> followingList = user.getFollowers();
                    if (!content.equals("") && followingList.contains(receivingUser))
                    {
                        String filteredContent = messageStructure.getInstance().filter(content);
                        messageStructure.getInstance().insertMessage(filteredContent);
                        String message = "09" + "0" + user.getUsername() +"\0" + filteredContent +"\0";
                        if (receivingUser.getConnectedHandlerID() != -1)
                        {
                            connections.send(receivingUser.getConnectedHandlerID(), message);
                        }
                        else
                        {
                            receivingUser.addToPendingMessages(message);
                        }
                        //TODO check if this ack is needed
                        String ack = "10" + "06";
                        connections.send(connectionHandlerId,ack);
                    }
                    else
                    {
                        String error = "11" + "06";
                        connections.send(connectionHandlerId, error);
                    }
                }
                else
                {
                    String error = "11" + "06";
                    connections.send(connectionHandlerId, error);
                }
            }
            else
            {
                String error = "11" + "06";
                connections.send(connectionHandlerId, error);
            }
        }

    }

    private void logstat()
    {
        /*
        User user = connections.getConnectedUser(connectionHandlerId);
        if (user != null && user.getConnectedHandlerID() != -1) { // why double checking
            LinkedList<String> logStatList = connections.logStat(connectionHandlerId);
            String returnString = "";
            for (String s : logStatList) {
                returnString = returnString + "10" + "07" + s + "\n";
            }
            connections.send(connectionHandlerId, returnString);
        } else {
            String error = "11" + "07";
            connections.send(connectionHandlerId, error);
        }
         */
        LinkedList<String> logStatList = connections.logStat(connectionHandlerId);
        if (logStatList == null)
        {
            String error = "11" + "07";
            connections.send(connectionHandlerId, error);
        }
        else
        {
            String returnString = "1007\n";
            for (String s : logStatList)
                returnString = returnString + s + "\n";

            connections.send(connectionHandlerId, returnString);
        }



    }

    private void stat(String listOfUsernames) {

        User user = connections.getConnectedUser(connectionHandlerId);
        if (user != null && user.getConnectedHandlerID() != -1) {
            LinkedList<String> userNames = new LinkedList<>();
            int index = 0;
            boolean stop = false;
            while (!stop) {
                int needToBeFound = listOfUsernames.indexOf('|', index);
                if (needToBeFound != -1) {
                    String username = listOfUsernames.substring(index, needToBeFound);
                    userNames.add(username);
                    index = needToBeFound + 1;
                } else {
                    stop = true;
                }

            }
            LinkedList<String> stat = connections.stat(connectionHandlerId, userNames);
            String returningString = "1008\n";
            for (String s : stat) {
                returningString = returningString + s + "\n";
            }
            connections.send(connectionHandlerId, returningString);
        } else {
            String error = "11" + "08";
            connections.send(connectionHandlerId, error);
        }

    }

    private void block(String username)
    {
        User userToBlock = connections.getUser(username);
        if (userToBlock != null)
        {

            User user = connections.getConnectedUser(connectionHandlerId);
            if(user != null) {
                user.getFollowers().remove(userToBlock);
                user.getFollowing().remove(userToBlock);
                userToBlock.getFollowers().remove(user);
                userToBlock.getFollowing().remove(user);
                user.addToBlockingList(userToBlock);
            }
        }
        else
        {
            String error = "11" + "12";
            connections.send(connectionHandlerId, error);
        }
    }


}