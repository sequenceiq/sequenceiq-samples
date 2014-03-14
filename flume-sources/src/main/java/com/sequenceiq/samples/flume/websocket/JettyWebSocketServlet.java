package com.sequenceiq.samples.flume.websocket;

import org.apache.flume.channel.ChannelProcessor;
import org.eclipse.jetty.websocket.servlet.ServletUpgradeRequest;
import org.eclipse.jetty.websocket.servlet.ServletUpgradeResponse;
import org.eclipse.jetty.websocket.servlet.WebSocketCreator;
import org.eclipse.jetty.websocket.servlet.WebSocketServlet;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;

/**
 * @author keyki
 */
public class JettyWebSocketServlet extends WebSocketServlet {

    private final ChannelProcessor channelProcessor;

    public JettyWebSocketServlet(ChannelProcessor channelProcessor) {
        this.channelProcessor = channelProcessor;
    }

    @Override
    public void configure(WebSocketServletFactory webSocketServletFactory) {
        webSocketServletFactory.setCreator(new WebSocketCreator() {
            @Override
            public Object createWebSocket(ServletUpgradeRequest req, ServletUpgradeResponse resp) {
                return new JettyWebSocketListener(channelProcessor);
            }
        });
    }
}
