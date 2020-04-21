package com.applitools.connectivity.api;

import com.applitools.eyes.AbstractProxySettings;

import java.net.URI;

public abstract class HttpClient {

    protected final int timeout;
    protected final AbstractProxySettings abstractProxySettings;

    public HttpClient(int timeout, AbstractProxySettings abstractProxySettings) {
        this.timeout = timeout;
        this.abstractProxySettings = abstractProxySettings;
    }

    /**
     * Creates a new web resource target.
     * @param baseUrl The base url of the server.
     * @return The created target
     */
    public abstract ConnectivityTarget target(URI baseUrl);

    /**
     * Creates a new web resource target.
     * @param path The base url of the server.
     * @return The created target
     */
    public abstract ConnectivityTarget target(String path);

    public AbstractProxySettings getProxySettings() {
        return abstractProxySettings;
    }

    public int getTimeout() {
        return timeout;
    }

    public abstract void close();
}
