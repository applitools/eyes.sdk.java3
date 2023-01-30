package com.applitools.eyes.playwright.universal.dto;

import com.applitools.eyes.WebDriverProxySettings;
import com.applitools.eyes.universal.Reference;
import com.applitools.eyes.universal.dto.ITargetDto;
import com.applitools.eyes.universal.dto.ProxyDto;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.microsoft.playwright.Page;

import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Driver extends Reference implements ITargetDto {

    private ProxyDto proxy = null;

    @JsonIgnore
    private Page page;
    @JsonIgnore
    private Reference root;

    public Driver() {
        super();
        root = new Reference();
        root.setApplitoolsRefId(UUID.randomUUID().toString());
    }

    public Driver(Page page) {
        super();
        this.page = page;

        root = new Driver();
        root.setApplitoolsRefId(UUID.randomUUID().toString());
    }

    public Page getPage() {
        return page;
    }

    public void setPage(Page page) {
        this.page = page;
    }

    public Reference getRoot() {
        return root;
    }

    public void setWebDriverProxy(WebDriverProxySettings webDriverProxy) {
        if (webDriverProxy == null) {
            return;
        }
        this.proxy = new ProxyDto();
        setUrl(webDriverProxy.getUrl());
        setUsername(webDriverProxy.getUsername());
        setPassword(webDriverProxy.getPassword());
    }

    public String getUrl() {
        return proxy != null? proxy.getUrl() : null;
    }

    public void setUrl(String url) {
        if (proxy == null) {
            this.proxy = new ProxyDto();
        }
        this.proxy.setUrl(url);
    }

    public String getUsername() {
        return proxy != null? proxy.getUsername() : null;
    }

    public void setUsername(String username) {
        if (proxy == null) {
            this.proxy = new ProxyDto();
        }
        this.proxy.setUsername(username);
    }

    public String getPassword() {
        return proxy != null? proxy.getPassword() : null;
    }

    public void setPassword(String password) {
        if (proxy == null) {
            this.proxy = new ProxyDto();
        }
        this.proxy.setPassword(password);
    }
}
