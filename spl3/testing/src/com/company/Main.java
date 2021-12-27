package com.company;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class Main {

    public static void main(String[] args) {
	// write your code here
        short me = 3000;
        String azx = "0";

        byte[] moomoo = azx.getBytes(StandardCharsets.UTF_8);

        for(int i = 0;i<moomoo.length;i++){
            System.out.println(moomoo[i]);
        }
//        byte[] mine = shortToBytes(me);
//        System.out.println(mine[0]);
//        System.out.println(mine[1]);

    }
    public static byte[] shortToBytes(short num)
    {
        byte[] bytesArr = new byte[2];
        bytesArr[0] = (byte)((num >> 8) & 0xFF);
        bytesArr[1] = (byte)(num & 0xFF);
        return bytesArr;
    }






}
