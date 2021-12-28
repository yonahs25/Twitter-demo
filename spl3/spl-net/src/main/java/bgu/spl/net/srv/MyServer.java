//package bgu.spl.net.srv;
//
//import bgu.spl.net.api.MessageEncoderDecoder;
//import bgu.spl.net.api.MessagingProtocol;
//
//import java.io.IOException;
//import java.net.ServerSocket;
//import java.net.Socket;
//import java.util.function.Supplier;
//
//public class MyServer<T> implements Server{
//
//    private int ID = 0;
//    private final int port;
//    private final Supplier<MessagingProtocol<T>> protocolFactory;
//    private final Supplier<MessageEncoderDecoder<T>> encdecFactory;
//    private ServerSocket sock;
//
//    public MyServer(
//            int port,
//            Supplier<MessagingProtocol<T>> protocolFactory,
//            Supplier<MessageEncoderDecoder<T>> encdecFactory) {
//
//        this.port = port;
//        this.protocolFactory = protocolFactory;
//        this.encdecFactory = encdecFactory;
//        this.sock = null;
//    }
//
//    @Override
//    public void serve() {
//
//        try (ServerSocket serverSock = new ServerSocket(port)) {
//            System.out.println("Server started");
//
//            this.sock = serverSock; //just to be able to close
//
//            while (!Thread.currentThread().isInterrupted()) {
//
//                Socket clientSock = serverSock.accept();
//
//                BlockingConnectionHandler<T> handler = new BlockingConnectionHandler<>(
//                        clientSock,
//                        encdecFactory.get(),
//                        protocolFactory.get());
//
//                execute(handler);
//            }
//        } catch (IOException ex) {
//        }
//
//        System.out.println("server closed!!!");
//
//    }
//
//    @Override
//    public void close() throws IOException {
//
//    }
//}