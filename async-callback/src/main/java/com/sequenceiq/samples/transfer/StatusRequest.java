package com.sequenceiq.samples.transfer;

public class StatusRequest {

    private final long clientId;
    private final long requestId;

    public StatusRequest(long clientId, long requestId) {
        this.clientId = clientId;
        this.requestId = requestId;
    }

    public long getClientId() {
        return clientId;
    }

    public long getRequestId() {
        return requestId;
    }
}
