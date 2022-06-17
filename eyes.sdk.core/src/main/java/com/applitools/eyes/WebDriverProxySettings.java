package com.applitools.eyes;

public class WebDriverProxySettings {
    private String proxyUrl;

    public WebDriverProxySettings(String proxyUrl) {
        this.proxyUrl = proxyUrl;
    }

    public WebDriverProxySettings() { }

    public WebDriverProxySettings setProxyUrl(String proxyUrl) {
        this.proxyUrl = proxyUrl;
        return this;
    }

    public String getProxyUrl() {
        return proxyUrl;
    }
}
