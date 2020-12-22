package com.applitools.eyes.visualgrid.services;

import com.applitools.connectivity.ServerConnector;
import com.applitools.eyes.*;
import com.applitools.eyes.logging.TraceLevel;
import com.applitools.eyes.services.EyesServiceRunner;
import com.applitools.eyes.visualgrid.model.FrameData;
import com.applitools.eyes.visualgrid.model.IDebugResourceWriter;
import com.applitools.eyes.visualgrid.model.RGridResource;
import com.applitools.eyes.visualgrid.model.RenderingInfo;
import com.applitools.utils.GeneralUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.*;

public class VisualGridRunner extends EyesRunner {
    static class TestConcurrency {
        final int userConcurrency;
        final int actualConcurrency;
        final boolean isLegacy;
        boolean isDefault = false;

        TestConcurrency() {
            isDefault = true;
            isLegacy = false;
            userConcurrency = DEFAULT_CONCURRENCY;
            actualConcurrency = DEFAULT_CONCURRENCY;
        }

        TestConcurrency(int userConcurrency, boolean isLegacy) {
            this.userConcurrency = userConcurrency;
            this.actualConcurrency = isLegacy ? userConcurrency * CONCURRENCY_FACTOR : userConcurrency;
            this.isLegacy = isLegacy;
        }
    }

    private static final int CONCURRENCY_FACTOR = 5;
    static final int DEFAULT_CONCURRENCY = 5;

    private EyesServiceRunner eyesServiceRunner;
    final TestConcurrency testConcurrency;
    private boolean wasConcurrencyLogSent = false;
    final Set<IEyes> allEyes = Collections.synchronizedSet(new HashSet<IEyes>());
    private final Map<String, RGridResource> resourcesCacheMap = Collections.synchronizedMap(new HashMap<String, RGridResource>());

    private RenderingInfo renderingInfo;
    private IDebugResourceWriter debugResourceWriter;
    private boolean isDisabled;

    private String suiteName;

    public VisualGridRunner() {
        this(Thread.currentThread().getStackTrace()[2].getClassName());
    }

    public VisualGridRunner(String suiteName) {
        this.testConcurrency = new TestConcurrency();
        init(suiteName);
    }

    public VisualGridRunner(int testConcurrency) {
        this(testConcurrency, Thread.currentThread().getStackTrace()[2].getClassName());
    }

    public VisualGridRunner(int testConcurrency, String suiteName) {
        this.testConcurrency = new TestConcurrency(testConcurrency, true);
        init(suiteName);
    }

    public VisualGridRunner(RunnerOptions runnerOptions) {
        this(runnerOptions, Thread.currentThread().getStackTrace()[2].getClassName());
    }

    public VisualGridRunner(RunnerOptions runnerOptions, String suiteName) {
        int testConcurrency = runnerOptions.getTestConcurrency() == null ? DEFAULT_CONCURRENCY : runnerOptions.getTestConcurrency();
        this.testConcurrency = new TestConcurrency(testConcurrency, false);
        setApiKey(runnerOptions.getApiKey());
        setServerUrl(runnerOptions.getServerUrl());
        setProxy(runnerOptions.getProxy());
        init(suiteName);
    }

    private void init(String suiteName) {
        this.suiteName = suiteName;
        this.logger = new IdPrintingLogger(suiteName);
        logger.log("runner created");
        eyesServiceRunner = new EyesServiceRunner(logger, serverConnector, allEyes, testConcurrency.actualConcurrency, debugResourceWriter, resourcesCacheMap);
        eyesServiceRunner.start();
        logger.verbose("rendering grid manager is built");
    }

    public void open(IEyes eyes, List<VisualGridRunningTest> newTests) {
        logger.verbose("enter");

        setApiKey(eyes.getApiKey());
        setServerUrl(eyes.getServerUrl().toString());
        setProxy(eyes.getProxy());

        if (renderingInfo == null) {
            renderingInfo = serverConnector.getRenderInfo();
        }

        eyesServiceRunner.setRenderingInfo(renderingInfo);
        if (allEyes.isEmpty()) {
            this.setLogger(eyes.getLogger());
        }
        synchronized (allEyes) {
            allEyes.add(eyes);
        }

        try {
            String logMessage = getConcurrencyLog();
            if (logMessage != null) {
                NetworkLogHandler.sendSingleLog(serverConnector, TraceLevel.Notice, logMessage);
            }
        } catch (JsonProcessingException e) {
            GeneralUtils.logExceptionStackTrace(logger, e);
        }

        this.addBatch(eyes.getBatchId(), eyes.getBatchCloser());
        eyesServiceRunner.openTests(newTests);
    }

    public synchronized void check(FrameData domData, List<CheckTask> checkTasks) {
        eyesServiceRunner.addResourceCollectionTask(domData, checkTasks);
    }

    public TestResultsSummary getAllTestResultsImpl(boolean throwException) {
        logger.log("enter");
        boolean isRunning = true;
        while (isRunning && eyesServiceRunner.getError() == null) {
            isRunning = false;
            synchronized (allEyes) {
                for (IEyes eyes : allEyes) {
                    isRunning = isRunning || !eyes.isCompleted();
                }
            }

            try {
                Thread.sleep(500);
            } catch (InterruptedException ignored) {}
        }

        if (eyesServiceRunner.getError() != null) {
            throw new EyesException("Execution crashed", eyesServiceRunner.getError());
        }

        eyesServiceRunner.stopServices();

        Throwable exception = null;
        List<TestResultContainer> allResults = new ArrayList<>();
        synchronized (allEyes) {
            for (IEyes eyes : allEyes) {
                List<TestResultContainer> eyesResults = eyes.getAllTestResults();
                for (TestResultContainer result : eyesResults) {
                    if (exception == null && result.getException() != null) {
                        exception = result.getException();
                    }
                }

                allResults.addAll(eyesResults);
            }
        }

        if (throwException && exception != null) {
            throw new Error(exception);
        }
        return new TestResultsSummary(allResults);
    }

    public void setDebugResourceWriter(IDebugResourceWriter debugResourceWriter) {
        this.debugResourceWriter = debugResourceWriter;
        eyesServiceRunner.setDebugResourceWriter(debugResourceWriter);
    }

    public IDebugResourceWriter getDebugResourceWriter() {
        return this.debugResourceWriter;
    }

    public void setLogger(Logger logger) {
        eyesServiceRunner.setLogger(logger);
        if (this.logger == null) {
            this.logger = logger;
        } else {
            this.logger.setLogHandler(logger.getLogHandler());
        }
    }

    @Override
    public void setServerConnector(ServerConnector serverConnector) {
        super.setServerConnector(serverConnector);
        eyesServiceRunner.setServerConnector(serverConnector);
    }

    public String getConcurrencyLog() throws JsonProcessingException {
        if (wasConcurrencyLogSent) {
            return null;
        }

        wasConcurrencyLogSent = true;
        String key = testConcurrency.isDefault ? "defaultConcurrency" : testConcurrency.isLegacy ? "concurrency" : "testConcurrency";
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put("type", "runnerStarted");
        objectNode.put(key, testConcurrency.userConcurrency);
        return objectMapper.writeValueAsString(objectNode);
    }

    public Map<String, RGridResource> getResourcesCacheMap() {
        return resourcesCacheMap;
    }

    public void setIsDisabled(boolean isDisabled) {
        this.isDisabled = isDisabled;
    }

    public boolean getIsDisabled() {
        return this.isDisabled;
    }

    public String getSuiteName() {
        return suiteName;
    }

    public void setSuiteName(String suiteName) {
        this.suiteName = suiteName;
    }
}
