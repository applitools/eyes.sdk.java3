package com.applitools.eyes.universal.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ECClientSettingsDto {

    private String serverUrl;
    private ECCapabilities capabilities;
    private Integer port;
    private ProxyDto proxy;

    public ECClientSettingsDto(String serverUrl, ECCapabilities capabilities, Integer port, ProxyDto proxy) {
        this.serverUrl = serverUrl;
        this.capabilities = capabilities;
        this.port = port;
        this.proxy = proxy;
    }

    public String getServerUrl() {
        return serverUrl;
    }

    public void setServerUrl(String serverUrl) {
        this.serverUrl = serverUrl;
    }

    public ECCapabilities getCapabilities() {
        return capabilities;
    }

    public void setCapabilities(ECCapabilities capabilities) {
        this.capabilities = capabilities;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public ProxyDto getProxy() {
        return proxy;
    }

    public void setProxy(ProxyDto proxy) {
        this.proxy = proxy;
    }

    @Override
    public String toString() {
        return "ECClientSettingsDto{" +
                "serverUrl='" + serverUrl + '\'' +
                ", capabilities=" + capabilities +
                ", port=" + port +
                ", proxy=" + proxy +
                '}';
    }
}
