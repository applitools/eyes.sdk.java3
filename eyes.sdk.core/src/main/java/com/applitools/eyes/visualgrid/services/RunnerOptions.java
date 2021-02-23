package com.applitools.eyes.visualgrid.services;

import com.applitools.eyes.AbstractProxySettings;

public class RunnerOptions {

    private Integer testConcurrency = null;
    private String apiKey = null;
    private String serverUrl = null;
    private AbstractProxySettings proxy = null;

    private boolean isAutProxySet = false;
    private AbstractProxySettings autProxy = null;

    public RunnerOptions testConcurrency(int testConcurrency) {
        this.testConcurrency = testConcurrency;
        return this;
    }

    public Integer getTestConcurrency() {
        return testConcurrency;
    }

    public RunnerOptions apiKey(String apiKey) {
        this.apiKey = apiKey;
        return this;
    }

    public String getApiKey() {
        return apiKey;
    }

    public RunnerOptions serverUrl(String serverUrl) {
        this.serverUrl = serverUrl;
        return this;
    }

    public String getServerUrl() {
        return serverUrl;
    }

    public RunnerOptions proxy(AbstractProxySettings proxySettings) {
        this.proxy = proxySettings;
        return this;
    }

    public AbstractProxySettings getProxy() {
        return proxy;
    }

    public RunnerOptions autProxy(AbstractProxySettings autProxy) {
        isAutProxySet = true;
        this.autProxy = autProxy;
        return this;
    }

    public AbstractProxySettings getAutProxy() {
        return autProxy;
    }

    public boolean isAutProxySet() {
        return isAutProxySet;
    }
}
