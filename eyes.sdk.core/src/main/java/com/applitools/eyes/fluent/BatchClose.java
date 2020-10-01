package com.applitools.eyes.fluent;

import com.applitools.connectivity.ServerConnector;
import com.applitools.eyes.Logger;
import com.applitools.utils.ArgumentGuard;
import com.applitools.utils.GeneralUtils;

import java.util.List;

public class BatchClose {

    public class EnableBatchClose {
        ServerConnector serverConnector;
        private final List<String> batchIds;
        private EnableBatchClose(List<String> batchIds) {
            this.serverConnector = new ServerConnector(logger);
            this.batchIds = batchIds;
        }

        public void close() {
            logger.verbose(String.format("Closing %d batches", batchIds.size()));
            for (String batchId : batchIds) {
                serverConnector.closeBatch(batchId, true, serverUrl);
            }
        }
    }

    private final Logger logger;
    private String serverUrl;

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

    public EnableBatchClose setBatchId(List<String> batchIds) {
        ArgumentGuard.notNull(batchIds, "batchIds");
        ArgumentGuard.notContainsNull(batchIds, "batchIds");
        return new EnableBatchClose(batchIds);
    }
}
