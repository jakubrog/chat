package server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.BlockingQueue;

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
                String msg = new String(receivePacket.getData(), 0, receivePacket.getLength());
                Scanner msgScan = new Scanner(msg);
                msgScan.useDelimiter(";");

                if(msgScan.hasNext()) {
                    String senderNickname = msgScan.next();
                    Client sender = null;
                    for(Client client : clients) {
                        if (client.getNickname().equals(senderNickname)) {
                            sender = client;
                            break;
                        }
                    }

                    if (sender != null && msgScan.hasNext()) {
                        System.out.println("Received UDP from client " + sender.getNickname());
                        msgQueue.put(new Message(sender, msgScan.next(), MessageType.UDP_MESSAGE));
                    }

                }
            }
        }catch(IOException | InterruptedException e){
            e.printStackTrace();
        }
    }

}
