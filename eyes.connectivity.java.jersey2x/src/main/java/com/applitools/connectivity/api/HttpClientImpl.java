package com.applitools.connectivity.api;

import com.applitools.eyes.AbstractProxySettings;
import com.applitools.eyes.Logger;
import com.applitools.utils.NetworkUtils;
import org.glassfish.jersey.apache.connector.ApacheConnectorProvider;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.ClientProperties;
import org.glassfish.jersey.client.RequestEntityProcessing;

import javax.net.ssl.SSLContext;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import java.net.URI;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

public class HttpClientImpl extends HttpClient {

    private final Client client;

    public HttpClientImpl(Logger logger, int timeout, AbstractProxySettings abstractProxySettings) {
        super(logger, timeout, abstractProxySettings);

        // Creating the client configuration
        ClientConfig clientConfig = new ClientConfig();
        clientConfig.property(ClientProperties.CONNECT_TIMEOUT, timeout);
        clientConfig.property(ClientProperties.READ_TIMEOUT, timeout);
        if (abstractProxySettings != null) {
            // URI is mandatory.
            clientConfig = clientConfig.property(ClientProperties.PROXY_URI, abstractProxySettings.getUri());
            // username/password are optional
            if (abstractProxySettings.getUsername() != null) {
                clientConfig = clientConfig.property(ClientProperties.PROXY_USERNAME, abstractProxySettings.getUsername());
            }
            if (abstractProxySettings.getPassword() != null) {
                clientConfig = clientConfig.property(ClientProperties.PROXY_PASSWORD, abstractProxySettings.getPassword());
            }
        }

        // This tells the connector NOT to use "chunked encoding" ,
        // since Eyes server does not handle it.
        clientConfig.property(ClientProperties.REQUEST_ENTITY_PROCESSING, RequestEntityProcessing.BUFFERED);

        // We must use the Apache connector, since Jersey's default connector
        // does not support proxy settings.
        clientConfig.connectorProvider(new ApacheConnectorProvider());

        ClientBuilder builder = ClientBuilder.newBuilder().withConfig(clientConfig);
        try {
            SSLContext sslContext = NetworkUtils.getDisabledSSLContext();
            builder.sslContext(sslContext);
        } catch (NoSuchAlgorithmException | KeyManagementException ignored) {}
        client = builder.build();
    }

    @Override
    public ConnectivityTarget target(URI baseUrl) {
        return new ConnectivityTargetImpl(client.target(baseUrl), logger);
    }

    @Override
    public ConnectivityTarget target(String path) {
        return new ConnectivityTargetImpl(client.target(path), logger);
    }

    @Override
    public void close() {
        client.close();
        isClosed = true;
    }
}
