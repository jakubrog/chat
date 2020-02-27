package server;

import javax.xml.crypto.Data;
import java.net.DatagramSocket;
import java.net.Socket;

public class Message {
    private Client sender;
    private MessageType messageType;
    private String context;

    public Message(Client sender, String context, MessageType messageType) throws IllegalArgumentException{
        this.context = context;
        this.sender = sender;
        this.messageType = messageType;
    }

    public Client getSender() {
        return sender;
    }

    public MessageType getMessageType() {
        return messageType;
    }

    public String getContext(){
        return sender.getNickname() + ": " + context;
    }

}
