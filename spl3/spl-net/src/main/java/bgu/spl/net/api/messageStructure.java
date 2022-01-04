package bgu.spl.net.api;

import bgu.spl.net.api.connectionImpl;

import java.util.concurrent.ConcurrentLinkedQueue;

public class messageStructure {

    private ConcurrentLinkedQueue<String> messageDataStructure = new ConcurrentLinkedQueue<>() ;
    private final String[] filter = {"race","rice","fun","rock","gum","dog","gravity"};

    private static class singeltonHolder
    {
        private static messageStructure instance = new messageStructure();
    }
    public static messageStructure getInstance()
    {
        return messageStructure.singeltonHolder.instance;
    }

    public void insertMessage (String message)
    {
    messageDataStructure.add(message);
    }
    public String filter(String message)
    {
        for(String s : filter)
        {
            message = message.replaceAll("\\b"+s+"\\b","filtered");
        }
        return message;
    }
}
