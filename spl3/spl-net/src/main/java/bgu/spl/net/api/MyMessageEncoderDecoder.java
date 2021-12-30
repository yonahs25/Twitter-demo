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

    @Override
    public byte[] encode(String message) {
        if(message.substring(0,2).equals("10") && message.substring(2,4).equals("08")){
            ArrayList<Byte> ans = new ArrayList<>();
            byte[] x = message.substring(0,4).getBytes(StandardCharsets.UTF_8);
            ArrayList<Byte> alwaysAdd = new ArrayList<>();
            for (int i=0; i<x.length;i++)  alwaysAdd.add(x[i]);
            ArrayList<Byte> toAdd = new ArrayList<>();
            // need to change this, think yona think
            byte[] one = shortToBytes(Short.parseShort(message.substring(4,6)));
            byte[] two = shortToBytes(Short.parseShort(message.substring(6,8)));
            byte[] three = shortToBytes(Short.parseShort(message.substring(8,10)));
            byte[] four = shortToBytes(Short.parseShort(message.substring(10,12)));



        }
        return (message + ";").getBytes(); //uses utf8 by default
    }

    private void pushByte(byte nextByte) {
        if (len >= bytes.length) {
            bytes = Arrays.copyOf(bytes, len * 2);
        }
        if (nextByte == 0)

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
