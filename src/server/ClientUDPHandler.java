package server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.concurrent.BlockingQueue;

public class ClientUDPHandler implements Runnable {
    private Client client;
    private BlockingQueue<Message> msgQueue;
    private byte[] receiveBuffer = new byte[1024];

    public ClientUDPHandler(Client client, BlockingQueue<Message> msgQueue){
        this.client = client;
        this.msgQueue = msgQueue;
    }

    public void run() {
        try {
            DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
            client.getDatagramSocket().receive(receivePacket);
            String msg = new String(receivePacket.getData(), 0, receivePacket.getLength());
            msgQueue.put(new Message(client, msg, MessageType.UDP_MESSAGE));

        }catch(IOException | InterruptedException e){
            e.printStackTrace();
        }
    }
}
