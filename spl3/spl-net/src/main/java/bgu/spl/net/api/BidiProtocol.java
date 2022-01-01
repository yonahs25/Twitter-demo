package bgu.spl.net.api;

import bgu.spl.net.api.bidi.BidiMessagingProtocol;
import bgu.spl.net.api.bidi.Connections;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentLinkedDeque;

public class BidiProtocol implements BidiMessagingProtocol<String> {

    //TODO connections.send is boolean function how can and should i use it?

    private int connectionHandlerId;
    private connectionImpl<String> connections;




    @Override
    public void start(int connectionId, Connections<String> connections) {
        this.connectionHandlerId = connectionId;


    }

    @Override
    public void process(String message) {

        String emptyString = "\0";
        char zeroChar = 0;
        int start = 2;
        int end = 2;
        String opcode;
        LinkedList<String> parameters = new LinkedList<String>();
        opcode = message.substring(0, 2);

        while (message.charAt(end) != ';') { //TODO you dont get a line with ; at the end
            //TODO problem at opcode 8
            if (message.charAt(end) == zeroChar || message.charAt(end) == ';') {
                parameters.add(message.substring(start, end));
                end++;
                start = end;
            } else {
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
                followUnfollow(parameters.get(0), parameters.get(1));
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
            //TODO case "12" block
        }

    }

    @Override
    public boolean shouldTerminate() {
        return false;
    }

    private void register(String username, String password, String birthday) {
        User user = connections.getUser(username);
        if (user == null) {
            User newUser = new User(username, password, birthday);
            connections.register(newUser);
            //TODO check if need to send more information in this ack message
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
        if (user != null) {
            // check if password is right, no1 is logged in , and captcha is right
            if (!user.getPassword().equals(password) || user.isConnected() || Integer.parseInt(captcha) != 1) {
                String error = "11" + "02";
                connections.send(connectionHandlerId, error);
            } else {
                // trying to log in, to avoid double log in
                if (user.tryLogIn(connectionHandlerId)){
                    connections.login(connectionHandlerId, user);
                    ConcurrentLinkedDeque<String> pendingMessages = user.getPendingMessages();
                    String ack = "1002";
                    connections.send(connectionHandlerId, ack);
                    // sending to client all pending messages
                    while (!pendingMessages.isEmpty()) {
                        connections.send(connectionHandlerId, pendingMessages.remove());
                    }
                }
                else {
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
        } else {
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
        //TODO need to terminate client
    }

    private void followUnfollow(String followOrUnfollow, String username) {
        //
        boolean ans = connections.followOrUn(connectionHandlerId, username, followOrUnfollow);
        if (!ans) {
            String error = "11" + "04";
            connections.send(connectionHandlerId, error);
        } else {
            String message = "10" + "04" + username;
            connections.send(connectionHandlerId,message);
        }
    }


    private void post(String content) {
        //TODO need to save posts to data structure in the server
        byte[] emptyByteArr = new byte[1];
        String emptyString = new String(emptyByteArr, 0, 1, StandardCharsets.UTF_8);
        User user = connections.getConnectedUser(connectionHandlerId);
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
                //increment the number of posts the user posted
                user.incrementPostsCount();
            } else {
                String error = "11" + "05";
                connections.send(connectionHandlerId, error);
            }
        }
    }

    private void pm(String username, String content, String dataAndTime) {
        //TODO check if there is a @ at the start of the username and remove it
        User user = connections.getConnectedUser(connectionHandlerId);
        if (user != null) {
            if (user.getConnectedHandlerID() != -1) {//???
                User receivingUser = connections.getUser(username);
                if (receivingUser != null) {
                    //need to check the content
                    // the user must follow the receivingUser
                    ConcurrentLinkedDeque<User> followingList = user.getFollowers();
                    if (!content.equals("") && followingList.contains(receivingUser)) {
                        //TODO save the pm message to a data structure
                        // TODO need to filter the message
                        String filteredContent = filter(content);
                        if (receivingUser.getConnectedHandlerID() != -1) {
                            connections.send(receivingUser.getConnectedHandlerID(), filteredContent);
                        } else {
                            receivingUser.addToPendingMessages(filteredContent);
                        }
                    } else {
                        String error = "11" + "06";
                        connections.send(connectionHandlerId, error);
                    }
                } else {
                    String error = "11" + "06";
                    connections.send(connectionHandlerId, error);
                }
            } else {
                String error = "11" + "06";
                connections.send(connectionHandlerId, error);
            }
        }

    }

    private void logstat() {
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
        if (logStatList == null){
            String error = "11" + "07";
            connections.send(connectionHandlerId, error);
        } else {
            String returnString = "";
            for (String s : logStatList)
                returnString = returnString + "10" + "07" + s + "\n";

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
                int needToBeFound = listOfUsernames.indexOf("|", index);
                if (needToBeFound != -1) {
                    String username = listOfUsernames.substring(index, needToBeFound);
                    userNames.add(username);
                    index = needToBeFound + 1;
                } else {
                    stop = true;
                }

            }
            LinkedList<String> stat = connections.stat(connectionHandlerId, userNames);
            String returningString = "";
            for (String s : stat) {
                returningString = returningString + "10" + "08" + s + "\n";
            }
            connections.send(connectionHandlerId, returningString);
        } else {
            String error = "11" + "08";
            connections.send(connectionHandlerId, error);
        }

    }

    private String filter(String content) {
        //TODO need to use the filter data structure to filter the message from filtered words
        return "";

    }
    //TODO implement private block method


}