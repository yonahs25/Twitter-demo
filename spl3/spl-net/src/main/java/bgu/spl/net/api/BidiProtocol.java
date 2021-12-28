package bgu.spl.net.api;

import bgu.spl.net.api.bidi.BidiMessagingProtocol;
import bgu.spl.net.api.bidi.Connections;
import bgu.spl.net.srv.bidi.ConnectionHandler;

import java.util.LinkedList;

public class BidiProtocol implements BidiMessagingProtocol<String> {


    private ConnectionHandler mine;
    private connectionImpl connections;
    private String arg;


    @Override
    public void start(int connectionId, Connections<String> connections) {


    }

    @Override
    public void process(String message)
    {
        // need to break the message to parameters
        int start = 0;
        int end = 0;
        int temp=0;
        String opcode;
        String username;
        String password;
        String birthday;
        String captcha;
        String followUnfollow;
        String content;
        String dateAndTime;
        LinkedList<String> parameters = new LinkedList<String>();

        while(message.charAt(end) != ';')
        {
            if (message.charAt(end) == ' ' || message.charAt(end) == ';')
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

        opcode = parameters.get(0);

        switch (opcode)
        {
            case ("01"):
                 username = parameters.get(1);
                 password = parameters.get(2);
                 birthday = parameters.get(3);
                 if(connections.findUser(username) != -1)
                 {
                     User user = new User(username,password,birthday);
                    connections.register(user);
                 }
                 else
                 {
                    // error message
                 }

                break;

            case ("02"):
                 username = parameters.get(1);
                 password = parameters.get(2);
                 captcha = parameters.get(3);
                 if(connections.findUser(username) != -1)
                 {
                    User user = connections.getUser(username);
                    if(user.getPassword() != password)
                    {

                    }
                    else
                    {
                        // error message
                    }
                 }
                 else
                 {
                     // error message
                 }

                break;

            case ("03"):

                break;

            case ("04"):
                followUnfollow = parameters.get(1);
                username = parameters.get(2);

                break;

            case ("05"):
                content = parameters.get(1);

                break;


            case ("06"):
                username = parameters.get(1);
                content = parameters.get(2);
                dateAndTime = parameters.get(3);

                break;

            case ("07"):

                break;

            case ("08"):
                String listOfUsernames = parameters.get(1);
                break;

        }

    }

    @Override
    public boolean shouldTerminate() {
        return false;
    }
}
