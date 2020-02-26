package server;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.Socket;

// TODO: keep address and port to send message via UDP, datagramsocket is not necessary
public class Client {
    private DatagramSocket datagramSocket;
    private Socket socket;
    private String nickname;

    public Client(Socket socket, DatagramSocket datagramSocket, String nickname) {
        this.datagramSocket = datagramSocket;
        this.socket = socket;
        this.nickname = nickname;
    }

    public DatagramSocket getDatagramSocket() {
        return datagramSocket;
    }

    public Socket getSocket() {
        return socket;
    }

    public void close() throws IOException {
        datagramSocket.close();
        socket.close();
    }


}

