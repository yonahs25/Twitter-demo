package bgu.spl.net.api;

import bgu.spl.net.api.bidi.BidiMessagingProtocol;
import bgu.spl.net.api.bidi.Connections;
import bgu.spl.net.srv.bidi.ConnectionHandler;

import java.nio.charset.StandardCharsets;
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
        if(connections.getUser(username) != null)
        {
            User user = new User(username,password,birthday);
            connections.register(user);
        }
        else
        {
            // error message
        }
    }


    private void login (String username, String password , String captcha)
    {
        User user = connections.getUser(username);
        if(user != null)
        {
            // TODO maybe connected is problem with a few threads
            if(user.getPassword() != password || user.isConected() || Integer.parseInt(captcha) != 1)
            {
                // error message
            }
            else
            {
                //need to login
            }
        }
        else
        {
            // error message
        }

    }

    private void logout()
    {

    }

    private void followUnfollow(String followOrUnfollow , String username)
    {
        // TODO maybe replace void to string
    }


    private void post(String content)
    {

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
