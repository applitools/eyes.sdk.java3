package com.applitools.eyes;

import com.applitools.connectivity.ServerConnector;
import com.applitools.utils.GeneralUtils;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public abstract class EyesRunner {
    protected ServerConnector serverConnector = new ServerConnector();
    private TestResultsSummary allTestResults = null;

    private boolean dontCloseBatches = false;

    protected Logger logger = new IdPrintingLogger("n/a");

    private final Map<String, IBatchCloser> batchesServerConnectorsMap = new HashMap<>();

    public abstract TestResultsSummary getAllTestResultsImpl(boolean shouldThrowException);

    public TestResultsSummary getAllTestResults() {
        return getAllTestResults(true);
    }

    public TestResultsSummary getAllTestResults(boolean shouldThrowException) {
        logger.verbose("enter");
        if (allTestResults != null) {
            logger.log("WARNING: getAllTestResults called more than once");
            return allTestResults;
        }

        try {
            allTestResults = getAllTestResultsImpl(shouldThrowException);
        } finally {
            deleteAllBatches();
        }

        serverConnector.closeConnector();
        logger.getLogHandler().close();
        return allTestResults;
    }

    private void deleteAllBatches() {
        if (dontCloseBatches) {
            return;
        }

        boolean dontCloseBatchesStr = GeneralUtils.getDontCloseBatches();
        if (dontCloseBatchesStr) {
            logger.log("APPLITOOLS_DONT_CLOSE_BATCHES environment variable set to true. Skipping batch close.");
            return;
        }

        logger.verbose(String.format("Deleting %d batches", batchesServerConnectorsMap.size()));
        for (String batch : batchesServerConnectorsMap.keySet()) {
            IBatchCloser connector = batchesServerConnectorsMap.get(batch);
            connector.closeBatch(batch);
        }
    }

    public void setLogHandler(LogHandler logHandler) {
        logger.setLogHandler(logHandler);
        if (!logHandler.isOpen()) {
            logHandler.open();
        }
    }

    public void setDontCloseBatches(boolean dontCloseBatches) {
        this.dontCloseBatches = dontCloseBatches;
    }

    public Logger getLogger() {
        return this.logger;
    }

    public void addBatch(String batchId, IBatchCloser batchCloser) {
        if (!batchesServerConnectorsMap.containsKey(batchId)) {
            batchesServerConnectorsMap.put(batchId, batchCloser);
        }
    }

    protected static class IdPrintingLogger extends Logger {
        protected final String runnerId = UUID.randomUUID().toString();
        protected final String suiteName;

        public IdPrintingLogger(String suiteName) {
            this.suiteName = suiteName;
        }

        @Override
        protected int getMethodsBack() {
            return 4;
        }

        @Override
        public String getPrefix() {
            return super.getPrefix() + suiteName + " (runnerId: " + runnerId + ") ";
        }
    }

    public void setServerUrl(String serverUrl) {
        if (serverUrl != null) {
            if (serverConnector.getServerUrl().equals(GeneralUtils.getServerUrl())) {
                try {
                    serverConnector.setServerUrl(new URI(serverUrl));
                } catch (URISyntaxException e) {
                    GeneralUtils.logExceptionStackTrace(logger, e);
                }
            } else if (!serverConnector.getServerUrl().toString().equals(serverUrl)) {
                throw new EyesException(String.format("Server url was already set to %s", serverConnector.getServerUrl()));
            }
        }
    }

    public String getServerUrl() {
        return serverConnector.getServerUrl().toString();
    }

    public String getApiKey() {
        return serverConnector.getApiKey();
    }

    public void setApiKey(String apiKey) {
        if (apiKey != null) {
            if (!serverConnector.wasApiKeySet()) {
                serverConnector.setApiKey(apiKey);
            } else if (!serverConnector.getApiKey().equals(apiKey)) {
                throw new EyesException(String.format("Api key was already set to %s", serverConnector.getApiKey()));
            }
        }
    }

    public void setServerConnector(ServerConnector serverConnector) {
        this.serverConnector = serverConnector;
    }

    public ServerConnector getServerConnector() {
        return serverConnector;
    }

    public void setProxy(AbstractProxySettings proxySettings) {
        if (proxySettings != null) {
            if (serverConnector.getProxy() == null) {
                serverConnector.setProxy(proxySettings);
            } else if (!serverConnector.getProxy().equals(proxySettings)) {
                throw new EyesException("Proxy was already set");
            }
        }
    }

    public AbstractProxySettings getProxy() {
        return serverConnector.getProxy();
    }

    public void setAgentId(String agentId) {
        if (agentId != null) {
            serverConnector.setAgentId(agentId);
        }
    }

    public String getAgentId() {
        return serverConnector.getAgentId();
    }
}
