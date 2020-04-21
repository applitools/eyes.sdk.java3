package com.applitools.eyes;

public class ProxySettings extends AbstractProxySettings {
    /**
     * @param uri  Uri should start without the scheme
     * @param port
     */
    public ProxySettings(String uri, int port, String username, String password) {
        super(uri, port, username, password);
    }

    /**
     * @param uri  Uri should start without the scheme
     * @param port
     */
    public ProxySettings(String uri, int port) {
        super(uri, port, "", "");
    }

    /**
     * @param uri Uri should start without the scheme
     */
    public ProxySettings(String uri, String username, String password) {
        super(uri, username, password);
    }

    /**
     * @param uri Uri should start without the scheme
     */
    public ProxySettings(String uri) {
        super(uri, "", "");
    }
}
