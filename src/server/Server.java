package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.*;
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
    private BlockingQueue<Message> msgQueue = new LinkedBlockingQueue<>();
    private List<Client> clients = new CopyOnWriteArrayList<>();
    private ExecutorService executor = newCachedThreadPool();


    public static void main(String [] args) throws IOException {
        new Server().start(PORT);
    }

    public void start(int port) throws IOException {

        serverSocket = new ServerSocket(port);
        datagramSocket = new DatagramSocket(port);

        new Thread(new MessageSender(msgQueue, clients, datagramSocket)).start();
        new Thread(new ClientUDPHandler(datagramSocket, msgQueue, clients)).start();

        while (true) {
            Socket socket = serverSocket.accept();
            executor.submit(() -> authenticateClient(socket));
        }
    }

    private void authenticateClient(Socket socket)  {
        System.out.println("Trying to authenticate");
        BufferedReader out = null;
        PrintWriter in = null;
        String authMessage = null;
        try {
            out = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            in = new PrintWriter(socket.getOutputStream(), true);
            authMessage = out.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(out == null || in == null || authMessage == null) {
            System.out.println("Cannot connect");
            return;
        }

        Scanner conf = new Scanner(authMessage).useDelimiter(";");
        String nickname = conf.next();
        String address = conf.next().substring(1);
        int port = Integer.parseInt(conf.next());
        Client client = null;
        try {
            client = new Client(socket, nickname, address, port, in, out);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        if(client == null) {
            System.out.println("Cannot connect");
            return;
        }
        clients.add(client);
        executor.submit(new ClientTCPHandler(client, msgQueue));
        in.println("OK");
        System.out.println("Client " + client.getNickname() + " connected");
    }


    public void stop() throws IOException {
        serverSocket.close();
    }

}