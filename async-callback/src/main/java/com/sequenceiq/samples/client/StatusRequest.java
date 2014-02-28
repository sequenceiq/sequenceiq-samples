package com.sequenceiq.samples.client;

public class StatusRequest {

    private final long clientId;

    public long getClientId() {
        return clientId;
    }

    public StatusRequest(long clientId) {

        this.clientId = clientId;
    }
}
