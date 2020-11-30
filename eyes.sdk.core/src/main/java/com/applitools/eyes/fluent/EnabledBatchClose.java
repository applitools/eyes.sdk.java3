package com.applitools.eyes.fluent;

import com.applitools.connectivity.ServerConnector;
import com.applitools.eyes.Logger;
import com.applitools.eyes.ProxySettings;
import com.applitools.utils.ArgumentGuard;

import java.util.List;

public class EnabledBatchClose extends BatchClose {
    ServerConnector serverConnector;
    private List<String> batchIds;

    EnabledBatchClose(Logger logger, String serverUrl, List<String> batchIds) {
        super(logger);
        this.serverConnector = new ServerConnector(logger);
        this.serverUrl = serverUrl;
        this.batchIds = batchIds;
    }

    @Override
    public EnabledBatchClose setUrl(String url) {
        ArgumentGuard.notNull(url, "url");
        serverUrl = url;
        return this;
    }

    @Override
    public EnabledBatchClose setApiKey(String apiKey) {
        ArgumentGuard.notNull(apiKey, "apiKey");
        this.apiKey = apiKey;
        serverConnector.setApiKey(apiKey);
        return this;
    }

    @Override
    public EnabledBatchClose setProxy(ProxySettings proxySettings) {
        ArgumentGuard.notNull(proxySettings, "proxySettings");
        this.proxySettings = proxySettings;
        serverConnector.setProxy(proxySettings);
        return this;
    }

    @Override
    public EnabledBatchClose setBatchId(List<String> batchIds) {
        ArgumentGuard.notNull(batchIds, "batchIds");
        ArgumentGuard.notContainsNull(batchIds, "batchIds");
        this.batchIds = batchIds;
        return this;
    }

    public void close() {
        logger.verbose(String.format("Closing %d batches", batchIds.size()));
        for (String batchId : batchIds) {
            serverConnector.closeBatch(batchId, serverUrl);
        }
    }
}
