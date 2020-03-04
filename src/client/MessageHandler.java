package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.*;
import java.util.Scanner;

public class MessageHandler {
    private BufferedReader in;
    private DatagramSocket datagramSocket;
    private Thread readTCP = new Thread(this::readTCPMessage);
    private Thread readUDP = new Thread(this::readUDPMessage);
    private Thread readMulti = new Thread(this::readMultiMessage);
    private  PrintWriter out;
    private String nickname;
    private MulticastSocket multicastSocket;

    public MessageHandler(Socket socket, DatagramSocket datagramSocket, MulticastSocket multicastSocket, String nickname){
        this.nickname = nickname;
        try {
            this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.out = new PrintWriter(socket.getOutputStream(), true);

        } catch (IOException e) {
            e.printStackTrace();
        }
        this.datagramSocket = datagramSocket;
        this.multicastSocket = multicastSocket;
    }
    public void readMessages(){
        readTCP.start();
        readUDP.start();
        readMulti.start();
    }

    private void readUDPMessage(){
        byte[] receiveBuffer = new byte[1024];
        while(true) {
            DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
            try {
                datagramSocket.receive(receivePacket);
            } catch (IOException e) {
                e.printStackTrace();
            }
            String msg = new String(receivePacket.getData(), 0, receivePacket.getLength());
            System.out.println("\b\b\b\b\b" + "UDP - "+ msg);
            System.out.print("You: ");
        }
    }
    private void readMultiMessage(){
        byte[] receiveBuffer = new byte[1024];
        while(true) {
            DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
            try {
                multicastSocket.receive(receivePacket);
            } catch (IOException e) {
                e.printStackTrace();
            }
            String msg = new String(receivePacket.getData(), 0, receivePacket.getLength());
            Scanner scan = new Scanner(msg).useDelimiter(";");
            String sender = scan.next();
            String message = scan.next();
            if(!sender.equals(nickname)) {
                System.out.println("\b\b\b\b\b" + "Multicast - " + sender + ": " + message);
                System.out.print("You: ");
            }
        }
    }

    private void readTCPMessage(){
        while(true) {
            String inputLine = null;
            try {
                inputLine = in.readLine();
            } catch (IOException e) {
                break;
            }
            System.out.println("\b\b\b\b\b" + inputLine);
            System.out.print("You: ");
            System.out.flush();
        }
    }

    public void send(String message){
        MessageType type = getMessageType(message);
        switch (type){
            case UDP:
                sendViaUDP(message);
                break;
            case TCP:
                sendViaTCP(message);
                break;
            case UDP_MULTICAST:
                System.out.println("Multi Sent");
                sendViaMulticast(message);
                break;
        }
    }

    private void sendViaUDP(String message){
        InetAddress address = null;
        try {
            address = InetAddress.getByName("localhost");
        } catch (UnknownHostException e) {
            e.printStackTrace();
        };
        // use substring(1) to avoid sending U
        byte[] sendBuffer = (nickname + ";" + message.substring(1).trim()).getBytes();
        DatagramPacket sendPacket = new DatagramPacket(sendBuffer, sendBuffer.length, address, Client.SERVER_PORT);
        try {
            datagramSocket.send(sendPacket);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendViaTCP(String message){
        out.println(message);
    }

    private void sendViaMulticast(String message){
        InetAddress address = null;
        try {
            address = InetAddress.getByName(Client.MULTICAST_ADDRESS);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        };
        // use substring(1) to avoid sending U
        byte[] sendBuffer = (nickname + ";" + message.substring(1).trim()).getBytes();
        DatagramPacket sendPacket = new DatagramPacket(sendBuffer, sendBuffer.length, address, multicastSocket.getLocalPort());
        try {
            multicastSocket.send(sendPacket);
        } catch (IOException e) {
            e.printStackTrace();
        }
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
            default:
                return MessageType.TCP;
        }
    }


}
