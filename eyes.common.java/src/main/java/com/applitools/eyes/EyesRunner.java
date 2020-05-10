package com.applitools.eyes;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public abstract class EyesRunner {

    protected Logger logger = new IdPrintingLogger("n/a");

    private Map<String, IBatchCloser> batchesServerConnectorsMap = new HashMap<>();

    public abstract TestResultsSummary getAllTestResultsImpl();

    public abstract TestResultsSummary getAllTestResultsImpl(boolean shouldThrowException);

    public TestResultsSummary getAllTestResults() {
        return getAllTestResults(true);
    }

    public TestResultsSummary getAllTestResults(boolean shouldThrowException) {
        TestResultsSummary allTestResults;
        try {
            allTestResults = getAllTestResultsImpl(shouldThrowException);
        } finally {
            deleteAllBatches();
        }
        return allTestResults;
    }

    private void deleteAllBatches() {
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
}
