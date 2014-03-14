package com.sequenceiq.samples.flume.websocket;

/**
 * @author keyki
 */
public interface WebSocketServer {
    void start() throws Exception;

    void startSSL(String keyStoreLocation, String keyStorePassword) throws Exception;
}
