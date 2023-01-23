package com.applitools.eyes.universal.dto;

public class EGClientSettingsDto {

    private String serverUrl;
    private String apiKey;
    private ProxyDto proxy;

    public EGClientSettingsDto(String apiKey, String serverUrl, ProxyDto proxy) {
        this.apiKey = apiKey;
        this.serverUrl = serverUrl;
        this.proxy = proxy;
    }

    public String getServerUrl() {
        return serverUrl;
    }

    public void setServerUrl(String serverUrl) {
        this.serverUrl = serverUrl;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public ProxyDto getProxy() {
        return proxy;
    }

    public void setProxy(ProxyDto proxy) {
        this.proxy = proxy;
    }
}
