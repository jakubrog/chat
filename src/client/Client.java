package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.*;
import java.util.Random;
import java.util.Scanner;

public class Client {
    private final static String IP = "localhost";
    final static String MULTICAST_ADDRESS = "230.0.0.0";
    final static int SERVER_PORT = 1234;
    private final int PORT;
    private Socket socket;
    private DatagramSocket datagramSocket;
    private MulticastSocket multicastSocket;
    private PrintWriter out;
    private BufferedReader in;
    private String nickname;
    private boolean loop = true;

    public static void main(String [] args) throws IOException, InterruptedException {
        new Client(3000 + new Random().nextInt(1000)).start();
    }

    public Client(int port) throws IOException {
        this.PORT = port;
    }

    public void start() throws IOException {
        setNickname();
        try {
            startConnection(IP, SERVER_PORT);
        }catch (IOException e){
            System.err.println("Cannot connect to server");
            return;
        }
        MessageHandler messageHandler = new MessageHandler(this);
        messageHandler.startReading();
        Scanner scanner;

        while(loop){
            System.out.print("You: ");
            scanner = new Scanner(System.in).useDelimiter("\n");
            String message = scanner.next();
            if(message.startsWith("UFILE")) {
                messageHandler.sendFileMessage(message.substring("UFILE".length()).trim());
            }else{
                messageHandler.send(message);
            }

            if(message.trim().toLowerCase().equals("quit()")) {
                messageHandler.close();
                stopConnection();
                break;
            }
        }
        System.err.println("Client disconnected");
    }

    public void setNickname(){
        System.out.println("Enter your nickname: ");
        Scanner scanner = new Scanner(System.in);
        this.nickname = scanner.next();
    }

    public void startConnection(String ip, int port) throws IOException {
        datagramSocket = new DatagramSocket(PORT, InetAddress.getByName(IP));
        multicastSocket = new MulticastSocket(10000);
        multicastSocket.joinGroup(InetAddress.getByName(MULTICAST_ADDRESS));
        establishTCPConnection(ip, port);
        new Authenticator(this).authenticateWithNickname(nickname);
    }

    private void establishTCPConnection(String ip, int port) throws IOException {
        socket = new Socket(ip, port);
        out = new PrintWriter(socket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    public void stopConnection() throws IOException {
        loop = false;
        in.close();
        out.close();
        socket.close();
        multicastSocket.close();
        datagramSocket.close();
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

    public String getNickname() {
        return nickname;
    }

    public PrintWriter getOut() {
        return out;
    }

    public BufferedReader getIn() {
        return in;
    }
}