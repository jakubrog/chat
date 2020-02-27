package server;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.BlockingQueue;

public class MessagesSender implements Runnable {
    private BlockingQueue<Message> msgQueue;
    private List<Client> clients;
    private DatagramSocket datagramSocket;


    public MessagesSender(BlockingQueue<Message> msgQueue, List<Client> clients, DatagramSocket datagramSocket){
        this.msgQueue = msgQueue;
        this.clients = clients;
        this.datagramSocket = datagramSocket;
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

        if(destination.getAddress() == message.getSender().getAddress() &&
            destination.getPort() == message.getSender().getPort())
            return;

        try {
            if(message.getMessageType() == MessageType.TCP_MESSAGE) {
                destination.getPrintWriterOut().println(message.getContext());
            }else{
                sendViaUDP(message);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendViaUDP(Message message) throws IOException {
        Client client = message.getSender();
        InetAddress address = client.getAddress();
        int port = client.getPort();

        byte[] sendBuffer = message.getContext().getBytes();

        DatagramPacket sendPacket = new DatagramPacket(sendBuffer, sendBuffer.length, address, port);
        datagramSocket.send(sendPacket);
    }


    private boolean isQuit(Message message){
        return message.getContext().equals("quit()");
    }
}
