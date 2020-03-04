package client;


import java.net.*;
import java.util.Scanner;

public class MessageHandler {
    private MessageSender sender;
    private MessageReceiver receiver;

    public MessageHandler(Client client){
        this.sender = new MessageSender(client, this);
        this.receiver = new MessageReceiver(client);

    }

    public void startReading(){
        this.receiver.start();
    }

    public void send(String message){
        sender.send(message, this.getMessageType(message));
    }

    public void close(){
        receiver.quit();
    }

    private MessageType getMessageType(String msg){
        msg = msg.trim();
        Scanner scanner = new Scanner(msg);
        scanner.useDelimiter(" ");
        String next = null;
        if(scanner.hasNext())
            next = scanner.next();

        if(next == null)
            return MessageType.TCP;

        switch (next){
            case "M":
                return MessageType.UDP_MULTICAST;
            case "U":
                return MessageType.UDP;
            case "quit()":
                return MessageType.QUIT;
            default:
                return MessageType.TCP;
        }
    }
}
