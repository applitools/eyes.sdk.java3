package com.applitools.eyes.fluent;

import com.applitools.eyes.Logger;
import com.applitools.utils.ArgumentGuard;
import com.applitools.utils.GeneralUtils;

import java.util.List;

public class BatchClose {
    protected final Logger logger;
    protected String serverUrl;

    public BatchClose() {
        this(new Logger());
    }

    public BatchClose(Logger logger) {
        this.logger = logger;
        serverUrl = GeneralUtils.getServerUrl().toString();
    }

    public BatchClose setUrl(String url) {
        serverUrl = url;
        return this;
    }

    public EnabledBatchClose setBatchId(List<String> batchIds) {
        ArgumentGuard.notNull(batchIds, "batchIds");
        ArgumentGuard.notContainsNull(batchIds, "batchIds");
        return new EnabledBatchClose(logger, serverUrl, batchIds);
    }
}
