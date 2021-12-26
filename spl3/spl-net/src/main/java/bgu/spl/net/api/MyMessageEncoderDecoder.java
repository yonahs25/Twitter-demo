package bgu.spl.net.api;

public class MyMessageEncoderDecoder implements MessageEncoderDecoder<String>{

    /**
     * check if i need this!!!
     * */
    @Override
    public String decodeNextByte(byte nextByte) {
        return null;
    }

    @Override
    public byte[] encode(String message) {
        return new byte[0];
    }
}
