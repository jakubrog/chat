package server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.List;
import java.util.concurrent.BlockingQueue;

public class MessageSender implements Runnable {
    private BlockingQueue<Message> msgQueue;
    private List<Client> clients;
    private DatagramSocket datagramSocket;


    public MessageSender(BlockingQueue<Message> msgQueue, List<Client> clients, DatagramSocket datagramSocket){
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

            if(message != null && message.getMessageType() == MessageType.QUIT_MESSAGE){
                clients.remove(message.getSender());
            } else {
                send(message);
            }
        }
    }

    private void send(Message message){
        clients.forEach(e -> send(e, message));
    }

    private void send(Client destination, Message message){
        if(message == null || destination.equals(message.getSender()))
            return;
        try {
            if(message.getMessageType() == MessageType.TCP_MESSAGE) {
                sendViaTCP(destination, message);
            }else{
                sendViaUDP(destination, message);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendViaTCP(Client destination, Message message){
        destination.getPrintWriterOut().println(message.getContext());

    }

    private void sendViaUDP(Client destination, Message message) throws IOException {
        InetAddress address = destination.getAddress();
        int port = destination.getPort();

        byte[] sendBuffer = message.getContext().getBytes();

        DatagramPacket sendPacket = new DatagramPacket(sendBuffer, sendBuffer.length, address, port);
        datagramSocket.send(sendPacket);
    }

}
