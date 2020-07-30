package com.applitools.connectivity.api;

import com.applitools.eyes.AbstractProxySettings;
import com.applitools.eyes.Logger;

import java.net.URI;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.SimpleFormatter;

public abstract class HttpClient {

    protected final Logger eyesLogger;
    protected final int timeout;
    protected final AbstractProxySettings abstractProxySettings;
    protected boolean isClosed = false;

    protected java.util.logging.Logger communicationLogger = null;

    public HttpClient(Logger eyesLogger, int timeout, AbstractProxySettings abstractProxySettings) {
        this.eyesLogger = eyesLogger;
        this.timeout = timeout;
        this.abstractProxySettings = abstractProxySettings;
        initCommunicationLogger();
    }

    private void initCommunicationLogger() {
        if (true) {
            return;
        }

        communicationLogger = java.util.logging.Logger.getLogger("Communication Logger");
        communicationLogger.setLevel(Level.FINE);
        ConsoleHandler handler = new ConsoleHandler();
        handler.setLevel(Level.FINE);
        handler.setFormatter(new SimpleFormatter());
        communicationLogger.addHandler(handler);
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

    public Logger getLogger() {
        return eyesLogger;
    }

    public abstract void close();

    public boolean isClosed() {
        return isClosed;
    }
}
