package com.applitools.connectivity.api;

import com.applitools.eyes.AbstractProxySettings;
import org.glassfish.jersey.apache.connector.ApacheConnectorProvider;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.ClientProperties;
import org.glassfish.jersey.client.RequestEntityProcessing;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import java.net.URI;

public class HttpClientImpl implements HttpClient {

    private final Client client;
    private final AbstractProxySettings abstractProxySettings;
    private final int timeout;

    public HttpClientImpl(int timeout, AbstractProxySettings abstractProxySettings) {
        this.abstractProxySettings = abstractProxySettings;
        this.timeout = timeout;

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

        client = ClientBuilder.newBuilder().withConfig(clientConfig).build();
    }

    @Override
    public Target target(URI baseUrl) {
        return new TargetImpl(client.target(baseUrl));
    }

    @Override
    public Target target(String path) {
        return new TargetImpl(client.target(path));
    }

    @Override
    public AbstractProxySettings getProxySettings() {
        return abstractProxySettings;
    }

    @Override
    public int getTimeout() {
        return timeout;
    }
}
