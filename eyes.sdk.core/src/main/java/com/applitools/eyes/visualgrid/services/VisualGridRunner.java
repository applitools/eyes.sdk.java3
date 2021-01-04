package com.applitools.eyes.visualgrid.services;

import com.applitools.connectivity.ServerConnector;
import com.applitools.eyes.*;
import com.applitools.eyes.services.VisualGridServiceRunner;
import com.applitools.eyes.visualgrid.model.FrameData;
import com.applitools.eyes.visualgrid.model.IDebugResourceWriter;
import com.applitools.eyes.visualgrid.model.RGridResource;
import com.applitools.eyes.visualgrid.model.RenderingInfo;

import java.util.*;

public class VisualGridRunner extends EyesRunner {
    VisualGridServiceRunner serviceRunner;
    final Set<IEyes> allEyes = Collections.synchronizedSet(new HashSet<IEyes>());
    private final Map<String, RGridResource> resourcesCacheMap = Collections.synchronizedMap(new HashMap<String, RGridResource>());

    private RenderingInfo renderingInfo;
    private IDebugResourceWriter debugResourceWriter;

    public VisualGridRunner() {
        this(Thread.currentThread().getStackTrace()[2].getClassName());
    }

    public VisualGridRunner(String suiteName) {
        super(suiteName);
        init();
    }

    public VisualGridRunner(int testConcurrency) {
        this(testConcurrency, Thread.currentThread().getStackTrace()[2].getClassName());
    }

    public VisualGridRunner(int testConcurrency, String suiteName) {
        super(testConcurrency, suiteName);
        init();
    }

    public VisualGridRunner(RunnerOptions runnerOptions) {
        this(runnerOptions, Thread.currentThread().getStackTrace()[2].getClassName());
    }

    public VisualGridRunner(RunnerOptions runnerOptions, String suiteName) {
        super(runnerOptions, suiteName);
        init();
        if (runnerOptions.isAutProxySet()) {
            serviceRunner.setAutProxy(runnerOptions.getAutProxy());
        } else {
            if (runnerOptions.getProxy() != null) {
                serviceRunner.setAutProxy(runnerOptions.getProxy());
            }
        }
        setProxy(runnerOptions.getProxy());
    }

    private void init() {
        serviceRunner = new VisualGridServiceRunner(logger, serverConnector, allEyes, testConcurrency.actualConcurrency, debugResourceWriter, resourcesCacheMap);
        serviceRunner.start();
    }

    public void open(IEyes eyes, List<RunningTest> newTests) {
        if (renderingInfo == null) {
            renderingInfo = serverConnector.getRenderInfo();
        }

        serviceRunner.setRenderingInfo(renderingInfo);
        if (allEyes.isEmpty()) {
            this.setLogger(eyes.getLogger());
        }
        synchronized (allEyes) {
            allEyes.add(eyes);
        }

        sendConcurrencyLog();
        this.addBatch(eyes.getBatchId(), eyes.getBatchCloser());
        serviceRunner.openTests(newTests);
    }

    public synchronized void check(FrameData domData, List<CheckTask> checkTasks) {
        serviceRunner.addResourceCollectionTask(domData, checkTasks);
        logMemoryUsage();
    }

    public TestResultsSummary getAllTestResultsImpl(boolean throwException) {
        synchronized (allEyes) {
            for (IEyes eyes : allEyes) {
                // Closing all tests that the user didn't close
                eyes.closeAsync();
            }
        }

        boolean isRunning = true;
        while (isRunning && getError() == null) {
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

        if (getError() != null) {
            throw new EyesException("Execution crashed", getError());
        }

        serviceRunner.stopServices();

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

    public Throwable getError() {
        return serviceRunner.getError();
    }

    public void setDebugResourceWriter(IDebugResourceWriter debugResourceWriter) {
        this.debugResourceWriter = debugResourceWriter;
        serviceRunner.setDebugResourceWriter(debugResourceWriter);
    }

    public IDebugResourceWriter getDebugResourceWriter() {
        return this.debugResourceWriter;
    }

    public void setLogger(Logger logger) {
        serviceRunner.setLogger(logger);
        this.logger = logger;
    }

    @Override
    public void setServerConnector(ServerConnector serverConnector) {
        super.setServerConnector(serverConnector);
        serviceRunner.setServerConnector(serverConnector);
    }

    public void setProxy(AbstractProxySettings proxySettings) {
        super.setProxy(proxySettings);
        if (proxySettings != null) {
            serviceRunner.setAutProxy(proxySettings);
        }
    }

    public Map<String, RGridResource> getResourcesCacheMap() {
        return resourcesCacheMap;
    }
}
