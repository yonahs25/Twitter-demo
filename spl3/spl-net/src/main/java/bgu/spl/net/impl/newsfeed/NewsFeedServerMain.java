package bgu.spl.net.impl.newsfeed;

import bgu.spl.net.api.BidiProtocol;
import bgu.spl.net.api.MyMessageEncoderDecoder;
import bgu.spl.net.srv.Server;

public class NewsFeedServerMain {

    public static void main(String[] args) {
        NewsFeed feed = new NewsFeed(); //one shared object

// you can use any server...
//        Server.threadPerClient(
//                7776,
//                BidiProtocol::new,
//                MyMessageEncoderDecoder::new).serve();

        Server.reactor(
                Runtime.getRuntime().availableProcessors(),
                7776,
                BidiProtocol::new,
                MyMessageEncoderDecoder::new).serve();

//        Server.threadPerClient(
//                7777, //port
//                () -> new RemoteCommandInvocationProtocol<>(feed), //protocol factory
//                ObjectEncoderDecoder::new //message encoder decoder factory
//        ).serve();

//        Server.reactor(
//                Runtime.getRuntime().availableProcessors(),
//                7777, //port
//                () ->  new RemoteCommandInvocationProtocol<>(feed), //protocol factory
//                ObjectEncoderDecoder::new //message encoder decoder factory
//        ).serve();


//        Server.threadPerClient(
//                7777, //port
//                EchoProtocol::new, //protocol factory
//                LineMessageEncoderDecoder::new//message encoder decoder factory
//        ).serve();


    }
}
