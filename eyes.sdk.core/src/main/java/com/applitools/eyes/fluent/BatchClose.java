package com.applitools.eyes.fluent;

import com.applitools.eyes.Logger;
import com.applitools.eyes.ProxySettings;
import com.applitools.utils.ArgumentGuard;
import com.applitools.utils.GeneralUtils;

import java.util.List;

public class BatchClose {
    protected final Logger logger;
    protected String serverUrl;
    protected String apiKey;
    protected ProxySettings proxySettings;

    public BatchClose() {
        this(new Logger());
    }

    public BatchClose(Logger logger) {
        this.logger = logger;
        serverUrl = GeneralUtils.getServerUrl().toString();
    }

    public BatchClose setUrl(String url) {
        ArgumentGuard.notNull(url, "url");
        serverUrl = url;
        return this;
    }

    public BatchClose setApiKey(String apiKey) {
        ArgumentGuard.notNull(apiKey, "apiKey");
        this.apiKey = apiKey;
        return this;
    }

    public BatchClose setProxy(ProxySettings proxySettings) {
        ArgumentGuard.notNull(proxySettings, "proxySettings");
        this.proxySettings = proxySettings;
        return this;
    }

    public EnabledBatchClose setBatchId(List<String> batchIds) {
        ArgumentGuard.notNull(batchIds, "batchIds");
        ArgumentGuard.notContainsNull(batchIds, "batchIds");
        EnabledBatchClose enabledBatchClose = new EnabledBatchClose(logger, serverUrl, batchIds);
        if (apiKey != null) {
            enabledBatchClose.setApiKey(apiKey);
        }

        if (proxySettings != null) {
            enabledBatchClose.setProxy(proxySettings);
        }

        return enabledBatchClose;
    }
}
