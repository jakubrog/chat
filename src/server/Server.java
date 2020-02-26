package server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;

import static java.util.concurrent.Executors.newCachedThreadPool;

public class Server {
    private static final int PORT = 1234;
    private ServerSocket serverSocket;
    private DatagramSocket datagramSocket;
    private byte[] receiveBuffer;

    private BlockingQueue<Message> msgQueue = new LinkedBlockingQueue<>();
    private List<Client> clients = new CopyOnWriteArrayList<>();
    private ExecutorService executor = newCachedThreadPool();


    public static void main(String [] args) throws IOException {
        new Server().start(PORT);
    }

    public void start(int port) throws IOException {

        serverSocket = new ServerSocket(port);
        datagramSocket = new DatagramSocket(port);
        new Thread(new MessagesSender(msgQueue, clients)).start();
        while (true) {
            Socket socket = serverSocket.accept();
            executor.submit(() -> authenticateClient(socket));
        }
    }

    private void authenticateClient(Socket socket)  {
        receiveBuffer = new byte[1024];
        System.out.println("Trying to authenticate");
        while(true){
            DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
            try {
                datagramSocket.receive(receivePacket);
            } catch (IOException e) {
                e.printStackTrace();
            }
            String msg = new String(receivePacket.getData(), 0, receivePacket.getLength());
            if(msg.contains("authenticate client with nickname : ")
                    && socket.getInetAddress().equals(receivePacket.getAddress())) {

                Scanner scan = new Scanner(msg);
                scan.useDelimiter(" : ");
                scan.next();
                String nickname = scan.next();
                Client client = new Client(socket, datagramSocket, nickname);
                clients.add(client);
                executor.submit(new ClientHandler(client, msgQueue));
                System.out.println("Client connected with nickname : " + nickname);
                return;
            }
        }
    }

    public void stop() throws IOException {
        serverSocket.close();
    }

}