package com.sequenceiq.samples.flume.websocket;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.flume.channel.ChannelProcessor;
import org.apache.flume.event.SimpleEvent;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.WebSocketListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author keyki
 */
public class JettyWebSocketListener implements WebSocketListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(JettyWebSocketListener.class);
    private static final int LOG_PROCESS_THRESHOLD = 100;
    private final ChannelProcessor channelProcessor;
    private AtomicLong processedEvent = new AtomicLong(0);
    private Session session;

    public JettyWebSocketListener(ChannelProcessor channelProcessor) {
        this.channelProcessor = channelProcessor;
    }

    @Override
    public void onWebSocketBinary(byte[] bytes, int i, int i2) {
    }

    @Override
    public void onWebSocketClose(int i, String s) {
        LOGGER.info("Web socket connection closed with status {}", i);
    }

    @Override
    public void onWebSocketConnect(Session session) {
        LOGGER.info("New web socket session created with {}", session.getRemoteAddress());
        this.session = session;
    }

    @Override
    public void onWebSocketError(Throwable throwable) {
        LOGGER.error("Error during web socket connection", throwable);
    }

    @Override
    public void onWebSocketText(String s) {
        LOGGER.info("Received message: {}", s);
        channelProcessor.processEvent(createEvent(s));
        long processed = processedEvent.incrementAndGet();
        if (processed % LOG_PROCESS_THRESHOLD == 0) {
            try {
                session.getRemote().sendString("Processed: " + processedEvent.get());
            } catch (IOException e) {
                LOGGER.warn("Cannot send process message");
            }
        }
    }

    private SimpleEvent createEvent(String message) {
        SimpleEvent event = new SimpleEvent();
        event.setBody(message.getBytes());
        return event;
    }
}
