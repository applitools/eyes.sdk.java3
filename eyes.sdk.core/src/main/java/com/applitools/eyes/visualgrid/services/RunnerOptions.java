package com.applitools.eyes.visualgrid.services;

import com.applitools.eyes.AbstractProxySettings;

public class RunnerOptions {

    private Integer testConcurrency = null;
    private String apiKey = null;
    private String serverUrl = null;
    private AbstractProxySettings proxy = null;

    private boolean isAutProxySet = false;
    private AbstractProxySettings autProxy = null;
    private String[] autProxyDomains = null;

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

    /**
     * Setting a separated proxy for requests made to non-eyes domains.
     * If the AUT proxy is set, it cannot be changed again for those specific requests by any means.
     * If AUT proxy is set to null, there will be no proxy for those specific requests.
     */
    public RunnerOptions autProxy(AbstractProxySettings autProxy) {
        isAutProxySet = true;
        this.autProxy = autProxy;
        return this;
    }

    /**
     * Setting a separated proxy for requests made to the given domains. Requests to other non-eyes domains will be sent without a proxy.
     * If the AUT proxy is set, it cannot be changed again for those specific requests by any means.
     * If AUT proxy is set to null, the behavior will be the same as {@link #autProxy(AbstractProxySettings)}
     */
    public RunnerOptions autProxy(AbstractProxySettings autProxy, String[] domains) {
        isAutProxySet = true;
        this.autProxy = autProxy;
        this.autProxyDomains = domains;
        return this;
    }

    public AbstractProxySettings getAutProxy() {
        return autProxy;
    }

    public String[] getAutProxyDomains() {
        return autProxyDomains;
    }

    public boolean isAutProxySet() {
        return isAutProxySet;
    }
}
