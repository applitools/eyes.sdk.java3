package com.applitools.eyes;

public class WebDriverProxySettings {
    private String url;
    private String username;
    private String password;

    public WebDriverProxySettings(String url) {
        this.url = url;
        this.username = null;
        this.password = null;
    }

    public WebDriverProxySettings(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
    }

    public WebDriverProxySettings() { }

    public WebDriverProxySettings setUrl(String url) {
        this.url = url;
        return this;
    }

    public String getUrl() {
        return url;
    }

    public String getUsername() {
        return username;
    }

    public WebDriverProxySettings setUsername(String username) {
        this.username = username;
        return this;
    }

    public String getPassword() {
        return password;
    }

    public WebDriverProxySettings setPassword(String password) {
        this.password = password;
        return this;
    }
}
