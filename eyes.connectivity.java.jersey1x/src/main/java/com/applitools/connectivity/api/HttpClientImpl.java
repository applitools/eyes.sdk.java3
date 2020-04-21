package com.applitools.connectivity.api;

import com.applitools.eyes.AbstractProxySettings;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.client.apache4.config.DefaultApacheHttpClient4Config;
import com.sun.jersey.client.urlconnection.HttpURLConnectionFactory;
import com.sun.jersey.client.urlconnection.URLConnectionClientHandler;

import javax.ws.rs.core.UriBuilder;
import java.io.IOException;
import java.net.*;

public class HttpClientImpl extends HttpClient {

    private final Client client;

    public HttpClientImpl(int timeout, final AbstractProxySettings abstractProxySettings) {
        super(timeout, abstractProxySettings);

        // Creating the client configuration
        ClientConfig clientConfig = new DefaultApacheHttpClient4Config();
        clientConfig.getProperties().put(ClientConfig.PROPERTY_CONNECT_TIMEOUT, timeout);
        clientConfig.getProperties().put(ClientConfig.PROPERTY_READ_TIMEOUT, timeout);
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
        final URI finalUri = uri;
        HttpURLConnectionFactory factory = new HttpURLConnectionFactory() {
            @Override
            public HttpURLConnection getHttpURLConnection(URL url) throws IOException {
                Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(finalUri.getHost(), finalUri.getPort()));
                return (HttpURLConnection) url.openConnection(proxy);
            }
        };

        if (abstractProxySettings.getUsername() != null && abstractProxySettings.getPassword() != null) {
            Authenticator.setDefault(new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(abstractProxySettings.getUsername(), abstractProxySettings.getPassword().toCharArray());
                }
            });
        }

        URLConnectionClientHandler clientHandler = new URLConnectionClientHandler(factory);
        client = new Client(clientHandler, clientConfig);
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

    @Override
    public ConnectivityTarget target(String path) {
        return new ConnectivityTargetImpl(client.resource(path), client.asyncResource(path));
    }

    @Override
    public void close() {
    }
}
