package server;

import java.net.InetAddress;
import java.net.Socket;

public class Message {
    private static final int MAX_LENGTH = 1024;
    private Socket sender;
    private String context;

    public Message(String context, Socket sender){
        this.context = context;
        this.sender = sender;
    }

    public String getContext() {
        return context;
    }

    public Socket getSender() {
        return sender;
    }
}
