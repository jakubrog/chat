package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.*;
import java.util.Scanner;

public class Client {
    private Socket socket;
    private DatagramSocket datagramSocket;
    private PrintWriter out;
    private BufferedReader in;
    private final static String IP = "localhost";
    public final static int PORT = 1234;

    public static void main(String [] args) throws IOException, InterruptedException {
        Client client = new Client();
        System.out.println("Enter your nickname: ");
        Scanner scanner = new Scanner(System.in);
        String nickname = scanner.next();
        client.startConnection(nickname);
        MessageHandler messageHandler = new MessageHandler(client.getSocket(), client.getDatagramSocket(), nickname);
        messageHandler.readMessages();
        while(true){
            System.out.print("You: ");
            scanner = new Scanner(System.in).useDelimiter("\n");
            String message = scanner.next();

            if(message.trim().toLowerCase().equals("quit()"))
                break;
            messageHandler.send(message);
        }
        client.stopConnection();
    }

    public void startConnection(String nick) throws IOException, InterruptedException{
        startConnection(IP, PORT, nick);
    }

    public void startConnection(String ip, int port, String nick) throws IOException, InterruptedException {
        establishTCPConnection(ip, port);
        // wait some time to establish TCP connection
        Thread.sleep(1000);
        // authenticate client
        authenticateClient(ip, port, nick);
        // wait to connect
        Thread.sleep(1000);
    }
    private void establishTCPConnection(String ip, int port) throws IOException {
        socket = new Socket(ip, port);
        out = new PrintWriter(socket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    private void authenticateClient(String ip, int port, String nickname) throws IOException {
        datagramSocket = new DatagramSocket(3002);
        InetAddress address = InetAddress.getByName(ip);
        byte[] sendBuffer = ("authenticate client with nickname : " + nickname).getBytes();
        DatagramPacket sendPacket = new DatagramPacket(sendBuffer, sendBuffer.length, address, port);
        for(int i = 0; i < 3; i++) {
            datagramSocket.send(sendPacket);
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void stopConnection() throws IOException {
        in.close();
        out.close();
        socket.close();
    }

    public Socket getSocket() {
        return socket;
    }

    public DatagramSocket getDatagramSocket() {
        return datagramSocket;
    }
}