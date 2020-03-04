package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.*;
import java.util.Random;
import java.util.Scanner;

// TODO: define multicast port


public class Client {
    private final static String IP = "localhost";
    public final static int SERVER_PORT = 1234;
    private final int PORT;
    public final int MULTI_PORT;
    public final static String MULTICAST_ADDRESS = "230.0.0.0";
    private Socket socket;
    private DatagramSocket datagramSocket;
    private MulticastSocket multicastSocket;
    private PrintWriter out;
    private BufferedReader in;


    public Client(int port) throws IOException {
        this.PORT = port;
        this.MULTI_PORT = PORT;
        this.datagramSocket = new DatagramSocket(PORT, InetAddress.getByName(IP));
        this.multicastSocket = new MulticastSocket(10000);
        multicastSocket.joinGroup(InetAddress.getByName(MULTICAST_ADDRESS));
    }

    public static void main(String [] args) throws IOException, InterruptedException {
        Client client = new Client(1024 + new Random().nextInt(5000));
        System.out.println("Enter your nickname: ");
        Scanner scanner = new Scanner(System.in);
        String nickname = scanner.next();
        client.startConnection(nickname);
        MessageHandler messageHandler = new MessageHandler(client.getSocket(), client.getDatagramSocket(), client.getMulticastSocket(), nickname);
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
        startConnection(IP, SERVER_PORT, nick);
    }

    public void startConnection(String ip, int port, String nick) throws IOException, InterruptedException {
        establishTCPConnection(ip, port);
        authenticateClient(nick);
    }
    private void establishTCPConnection(String ip, int port) throws IOException {
        socket = new Socket(ip, port);
        out = new PrintWriter(socket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    private void authenticateClient(String nickname) throws IOException {
        out.println(nickname + ";" + datagramSocket.getLocalAddress()  +  ";" + datagramSocket.getLocalPort());
        String response = in.readLine();
        if(response.equals("OK")) {
            System.out.println("Successfully connected");
            return;
        }
        System.out.println("Problems with authorization");
    }

    public void stopConnection() throws IOException {
        in.close();
        out.close();
        socket.close();
        multicastSocket.leaveGroup(InetAddress.getByName(MULTICAST_ADDRESS));
        multicastSocket.close();
    }

    public Socket getSocket() {
        return socket;
    }

    public DatagramSocket getDatagramSocket() {
        return datagramSocket;
    }

    public MulticastSocket getMulticastSocket() {
        return multicastSocket;
    }
}