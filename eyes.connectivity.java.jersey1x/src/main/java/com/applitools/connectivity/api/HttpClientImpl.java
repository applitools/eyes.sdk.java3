package com.applitools.connectivity.api;

import com.applitools.eyes.AbstractProxySettings;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.client.apache4.ApacheHttpClient4;
import com.sun.jersey.client.apache4.config.ApacheHttpClient4Config;
import com.sun.jersey.client.apache4.config.DefaultApacheHttpClient4Config;

import javax.ws.rs.core.UriBuilder;
import java.net.URI;

public class HttpClientImpl extends HttpClient {

    private final Client client;

    public HttpClientImpl(int timeout, AbstractProxySettings abstractProxySettings) {
        super(timeout, abstractProxySettings);

        // Creating the client configuration
        ApacheHttpClient4Config clientConfig = new DefaultApacheHttpClient4Config();
        clientConfig.getProperties().put(ApacheHttpClient4Config.PROPERTY_CONNECT_TIMEOUT, timeout);
        clientConfig.getProperties().put(ApacheHttpClient4Config.PROPERTY_READ_TIMEOUT, timeout);
        if (abstractProxySettings == null) {
            client = Client.create(clientConfig);
            return;
        }

        URI uri = URI.create(abstractProxySettings.getUri());
        UriBuilder uriBuilder = UriBuilder.fromUri(uri);
        boolean changed = false;
        if (uri.getScheme() == null && uri.getHost() == null && uri.getPath() != null) {
            uriBuilder.scheme("http");
            uriBuilder.host(uri.getPath());
            uriBuilder.replacePath(null);
            changed = true;
        }
        if (uri.getPort() != abstractProxySettings.getPort()) {
            uriBuilder.port(abstractProxySettings.getPort());
            changed = true;
        }
        if (changed) {
            uri = uriBuilder.build();
        }
        clientConfig.getProperties().put(ApacheHttpClient4Config.PROPERTY_PROXY_URI, uri);
        String username = abstractProxySettings.getUsername();
        if (username != null) {
            clientConfig.getProperties().put(ApacheHttpClient4Config.PROPERTY_PROXY_USERNAME, username);
        }
        String password = abstractProxySettings.getPassword();
        if (password != null) {
            clientConfig.getProperties().put(ApacheHttpClient4Config.PROPERTY_PROXY_PASSWORD, password);
        }

        client = ApacheHttpClient4.create(clientConfig);
    }

    @Override
    public ConnectivityTarget target(URI baseUrl) {
        return new ConnectivityTargetImpl(client.resource(baseUrl), client.asyncResource(baseUrl));
    }

    @Override
    public ConnectivityTarget target(String path) {
        return new ConnectivityTargetImpl(client.resource(path), client.asyncResource(path));
    }

    @Override
    public void close() {
    }
}
