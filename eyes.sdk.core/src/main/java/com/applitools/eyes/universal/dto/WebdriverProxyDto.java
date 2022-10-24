package com.applitools.eyes.universal.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * web driver dto
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class WebdriverProxyDto {
    private String url;
    private Boolean tunnel;

    public WebdriverProxyDto(String url, Boolean tunnel) {
        this.url = url;
        this.tunnel = tunnel;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Boolean getTunnel() {
        return tunnel;
    }

    public void setTunnel(Boolean tunnel) {
        this.tunnel = tunnel;
    }
}
