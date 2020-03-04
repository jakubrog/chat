package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.MulticastSocket;
import java.net.Socket;
import java.util.Scanner;

public class MessageReceiver {
    private DatagramSocket datagramSocket;
    private MulticastSocket multicastSocket;
    private Socket socket;
    private String nickname;
    private Client client;
    private boolean loop = true;

    public MessageReceiver(Client client) {
        this.datagramSocket = client.getDatagramSocket();
        this.multicastSocket = client.getMulticastSocket();
        this.socket = client.getSocket();
        this.nickname = client.getNickname();
        this.client = client;
    }

    public void start(){
        new Thread(this::readMultiMessage).start();
        new Thread(this::readTCPMessage).start();
        new Thread(this::readUDPMessage).start();
    }

    private void readUDPMessage(){
        byte[] receiveBuffer = new byte[1024];
        String message = "";
        while(loop) {
            DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
            try {
                datagramSocket.receive(receivePacket);
            } catch (IOException e) {
                System.out.println("UDP quit");
                break;
            }
            message = new String(receivePacket.getData(), 0, receivePacket.getLength());
            printMessage("UDP - " + message);
        }
    }

    private void readMultiMessage(){
        byte[] receiveBuffer = new byte[1024];
        String received;
        String sender;
        String message = "";
        while(loop) {
            DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
            try {
                multicastSocket.receive(receivePacket);
            } catch (IOException e) {
                System.out.println("Multi Quit");
                break;
            }
            received = new String(receivePacket.getData(), 0, receivePacket.getLength());
            Scanner scan = new Scanner(received).useDelimiter(";");
            sender = scan.next();
            message = scan.next();
            if(!sender.equals(nickname)) {
                printMessage("Multicast - " + sender + ": " + message);
            }
        }
    }

    private void readTCPMessage() {
        BufferedReader in = null;
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        String inputLine = "";
        while(loop && inputLine != null) {
            try {
                inputLine = in.readLine();
            } catch (IOException e) {
                System.out.println("TCP quit");
                break;
            }
            if(inputLine == null){
                try {
                    client.stopConnection();
                    this.quit();
                    break;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            printMessage(inputLine);
        }


    }

    private synchronized void printMessage(String message){
        System.out.println("\b\b\b\b\b" + message);
        System.out.print("You: ");
        System.out.flush();
    }

    public void quit(){
        loop = false;
    }

}
