package com.sequenceiq.samples.flume.server;

/**
 * @author keyki
 */
public interface WebSocketServer {
    void start() throws Exception;

    void startSSL(String keyStoreLocation, String keyStorePassword) throws Exception;
}
