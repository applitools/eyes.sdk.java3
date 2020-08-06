package com.applitools.connectivity.api;

import com.applitools.eyes.AbstractProxySettings;
import com.applitools.eyes.Logger;
import com.applitools.utils.NetworkUtils;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.client.apache4.config.DefaultApacheHttpClient4Config;
import com.sun.jersey.client.urlconnection.HTTPSProperties;
import com.sun.jersey.client.urlconnection.HttpURLConnectionFactory;
import com.sun.jersey.client.urlconnection.URLConnectionClientHandler;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.ws.rs.core.UriBuilder;
import java.io.IOException;
import java.net.*;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

public class HttpClientImpl extends HttpClient {

    private final Client client;

    public HttpClientImpl(Logger logger, int timeout, final AbstractProxySettings abstractProxySettings) {
        super(logger, timeout, abstractProxySettings);

        // Creating the client configuration
        ClientConfig clientConfig = new DefaultApacheHttpClient4Config();
        clientConfig.getProperties().put(ClientConfig.PROPERTY_CONNECT_TIMEOUT, timeout);
        clientConfig.getProperties().put(ClientConfig.PROPERTY_READ_TIMEOUT, timeout);
        try {
            SSLContext sslContext = NetworkUtils.getDisabledSSLContext();
            HTTPSProperties props = new HTTPSProperties(HttpsURLConnection.getDefaultHostnameVerifier(), sslContext);
            clientConfig.getProperties().put(HTTPSProperties.PROPERTY_HTTPS_PROPERTIES, props);
        } catch (NoSuchAlgorithmException | KeyManagementException ignored) {}

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
        return new ConnectivityTargetImpl(client.resource(baseUrl), client.asyncResource(baseUrl), logger);
    }

    @Override
    public ConnectivityTarget target(String path) {
        return new ConnectivityTargetImpl(client.resource(path), client.asyncResource(path), logger);
    }

    @Override
    public void close() {
        isClosed = true;
    }
}
