package server;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.DatagramSocket;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.BlockingQueue;

public class MessagesSender implements Runnable {
    private BlockingQueue<Message> msgQueue;
    private List<Client> clients;


    public MessagesSender(BlockingQueue<Message> msgQueue, List<Client> clients){
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

    private void send(Client destination, Message message){
        if(destination.equals(message.getSender()))
            return;
        try {
            if(message.getMessageType() == MessageType.TCP_MESSAGE) {
                PrintWriter out = new PrintWriter(destination.getSocket().getOutputStream(), true);
                out.println(message.getContext());
                out.close();
            }else{
                // TODO: send message via UDP
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private boolean isQuit(Message message){
        return message.getContext().equals("quit()");
    }
}
