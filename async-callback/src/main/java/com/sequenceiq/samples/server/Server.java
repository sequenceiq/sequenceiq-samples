package com.sequenceiq.samples.server;

import com.sequenceiq.samples.client.Client;
import com.sequenceiq.samples.client.StatusRequest;

import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.atomic.AtomicLong;

public class Server extends Thread {

    private volatile boolean stopRequested = false;
    private volatile boolean stop = false;
    private AtomicLong id = new AtomicLong(0);
    private Map<Long, Client> clients = new HashMap<>();
    private Queue<StatusRequest> statusRequests = new LinkedBlockingDeque<>();

    public Server() {
        start();
    }

    @Override
    public void run() {
        while (!stop) {
            try {
                Thread.sleep(4000);
                StatusRequest statusRequest = statusRequests.poll();
                if (statusRequest != null) {
                    clients.get(statusRequest.getClientId()).process(new StatusResponse("OK"));
                }
                if (stopRequested && statusRequests.size() == 0) {
                    stop = true;
                }
            } catch (InterruptedException e) {
            }
        }
    }

    public void shutdown() {
        stopRequested = true;
    }

    public long register(Client client) {
        long id = this.id.getAndIncrement();
        clients.put(id, client);
        return id;
    }

    public void remove(long id) {
        clients.remove(id);
    }

    public void receive(StatusRequest request) {
        statusRequests.add(request);
    }
}
