package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;

public class Client {
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;

    public static void main(String [] args) throws IOException, InterruptedException {
//        Client c1 = new Client();
//        Client c2 = new Client();
//        c1.startConnection("localhost", 1234);
//        Thread.sleep(2000);
//        System.out.println("S2");
//        c2.startConnection("localhost", 1234);
//        System.out.println("connected");
//        Thread t1 = new Thread(() -> {
//            try {
//                c1.sendMessage(1, "Hello c2");
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        });
//        Thread t2 = new Thread(() -> {
//            try {
//                c2.sendMessage(2, "Hello c1");
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        });
//        t1.start();
//        t2.start();
//        t1.join();
//        t2.join();

        Client c1 = new Client();
        c1.startConnection("localhost", 1234);
        Thread.sleep(10000);
        c1.stopConnection();
//        c2.stopConnection();
    }

    public void startConnection(String ip, int port) throws IOException, InterruptedException {
        DatagramSocket datagramSocket = new DatagramSocket();

        clientSocket = new Socket(ip, port);
        out = new PrintWriter(clientSocket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        Thread.sleep(1000);

        InetAddress address = InetAddress.getByName("localhost");
        byte[] sendBuffer = "authenticate client with nickname : xxx".getBytes();
        DatagramPacket sendPacket = new DatagramPacket(sendBuffer, sendBuffer.length, address, port);
        datagramSocket.send(sendPacket);
    }

    public String sendMessage(int id, String msg) throws IOException {
        out.println(msg);
        String resp = in.readLine();
        System.out.println(id + resp);
        return resp;
    }

    public void stopConnection() throws IOException {
        in.close();
        out.close();
        clientSocket.close();
    }
}