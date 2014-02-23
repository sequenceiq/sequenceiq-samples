package com.sequenceiq.samples.flume.source;

import com.sequenceiq.samples.flume.server.JettyWebSocketServer;
import org.apache.flume.Context;
import org.apache.flume.FlumeException;
import org.apache.flume.source.AbstractEventDrivenSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.apache.flume.conf.Configurables.ensureRequiredNonNull;

/**
 * @author keyki
 */
public class JettyWebSocketSource extends AbstractEventDrivenSource {

    private static final Logger LOGGER = LoggerFactory.getLogger(JettyWebSocketSource.class);
    private static final String HOST_KEY = "host";
    private static final String PORT_KEY = "port";
    private static final String PATH_KEY = "path";
    private static final String SSL_KEY = "ssl";
    private static final String KEYSTORE_KEY = "keystore";
    private static final String KEYSTORE_PASSWORD_KEY = "keystore-password";

    private String host;
    private String path;
    private int port;
    private boolean enableSsl;
    private String keystore;
    private String keystorePassword;

    @Override
    protected void doConfigure(Context context) throws FlumeException {
        ensureRequiredNonNull(context, HOST_KEY, PORT_KEY, PATH_KEY);

        this.host = context.getString(HOST_KEY);
        this.port = context.getInteger(PORT_KEY);
        this.path = context.getString(PATH_KEY);
        this.enableSsl = context.getBoolean(SSL_KEY, false);
        this.keystore = context.getString(KEYSTORE_KEY);
        this.keystorePassword = context.getString(KEYSTORE_PASSWORD_KEY);

        if (enableSsl) {
            checkNotNull(keystore, KEYSTORE_KEY + " must be specified when SSL is enabled");
            checkNotNull(keystorePassword, KEYSTORE_PASSWORD_KEY + " must be specified when SSL is enabled");
        }
    }

    @Override
    protected void doStart() throws FlumeException {
        try {
            LOGGER.info("Starting jetty server..");
            JettyWebSocketServer server = new JettyWebSocketServer(host, port, path, getChannelProcessor());
            if (enableSsl) {
                server.startSSL(keystore, keystorePassword);
            } else {
                server.start();
            }
        } catch (Exception e) {
            LOGGER.error("Error starting jetty server", e);
            throw new FlumeException(e);
        }
    }

    @Override
    protected void doStop() throws FlumeException {
    }
}
