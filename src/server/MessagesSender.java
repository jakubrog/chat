package server;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.BlockingQueue;

public class MessagesSender implements Runnable {
    private BlockingQueue<Message> msgQueue;
    private List<Socket> clients;

    public MessagesSender(BlockingQueue<Message> msgQueue, List<Socket> clients){
        this.msgQueue = msgQueue;
        this.clients = clients;
    }

    @Override
    public void run() {
        while(true){
            Message message = null;
            try {
                message = msgQueue.take();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if(isQuit(message) ){
                clients.remove(message.getSender());
            } else{
                send(message);
            }
        }
    }

    private void send(Message message){
        clients.forEach(e -> send(e, message));
    }

    private boolean isQuit(Message message){
        return message.getContext().equals("quit()");
    }

    private void send(Socket destination, Message message){
        if(destination == message.getSender())
            return;
        try {
            PrintWriter out = new PrintWriter(destination.getOutputStream(), true);
            out.println(message.getContext());
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
