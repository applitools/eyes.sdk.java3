package com.applitools.eyes;

public class WebdriverProxy {
    private String url;
    private Boolean tunnel;

    public WebdriverProxy(String url, Boolean tunnel) {
        this.url = url;
        this.tunnel = tunnel;
    }

    public WebdriverProxy(String url) {
        this(url, null);
    }

    public WebdriverProxy() {

    }

    public String getUrl() {
        return url;
    }

    public WebdriverProxy setUrl(String url) {
        this.url = url;
        return this;
    }

    public Boolean getTunnel() {
        return tunnel;
    }

    public WebdriverProxy setTunnel(Boolean tunnel) {
        this.tunnel = tunnel;
        return this;
    }
}
