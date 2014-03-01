package com.sequenceiq.samples.test;

import com.sequenceiq.samples.callback.StatusCallback;
import com.sequenceiq.samples.client.Client;
import com.sequenceiq.samples.server.Server;
import com.sequenceiq.samples.transfer.StatusResponse;

public class Executor implements StatusCallback {

    private final Client client;
    private final String name;

    public Executor(Client client, String name) {
        this.client = client;
        this.name = name;
    }

    public void serverStatus() {
        client.sendStatusRequest(this);
    }

    @Override
    public void process(StatusResponse status) {
        System.out.println("Executor (" + name + ") received status response: " + status.getStatus());
    }

    public static void main(String[] args) {
        Server server = new Server();
        Client client = new Client(server);

        Executor executor1 = new Executor(client, "exec1");
        Executor executor2 = new Executor(client, "exec2");

        executor1.serverStatus();
        executor2.serverStatus();
        executor1.serverStatus();
        executor1.serverStatus();
        executor2.serverStatus();

        server.shutdown();
    }

}
