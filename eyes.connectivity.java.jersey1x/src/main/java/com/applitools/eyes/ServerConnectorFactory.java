package com.applitools.eyes;

import java.net.URI;

/**
 * Encapsulates creation of a connectivity provider.
 */
public class ServerConnectorFactory {
    /***
     *
     * @param logger A logger instance.
     * @param sdkName An identifier for the current agent. Can be any string.
     * @param serverUrl The URI of the Eyes server.
     */
    static ServerConnector create(Logger logger, String sdkName, URI
            serverUrl) {
        return new Jersey1xServerConnector(logger, sdkName, serverUrl);
    }
}
