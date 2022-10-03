package com.applitools.eyes.selenium.universal.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * abstract autProxy settings dto
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AutProxyDto extends ProxyDto {

    private String[] domains;
    private String proxyMode;

    public void setDomains(String[] domains) { this.domains = domains; }

    public String[] getDomains() { return this.domains; }

    public void setAutProxyMode(String proxyMode) { this.proxyMode = proxyMode; }

    public String getAutProxyMode() { return this.proxyMode; }

}
