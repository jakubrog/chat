package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

public class Client {
    private Socket socket;
    private String nickname;
    private InetAddress address;
    private PrintWriter out;
    private BufferedReader in;
    private int port;

    public Client(Socket socket, String nickname, InetAddress address, int portUDP) {
        this.address = address;
        this.port = portUDP;
        this.socket = socket;
        this.nickname = nickname;

        try {
            this.out = new PrintWriter(socket.getOutputStream(), true);
            this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Socket getSocket() {
        return socket;
    }

    public void close() throws IOException {
        in.close();
        out.close();
        socket.close();
    }

    public String getNickname() {
        return nickname;
    }

    public InetAddress getAddress() {
        return address;
    }

    public int getPort() {
        return port;
    }

    public PrintWriter getPrintWriterOut() {
        return out;
    }

    public BufferedReader getBufferedReaderIn() {
        return in;
    }
}

