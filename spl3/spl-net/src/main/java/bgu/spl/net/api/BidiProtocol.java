package bgu.spl.net.api;

import bgu.spl.net.api.bidi.BidiMessagingProtocol;
import bgu.spl.net.api.bidi.Connections;
import bgu.spl.net.srv.bidi.ConnectionHandler;

import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentLinkedDeque;

public class BidiProtocol implements BidiMessagingProtocol<String> {


    private int connectionHendlerId;
    private connectionImpl connections;
    private String arg;


    @Override
    public void start(int connectionId, Connections<String> connections) {


    }

    @Override
    public void process(String message)
    {
        // need to break the message to parameters
        // TODO find a better way to make empty char
        byte[] emptyByteArr = new byte[1];
        String emptyString = new String(emptyByteArr,0,1, StandardCharsets.UTF_8);
        char zeroChar = emptyString.charAt(0);
        int start = 2;
        int end = 2;
        String opcode;
        LinkedList<String> parameters = new LinkedList<String>();
        opcode = message.substring(0,2);

        while(message.charAt(end) != ';')
        {
            if (message.charAt(end) == zeroChar || message.charAt(end) == ';')
            {
                parameters.add(message.substring(start, end));
                end++;
                start = end;
            }
            else
            {
                end++;
            }
        }

        switch (opcode)
        {
            case ("01"):
               register(parameters.get(0),parameters.get(1),parameters.get(2));
                break;

            case ("02"):
                login(parameters.get(0),parameters.get(1),parameters.get(2));
                break;

            case ("03"):
                logout();
                break;

            case ("04"):
                followUnfollow(parameters.get(0),parameters.get(1));

                break;

            case ("05"):
                post(parameters.get(0));

                break;

            case ("06"):
                pm(parameters.get(0),parameters.get(1),parameters.get(2));

                break;

            case ("07"):
                logstat();
                break;

            case ("08"):
                stat(parameters.get(0));
                break;

        }

    }

    @Override
    public boolean shouldTerminate()
    {
        return false;
    }

    private void register (String username,String password,String birthday)
    {
        User user = connections.getUser(username);
        if(user == null)
        {
            User newUser = new User(username,password,birthday);
            connections.register(newUser);
            //TODO ack message for good registration
        }
        else
        {
            //TODO error message
        }
    }


    private void login (String username, String password , String captcha)
    {
        User user = connections.getUser(username);
        if(user != null)
        {
            if(!user.getPassword().equals(password) || user.isConnected() || Integer.parseInt(captcha) != 1)
            {
                //TODO error message
            }
            else
            {
                connections.login(connectionHendlerId, user);
                user.setConnectedHandlerID(connectionHendlerId);
                // need to send pending messages if the user loged in and have pending messages
            }
        }
        else
        {
            //TODO error message
        }

    }

    private void logout()
    {
        boolean ans = connections.logout(connectionHendlerId);
        if(!ans)
        {
            //TODO error message
        }
        else
        {
         //TODO ack message
        }
    }

    private void followUnfollow(String followOrUnfollow , String username)
    {

        boolean ans = connections.followOrUn(connectionHendlerId,username,followOrUnfollow);
        if(!ans)
        {
            //TODO error message
        }
        else
        {
            //TODO ack message
        }
    }


    private void post(String content) {
        // need to save posts to data structure in the server
        byte[] emptyByteArr = new byte[1];
        String emptyString = new String(emptyByteArr, 0, 1, StandardCharsets.UTF_8);

        //increment number of posts
//        int indexOfSpace = content.indexOf(" ");
        User user = connections.getConnectedUser(connectionHendlerId);
        if (user != null) {
            LinkedList<String> usersStrings = new LinkedList<String>();
            int lastIndexOf = content.lastIndexOf("@");
            int space = (content.substring(lastIndexOf)).indexOf(" ");
            String cont = content.substring(space + 1);
            String users = content.substring(0, space);
            boolean stop = false;
            int index = 0;
            while (!stop) {
                int indexOf = users.indexOf("@", index);
                int indexOfSpace = users.indexOf(" ", indexOf);
                if (indexOf == -1)
                    stop = true;
                else {
                    usersStrings.add(users.substring(indexOf, indexOfSpace));
                    index = indexOf + 1;
                }
            }
            if (user.getConnectedHandlerID() != -1) {
                String messageString = "05" + "1" + user.getUsername() + emptyString + cont + emptyString;
                ConcurrentLinkedDeque<User> followers = user.getFollowers();
                for (User u : followers) {
                    int connectedId = u.getConnectedHandlerID();
                    if (connectedId != -1) {
                        connections.send(connectedId, messageString);
                    } else {
                        user.addToPendingMessages(messageString);
                    }
                }
                for (String name : usersStrings) {
                    User user1 = connections.getUser(name);
                    if (user1 != null) {
                        int connectedId = user1.getConnectedHandlerID();
                        if (connectedId != -1) {
                            connections.send(connectedId, messageString);
                        } else {
                            user.addToPendingMessages(messageString);
                        }
                    }
                }
            else
                {
                    // he is not loged in need to send error
                }
            }
        }
    }
    private void pm(String username , String content , String dataAndTime)
    {

    }

    private void logstat()
    {

    }

    private void stat(String listOfUsernames)
    {

    }



}
