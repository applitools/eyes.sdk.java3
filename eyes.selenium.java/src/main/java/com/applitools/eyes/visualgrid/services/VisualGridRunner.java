package com.applitools.eyes.visualgrid.services;

import com.applitools.eyes.AbstractProxySettings;
import com.applitools.eyes.AutProxySettings;
import com.applitools.eyes.EyesRunner;
import com.applitools.eyes.Logger;
import com.applitools.eyes.selenium.exceptions.StaleElementReferenceException;
import com.applitools.eyes.universal.ManagerType;
import com.applitools.utils.ArgumentGuard;
import com.applitools.utils.ClassVersionGetter;

/**
 * Used to manage multiple Eyes sessions when working with the Ultrafast Grid
 */
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

    final TestConcurrency testConcurrency;
    private boolean isDisabled;
    private RunnerOptions runnerOptions;

    /**
     * name of the client sdk
     */
    protected static String BASE_AGENT_ID = "eyes.sdk.java";

    /**
     * version of the client sdk
     */
    protected static String VERSION = ClassVersionGetter.CURRENT_VERSION;

    public VisualGridRunner() {
        this(Thread.currentThread().getStackTrace()[2].getClassName());
    }

    public VisualGridRunner(String suiteName) {
        super(BASE_AGENT_ID, VERSION);
        this.testConcurrency = new TestConcurrency();
        this.runnerOptions = new RunnerOptions().testConcurrency(testConcurrency.actualConcurrency);
        managerRef = commandExecutor.coreMakeManager(ManagerType.VISUAL_GRID.value, testConcurrency.userConcurrency, testConcurrency.actualConcurrency, BASE_AGENT_ID);
    }

    public VisualGridRunner(int testConcurrency) {
        this(testConcurrency, Thread.currentThread().getStackTrace()[2].getClassName());
    }

    public VisualGridRunner(int testConcurrency0, String suiteName) {
        super(BASE_AGENT_ID, VERSION);
        this.testConcurrency = new TestConcurrency(testConcurrency0, true);
        this.runnerOptions = new RunnerOptions().testConcurrency(testConcurrency.actualConcurrency);
        managerRef = commandExecutor.coreMakeManager(ManagerType.VISUAL_GRID.value, testConcurrency.userConcurrency, testConcurrency.actualConcurrency, BASE_AGENT_ID);
    }

    public VisualGridRunner(RunnerOptions runnerOptions) {
        this(runnerOptions, Thread.currentThread().getStackTrace()[2].getClassName());
    }

    public VisualGridRunner(RunnerOptions runnerOptions, String suiteName) {
        super(BASE_AGENT_ID, VERSION, runnerOptions);
        this.runnerOptions = runnerOptions;
        int testConcurrency0 = runnerOptions.getTestConcurrency() == null ? DEFAULT_CONCURRENCY : runnerOptions.getTestConcurrency();
        this.testConcurrency = new TestConcurrency(testConcurrency0, false);
        managerRef = commandExecutor.coreMakeManager(ManagerType.VISUAL_GRID.value, testConcurrency.userConcurrency, testConcurrency.actualConcurrency, BASE_AGENT_ID);
    }

    protected VisualGridRunner(String baseAgentId, String version) {
        super(baseAgentId, version);
        this.testConcurrency = new TestConcurrency();
        this.runnerOptions = new RunnerOptions().testConcurrency(testConcurrency.actualConcurrency);
        managerRef = commandExecutor.coreMakeManager(ManagerType.VISUAL_GRID.value, testConcurrency.userConcurrency, testConcurrency.actualConcurrency, BASE_AGENT_ID);
    }

    @Override
    public com.applitools.eyes.exceptions.StaleElementReferenceException getStaleElementException() {
        return new StaleElementReferenceException();
    }

    protected VisualGridRunner(int testConcurrency0, String baseAgentId, String version) {
        super(baseAgentId, version);
        this.testConcurrency = new TestConcurrency(testConcurrency0, true);
        this.runnerOptions = new RunnerOptions().testConcurrency(testConcurrency.actualConcurrency);
        managerRef = commandExecutor.coreMakeManager(ManagerType.VISUAL_GRID.value, testConcurrency.userConcurrency, testConcurrency.actualConcurrency, BASE_AGENT_ID);
    }

    protected VisualGridRunner(RunnerOptions runnerOptions,  String baseAgentId, String version) {
        super(baseAgentId, version);
        ArgumentGuard.notNull(runnerOptions, "runnerOptions");
        this.runnerOptions = runnerOptions;
        int testConcurrency0 = runnerOptions.getTestConcurrency() == null ? DEFAULT_CONCURRENCY : runnerOptions.getTestConcurrency();
        this.testConcurrency = new TestConcurrency(testConcurrency0, false);
        managerRef = commandExecutor.coreMakeManager(ManagerType.VISUAL_GRID.value, testConcurrency.userConcurrency, testConcurrency.actualConcurrency, BASE_AGENT_ID);
    }

    public void setLogger(Logger logger) {
        this.logger = logger;
    }

    public void setProxy(AbstractProxySettings proxySettings) {
        if (proxySettings != null)
            this.runnerOptions = this.runnerOptions.proxy(proxySettings);
    }

    public void setAutProxy(AutProxySettings autProxy) {
        this.runnerOptions = this.runnerOptions.autProxy(autProxy);
    }

    public AutProxySettings getAutProxy() { return this.runnerOptions.getAutProxy(); }

    @Override
    public AbstractProxySettings getProxy() { return this.runnerOptions.getProxy(); }

    public void setIsDisabled(boolean isDisabled) {
        this.isDisabled = isDisabled;
    }

    public boolean getIsDisabled() {
        return this.isDisabled;
    }
}
