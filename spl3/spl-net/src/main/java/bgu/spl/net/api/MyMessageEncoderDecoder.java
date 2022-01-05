package bgu.spl.net.api;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;

public class MyMessageEncoderDecoder implements MessageEncoderDecoder<String>{

    /**
     * check if I need this!!!
     * */
    private byte[] bytes = new byte[1 << 10]; //start with 1k
    private int len = 0;

    @Override
    public String decodeNextByte(byte nextByte) {
        //notice that the top 128 ascii characters have the same representation as their utf-8 counterparts
        //this allow us to do the following comparison
        if (nextByte == ';') {
            return popString();
        }
        pushByte(nextByte);
        return null; //not a line yet
    }

    private byte[] encodeStatOrLog(ArrayList<Byte> alwaysAdd, String message) {
        ArrayList<Byte> ans = new ArrayList<>(alwaysAdd);
        int nextBackslashN = message.indexOf('\n');
        message = message.substring(nextBackslashN+1);
        int space ;
        int prevSpace;
        nextBackslashN = message.indexOf('\n');
        while (nextBackslashN != -1) {
            space = message.indexOf(' ');
            byte[] one = shortToBytes(Short.parseShort(message.substring(0,space)));
            prevSpace = space+1;
            space = message.indexOf(' ', prevSpace);
            byte[] two = shortToBytes(Short.parseShort(message.substring(prevSpace,space)));
            prevSpace = space+1;
            space = message.indexOf(' ', prevSpace);
            byte[] three = shortToBytes(Short.parseShort(message.substring(prevSpace,space)));
            prevSpace = space+1;
            byte[] four = shortToBytes(Short.parseShort(message.substring(prevSpace,nextBackslashN)));
            message = message.substring(nextBackslashN+1);
            nextBackslashN = message.indexOf('\n');
            ans.add(one[0]);
            ans.add(one[1]);
            ans.add(two[0]);
            ans.add(two[1]);
            ans.add(three[0]);
            ans.add(three[1]);
            ans.add(four[0]);
            ans.add(four[1]);
            nextBackslashN = message.indexOf('\n', nextBackslashN);
        }
        byte[] toR = new byte[ans.size() + 1];
        for(int i=0; i <toR.length-1; i++) toR[i] = ans.get(i);
        toR[ans.size()] = ";".getBytes(StandardCharsets.UTF_8)[0];
        return toR;
    }
    @Override
    public byte[] encode(String message) {
        if(message.substring(0,2).equals("10") &&
                (message.substring(2,4).equals("07") || message.substring(2,4).equals("08"))) {
            ArrayList<Byte> ans = new ArrayList<>(); //check if to remove
            byte[] x = message.substring(0,4).getBytes(StandardCharsets.UTF_8);
            ArrayList<Byte> alwaysAdd = new ArrayList<>();
            for (int i=0; i<x.length;i++) alwaysAdd.add(x[i]);
            return encodeStatOrLog(alwaysAdd,message);
        }
        return (message + ";").getBytes(); //uses utf8 by default
    }

    private void pushByte(byte nextByte) {
        if (len >= bytes.length) {
            bytes = Arrays.copyOf(bytes, len * 2);
        }
        bytes[len++] = nextByte;
    }

    private String popString() {
        //notice that we explicitly requesting that the string will be decoded from UTF-8
        //this is not actually required as it is the default encoding in java.
        String result = new String(bytes, 0, len, StandardCharsets.UTF_8);
        len = 0;
        return result;
    }


    public short bytesToShort(byte[] byteArr)
    {
        short result = (short)((byteArr[0] & 0xff) << 8);
        result += (short)(byteArr[1] & 0xff);
        return result;
    }

    public byte[] shortToBytes(short num)
    {
        byte[] bytesArr = new byte[2];
        bytesArr[0] = (byte)((num >> 8) & 0xFF);
        bytesArr[1] = (byte)(num & 0xFF);
        return bytesArr;
    }
}
