package com.applitools.connectivity.api;

import com.applitools.eyes.AbstractProxySettings;
import com.applitools.eyes.EyesException;
import com.applitools.eyes.Logger;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.engines.ApacheHttpClient4Engine;

import java.net.URI;
import java.util.concurrent.TimeUnit;

public class HttpClientImpl extends HttpClient {

    private static final int DEFAULT_HTTP_PROXY_PORT = 80;
    private static final int DEFAULT_HTTPS_PROXY_PORT = 443;

    private final ResteasyClient client;

    public HttpClientImpl(Logger logger, int timeout, AbstractProxySettings abstractProxySettings) {
        super(logger, timeout, abstractProxySettings);

        ResteasyClientBuilder builder = new ResteasyClientBuilder();
        builder = builder.establishConnectionTimeout(timeout, TimeUnit.MILLISECONDS)
                .socketTimeout(timeout, TimeUnit.MILLISECONDS)
                .connectionPoolSize(Integer.MAX_VALUE)
                .disableTrustManager();
        if (abstractProxySettings == null) {
            client = builder.build();
            return;
        }

        // Setting the proxy configuration
        String uri = abstractProxySettings.getUri();
        String[] uriParts = uri.split(":", 3);

        // There must be at least http':'//...
        if (uriParts.length < 2) {
            throw new EyesException("Invalid proxy URI: " + uri);
        }
        String scheme = uriParts[0];
        String hostName = uriParts[1].substring(2); // remove "//" part of the hostname.;

        int port = scheme.equalsIgnoreCase("https") ? DEFAULT_HTTPS_PROXY_PORT : DEFAULT_HTTP_PROXY_PORT;

        // If a port is specified
        if (uriParts.length > 2) {
            String leftOverUri = uriParts[2];
            String[] leftOverParts = leftOverUri.split("/", 2);
            port = Integer.parseInt(leftOverParts[0]);

            // If there's a "path" part following the port
            if (leftOverParts.length == 2) {
                hostName += "/" + leftOverParts[1];
            }
        }

        client = builder.defaultProxy(hostName, port, scheme).build();

        if (abstractProxySettings.getUsername() != null) {
            Credentials credentials = new UsernamePasswordCredentials(abstractProxySettings.getUsername(),
                    abstractProxySettings.getPassword());

            ApacheHttpClient4Engine engine = (ApacheHttpClient4Engine) client.httpEngine();
            HttpContext context = new BasicHttpContext();
            engine.setHttpContext(context);

            CredentialsProvider credProvider = new BasicCredentialsProvider();
            context.setAttribute(HttpClientContext.CREDS_PROVIDER, credProvider);
            AuthScope authScope = new AuthScope(hostName, port, null, null);
            credProvider.setCredentials(authScope, credentials);
        }
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
