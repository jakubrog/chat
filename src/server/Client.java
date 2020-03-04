package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client {
    private Socket socket;
    private String nickname;
    private InetAddress address;
    private PrintWriter out;
    private BufferedReader in;
    private int port;

    public Client(Socket socket, String nickname, String address, int portUDP, PrintWriter out, BufferedReader in ) throws UnknownHostException {
        this.address = InetAddress.getByName(address);
        this.port = portUDP;
        this.socket = socket;
        this.nickname = nickname;
        this.out = out;
        this.in = in;
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

    @Override
    public boolean equals(Object other){
        if(other instanceof Client){
            return ((Client) other).getNickname().equals(this.nickname);
        }
        return false;
    }
}

