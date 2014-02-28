package com.sequenceiq.samples.client;

import com.sequenceiq.samples.callback.StatusCallback;
import com.sequenceiq.samples.server.Server;
import com.sequenceiq.samples.server.StatusResponse;

public class Client implements StatusCallback {

    private long sessionId;
    private Server server;

    public Client(Server server) {
        this.server = server;
        this.sessionId = server.register(this);
    }

    @Override
    public void process(StatusResponse status) {
        System.out.println("Client (" + sessionId + ") received status response: " + status.getStatus());
    }

    public void sendStatusRequest() {
        System.out.println("Client (" + sessionId + ") sends status request");
        server.receive(new StatusRequest(sessionId));
    }

    public static void main(String[] args) {
        Server server = new Server();

        Client client = new Client(server);
        Client client2 = new Client(server);

        client.sendStatusRequest();
        client.sendStatusRequest();

        client2.sendStatusRequest();
        client2.sendStatusRequest();

        server.shutdown();
    }
}
