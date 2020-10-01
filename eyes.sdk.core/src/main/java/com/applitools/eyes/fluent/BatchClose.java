package com.applitools.eyes.fluent;

import com.applitools.connectivity.ServerConnector;
import com.applitools.utils.ArgumentGuard;
import com.applitools.utils.GeneralUtils;

import java.util.List;

public class BatchClose {

    public class EnableBatchClose {
        ServerConnector serverConnector;
        private final List<String> batchIds;
        private EnableBatchClose(List<String> batchIds) {
            this.serverConnector = new ServerConnector();
            this.batchIds = batchIds;
        }

        public void close() {
            for (String batchId : batchIds) {
                serverConnector.closeBatch(batchId, true, serverUrl);
            }
        }
    }

    private String serverUrl;

    public BatchClose() {
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
