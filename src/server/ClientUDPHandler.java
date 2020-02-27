package server;

import javax.xml.crypto.Data;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.stream.Collectors;

// TODO: how to find who has send this message
public class ClientUDPHandler implements Runnable {
    private DatagramSocket socket;
    private BlockingQueue<Message> msgQueue;
    private List<Client> clients;
    private byte[] receiveBuffer = new byte[1024];

    public ClientUDPHandler(DatagramSocket socket, BlockingQueue<Message> msgQueue, List<Client> clients){
        this.socket = socket;
        this.msgQueue = msgQueue;
        this.clients = clients;
    }

    public void run() {
        try {
            while(true) {
                DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
                socket.receive(receivePacket);
                Client sender = clients
                        .stream()
                        .filter(e -> receivePacket.getAddress() == e.getAddress() && receivePacket.getPort() == e.getPort())
                        .collect(Collectors.toList())
                        .get(0);

                if(sender != null) {
                    String msg = new String(receivePacket.getData(), 0, receivePacket.getLength());
                    msgQueue.put(new Message(sender, msg, MessageType.UDP_MESSAGE));
                }
            }
        }catch(IOException | InterruptedException e){
            e.printStackTrace();
        }
    }
}
