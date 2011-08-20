package snarl4j.client;


public class FailedCommandException extends RuntimeException {
    public FailedCommandException(String clientPacket, Throwable cause) {
        super("A problem occured while trying to communicate with the Snarl server\nClient Packet=[" + clientPacket + "]\n", cause);
    }

    public FailedCommandException(String clientPacket, String serverResponse) {
        super("A problem occured while trying to communicate with the Snarl server\nClient Packet=[" + clientPacket + "]\nServer Response=[" + serverResponse + "]");
    }
}
