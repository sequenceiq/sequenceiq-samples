package com.sequenceiq.samples.flume.server;

import org.apache.flume.channel.ChannelProcessor;
import org.eclipse.jetty.server.*;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.ssl.SslContextFactory;

/**
 * @author keyki
 */
public class JettyWebSocketServer implements WebSocketServer {

    private final String host;
    private final int port;
    private final String path;
    private final ChannelProcessor channelProcessor;

    public JettyWebSocketServer(String host, int port, String path, ChannelProcessor channelProcessor) {
        this.host = host;
        this.port = port;
        this.path = path;
        this.channelProcessor = channelProcessor;
    }

    @Override
    public void start() throws Exception {
        Server server = new Server();

        ServerConnector http = new ServerConnector(server, new HttpConnectionFactory(new HttpConfiguration()));
        http.setHost(host);
        http.setPort(port);
        server.setConnectors(new Connector[]{http});

        configureContextHandler(server);
        startServer(server);
    }

    private void configureContextHandler(Server server) {
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.addServlet(new ServletHolder(new JettyWebSocketServlet(channelProcessor)), path);
        context.setContextPath("/");
        server.setHandler(context);
    }

    @Override
    public void startSSL(String keyStoreLocation, String keyStorePassword) throws Exception {
        Server server = new Server();

        HttpConfiguration httpsConfig = new HttpConfiguration();
        httpsConfig.addCustomizer(new SecureRequestCustomizer());
        SslContextFactory sslContextFactory = new SslContextFactory();
        sslContextFactory.setKeyStorePath(keyStoreLocation);
        sslContextFactory.setKeyStorePassword(keyStorePassword);
        ServerConnector https = new ServerConnector(server,
                new SslConnectionFactory(sslContextFactory, "http/1.1"),
                new HttpConnectionFactory(httpsConfig));
        https.setHost(host);
        https.setPort(port);
        server.setConnectors(new Connector[]{https});

        configureContextHandler(server);
        startServer(server);
    }

    private void startServer(Server server) throws Exception {
        server.start();
        server.join();
    }
}
