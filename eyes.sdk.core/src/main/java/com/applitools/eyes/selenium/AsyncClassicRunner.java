package com.applitools.eyes.selenium;

import com.applitools.connectivity.ServerConnector;
import com.applitools.eyes.*;
import com.applitools.eyes.visualgrid.services.CheckTask;
import com.applitools.eyes.visualgrid.services.RunnerOptions;

import java.util.*;

public class AsyncClassicRunner extends EyesRunner implements IClassicRunner {

    private ClassicServiceRunner serviceRunner;
    protected final Map<String, RunningTest> runningTests = Collections.synchronizedMap(new HashMap<String, RunningTest>());

    public AsyncClassicRunner() {
        this(Thread.currentThread().getStackTrace()[2].getClassName());
    }

    public AsyncClassicRunner(String suiteName) {
        super(suiteName);
        init();
    }

    public AsyncClassicRunner(RunnerOptions runnerOptions) {
        this(runnerOptions, Thread.currentThread().getStackTrace()[2].getClassName());
    }

    public AsyncClassicRunner(RunnerOptions runnerOptions, String suiteName) {
        super(runnerOptions, suiteName);
        init();
        setProxy(runnerOptions.getProxy());
    }

    protected void init() {
        serviceRunner = new ClassicServiceRunner(logger, serverConnector, runningTests, testConcurrency.actualConcurrency);
        serviceRunner.start();
    }

    public void open(RunningTest runningTest) {
        sendConcurrencyLog();
        addBatch(runningTest.getConfiguration().getBatch().getId(), runningTest);
        serviceRunner.addTest(runningTest);
    }

    public void check(CheckTask checkTask) {
        serviceRunner.addCheckTask(checkTask);
    }

    @Override
    public TestResultsSummary getAllTestResultsImpl(boolean throwException) {
        boolean isRunning = true;
        while (isRunning && getError() == null) {
            isRunning = false;
            synchronized (runningTests) {
                for (RunningTest runningTest : runningTests.values()) {
                    isRunning = isRunning || !runningTest.isCompleted();
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
        synchronized (runningTests) {
            for (RunningTest runningTest : runningTests.values()) {
                TestResultContainer result = runningTest.getTestResultContainer();
                if (exception == null && result.getException() != null) {
                    exception = result.getException();
                }

                allResults.add(result);
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

    @Override
    public void setServerConnector(ServerConnector serverConnector) {
        super.setServerConnector(serverConnector);
        serviceRunner.setServerConnector(serverConnector);
    }
}
