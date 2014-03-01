package com.sequenceiq.samples.client;

import com.sequenceiq.samples.callback.StatusCallback;
import com.sequenceiq.samples.server.Server;
import com.sequenceiq.samples.transfer.StatusRequest;
import com.sequenceiq.samples.transfer.StatusResponse;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class Client {

    private long sessionId;
    private Server server;
    private AtomicLong requestId = new AtomicLong(0);
    private Map<Long, StatusCallback> callbacks = new ConcurrentHashMap<>();

    public Client(Server server) {
        this.server = server;
        this.sessionId = server.register(this);
    }

    public void sendStatusRequest(StatusCallback callback) {
        long id = requestId.incrementAndGet();
        callbacks.put(id, callback);
        server.status(new StatusRequest(sessionId, id));
        System.out.println("Client (" + sessionId + ") sends status request");
    }

    public void receive(StatusResponse status) {
        long id = status.getRequestId();
        callbacks.get(id).process(status);
        callbacks.remove(id);
    }

}
