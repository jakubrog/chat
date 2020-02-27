package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.concurrent.BlockingQueue;

public class ClientHandler implements Runnable {
        private Client client;
        private PrintWriter out;
        private BufferedReader in;
        private BlockingQueue<Message> msgQueue;

        public ClientHandler(Client client, BlockingQueue<Message> msgQueue) {
            this.client = client;
            this.msgQueue = msgQueue;
        }

        public void run() {
            try {
                in = client.getBufferedReaderIn();

                String inputLine = in.readLine();
                while (inputLine != null) {
                    if ("quit()".equals(inputLine.toLowerCase().trim())) {
                        msgQueue.add(new Message( client, "quit()", MessageType.TCP_MESSAGE));
                        client.getPrintWriterOut().println("bye");
                        break;
                    }
                    msgQueue.put(new Message(client, inputLine, MessageType.TCP_MESSAGE));
                    inputLine = in.readLine();
                }

                client.close();

            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }
}
