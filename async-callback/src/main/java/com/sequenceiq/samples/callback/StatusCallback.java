package com.sequenceiq.samples.callback;

import com.sequenceiq.samples.server.StatusResponse;

public interface StatusCallback {
    void process(StatusResponse status);
}
