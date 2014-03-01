package com.sequenceiq.samples.callback;

import com.sequenceiq.samples.transfer.StatusResponse;

public interface StatusCallback {
    void process(StatusResponse status);
}
