package client;

import java.io.IOException;

public class Authenticator {
    private Client client;

    public Authenticator(Client client) {
        this.client = client;
    }

    public boolean authenticateWithNickname(String nickname) throws IOException {
        client.getOut().println(nickname + ";" + client.getDatagramSocket().getLocalAddress()
                +  ";" + client.getDatagramSocket().getLocalPort());
        String response = client.getIn().readLine();
        if(response.equals("OK")) {
            System.out.println("Successfully connected");
            return true;
        }
        System.out.println("Problem with authorization");
        return false;
    }
}
