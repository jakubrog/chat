package server;

import java.io.BufferedReader;
import java.io.IOException;
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
                        msgQueue.add(new Message( client, "quit()", MessageType.QUIT_MESSAGE));
                        System.out.println("Client " + client.getNickname() + " disconnected");
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
