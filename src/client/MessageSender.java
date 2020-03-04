package client;

import client.MessageHandler;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.*;

public class MessageSender {
    private String nickname;
    private DatagramSocket datagramSocket;
    private MulticastSocket multicastSocket;
    private PrintWriter out;
    private MessageHandler handler;

    public MessageSender(Client client, MessageHandler handler) {
        this.nickname = client.getNickname();
        this.datagramSocket = client.getDatagramSocket();
        this.multicastSocket = client.getMulticastSocket();
        try {
            this.out = new PrintWriter(client.getSocket().getOutputStream(), true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.handler = handler;
    }

    public void send(String message, MessageType type){
        switch (type){
            case UDP:
                sendViaUDP(message, "localhost", Client.SERVER_PORT);
                break;
            case TCP:
                sendViaTCP(message);
                break;
            case UDP_MULTICAST:
                sendViaMulticast(message);
                break;
            case QUIT:
                sendViaTCP(message);
                handler.close();
                break;
        }
    }

    private void sendViaUDP(String message, String addr, int port){
        InetAddress address = null;
        try {
            address = InetAddress.getByName(addr);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        };
        // use substring(1) to avoid sending U
        byte[] sendBuffer = (nickname + ";" + message.substring(1).trim()).getBytes();
        DatagramPacket sendPacket = new DatagramPacket(sendBuffer, sendBuffer.length, address, port);
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
        // using substring(1) to avoid sending U
        byte[] sendBuffer = (nickname + ";" + message.substring(1).trim()).getBytes();
        DatagramPacket sendPacket = new DatagramPacket(sendBuffer, sendBuffer.length, address, multicastSocket.getLocalPort());
        try {
            multicastSocket.send(sendPacket);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
