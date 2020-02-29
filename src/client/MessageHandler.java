package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Socket;
import java.util.Scanner;

public class MessageHandler {
    private BufferedReader in;
    private Socket socket;
    private DatagramSocket datagramSocket;
    private Thread readTCP = new Thread(this::readTCPMessage);
    private Thread readUDP = new Thread(this::readUDPMessage);
    private  PrintWriter out;

    public MessageHandler(Socket socket, DatagramSocket datagramSocket){
        try {
            this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.out = new PrintWriter(socket.getOutputStream(), true);

        } catch (IOException e) {
            e.printStackTrace();
        }
        this.datagramSocket = datagramSocket;

    }
    public void readMessages(){
        readTCP.start();
        readUDP.start();
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
            System.out.println("\b\b\b" + msg);
            System.out.print("You:");
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
                sendViaMulticast(message);
                break;
        }
    }

    private void sendViaUDP(String message){

    }

    private void sendViaTCP(String message){
        out.println(message);
    }

    private void sendViaMulticast(String message){

    }

    private MessageType getMessageType(String msg){
        Scanner scanner = new Scanner(msg);
        scanner.useDelimiter(" ");
        if(scanner.hasNext() && scanner.next().equals("U"))
            return MessageType.UDP;

        scanner.useDelimiter(" ");
        if(scanner.hasNext() && scanner.next().equals("M"))
            return MessageType.UDP_MULTICAST;

        return MessageType.TCP;
    }


}
