package com.sequenceiq.samples.flume.server;

import org.apache.flume.channel.ChannelProcessor;
import org.eclipse.jetty.websocket.servlet.*;

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
