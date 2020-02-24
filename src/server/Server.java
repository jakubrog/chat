package server;

import java.io.IOException;
import java.lang.reflect.Member;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;

import static java.util.concurrent.Executors.newCachedThreadPool;

public class Server {
    private static final int PORT = 1234;
    private ServerSocket serverSocket;
    private BlockingQueue<Message> msgQueue = new LinkedBlockingQueue<>();
    private List<Socket> clients = new CopyOnWriteArrayList<>();
    private ExecutorService executor = newCachedThreadPool();


    public static void main(String [] args) throws IOException {
        new Server().start(PORT);
    }

    public void start(int port) throws IOException {
        serverSocket = new ServerSocket(port);
        new Thread(new MessagesSender(msgQueue, clients)).start();
        while (true) {
            System.out.println("Here");
            Socket socket = serverSocket.accept();
            System.out.println("Client connected " + socket.getLocalAddress());
            clients.add(socket);
            executor.submit(new ClientHandler(socket, msgQueue));
        }

    }


    public void stop() throws IOException {
        serverSocket.close();
    }

}