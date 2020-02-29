package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.BlockingQueue;

public class ClientTCPHandler implements Runnable {
        private Client client;
        private BufferedReader in;
        private BlockingQueue<Message> msgQueue;

        public ClientTCPHandler(Client client, BlockingQueue<Message> msgQueue) {
            this.client = client;
            this.msgQueue = msgQueue;
            this.in = client.getBufferedReaderIn();
        }

        public void run() {
            try {
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
