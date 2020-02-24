package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;

public class ClientHandler implements Runnable {
        private Socket clientSocket;
        private PrintWriter out;
        private BufferedReader in;
        private BlockingQueue<Message> msgQueue;

        public ClientHandler(Socket socket, BlockingQueue<Message> msgQueue) {
            this.clientSocket = socket;
            this.msgQueue = msgQueue;
        }

        public void run() {
            try {
                out = new PrintWriter(clientSocket.getOutputStream(), true);

                in = new BufferedReader(
                        new InputStreamReader(clientSocket.getInputStream()));

                String inputLine = in.readLine();
                while (inputLine != null) {
                    if ("quit()".equals(inputLine.trim())) {
                        msgQueue.add(new Message("quit()", clientSocket));
                        out.println("bye");
                        break;
                    }
                    msgQueue.put(new Message(inputLine, clientSocket));
                    inputLine = in.readLine();
                }

                in.close();
                out.close();
                clientSocket.close();

            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }
}
