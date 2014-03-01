package com.sequenceiq.samples.transfer;

public class StatusResponse {

    private final long requestId;
    private final String status;

    public StatusResponse(long requestId, String status) {
        this.requestId = requestId;
        this.status = status;
    }

    public long getRequestId() {
        return requestId;
    }

    public String getStatus() {
        return status;
    }
}
