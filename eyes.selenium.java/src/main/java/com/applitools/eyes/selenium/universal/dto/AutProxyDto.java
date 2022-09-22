package com.applitools.eyes.selenium.universal.dto;

/**
 * abstract autProxy settings dto
 */
public class AutProxyDto {

    private ProxyDto proxyDto;
    private String[] domains;
    private String proxyMode;

    public void setProxy(ProxyDto proxyDto) {
        this.proxyDto = proxyDto;
    }

    public ProxyDto getProxy() {
        return this.proxyDto;
    }

    public void setDomains(String[] domains) { this.domains = domains; }

    public String[] getDomains() { return this.domains; }

    public void setAutProxyMode(String proxyMode) { this.proxyMode = proxyMode; }

    public String getAutProxyMode() { return this.proxyMode; }

}
