package bgu.spl.net.impl;

import bgu.spl.net.api.BidiProtocol;
import bgu.spl.net.api.MyMessageEncoderDecoder;
import bgu.spl.net.srv.Server;

public class TPCMain {

    public static void main(String[] args) throws Exception {


        Server.threadPerClient(
                Integer.parseInt(args[0]),
                BidiProtocol::new,
                MyMessageEncoderDecoder::new).serve();


    }
}
