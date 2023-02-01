package com.applitools.eyes.visualgrid.services;

import com.applitools.eyes.AbstractProxySettings;
import com.applitools.eyes.AutProxySettings;
import com.applitools.eyes.EyesRunner;
import com.applitools.eyes.Logger;
import com.applitools.eyes.selenium.exceptions.StaleElementReferenceException;
import com.applitools.eyes.universal.ManagerType;
import com.applitools.eyes.universal.USDKListener;
import com.applitools.utils.ArgumentGuard;
import com.applitools.utils.ClassVersionGetter;

/**
 * Used to manage multiple Eyes sessions when working with the Ultrafast Grid
 */
public class VisualGridRunner extends EyesRunner {

    /**
     * name of the client sdk
     */
    protected static String BASE_AGENT_ID = "eyes.sdk.java";

    /**
     * version of the client sdk
     */
    protected static String VERSION = ClassVersionGetter.CURRENT_VERSION;

    /**
     * universal server listener
     */
    private static final USDKListener listener = USDKListener.getInstance();

    static final int DEFAULT_CONCURRENCY = 5;
    private boolean isDisabled;
    private RunnerOptions runnerOptions;

    public VisualGridRunner() {
        this(Thread.currentThread().getStackTrace()[2].getClassName());
    }

    public VisualGridRunner(String suiteName) {
        this(new RunnerOptions().testConcurrency(DEFAULT_CONCURRENCY));
    }

    public VisualGridRunner(int testConcurrency) {
        this(testConcurrency, Thread.currentThread().getStackTrace()[2].getClassName());
    }

    public VisualGridRunner(int testConcurrency, String suiteName) {
        super(BASE_AGENT_ID, VERSION, listener);
        this.runnerOptions = new RunnerOptions().testConcurrency(testConcurrency);
        managerRef = commandExecutor.coreMakeManager(ManagerType.VISUAL_GRID.value, null, testConcurrency, BASE_AGENT_ID);
    }

    public VisualGridRunner(RunnerOptions runnerOptions) {
        this(runnerOptions, Thread.currentThread().getStackTrace()[2].getClassName());
    }

    public VisualGridRunner(RunnerOptions runnerOptions, String suiteName) {
        super(BASE_AGENT_ID, VERSION, runnerOptions, listener);
        this.runnerOptions = runnerOptions;
        int testConcurrency = runnerOptions.getTestConcurrency() == null ? DEFAULT_CONCURRENCY : runnerOptions.getTestConcurrency();
        managerRef = commandExecutor.coreMakeManager(ManagerType.VISUAL_GRID.value, testConcurrency, null, BASE_AGENT_ID);
    }

    protected VisualGridRunner(String baseAgentId, String version) {
        super(baseAgentId, version, listener);
        this.runnerOptions = new RunnerOptions().testConcurrency(DEFAULT_CONCURRENCY);
        managerRef = commandExecutor.coreMakeManager(ManagerType.VISUAL_GRID.value, DEFAULT_CONCURRENCY, null, BASE_AGENT_ID);
    }

    protected VisualGridRunner(int testConcurrency, String baseAgentId, String version) {
        super(baseAgentId, version, listener);
        this.runnerOptions = new RunnerOptions().testConcurrency(testConcurrency);
        managerRef = commandExecutor.coreMakeManager(ManagerType.VISUAL_GRID.value, null, testConcurrency, BASE_AGENT_ID);
    }

    protected VisualGridRunner(RunnerOptions runnerOptions,  String baseAgentId, String version) {
        super(baseAgentId, version, listener);
        ArgumentGuard.notNull(runnerOptions, "runnerOptions");
        this.runnerOptions = runnerOptions;
        int testConcurrency = runnerOptions.getTestConcurrency() == null ? DEFAULT_CONCURRENCY : runnerOptions.getTestConcurrency();
        managerRef = commandExecutor.coreMakeManager(ManagerType.VISUAL_GRID.value, testConcurrency, null, BASE_AGENT_ID);
    }

    @Override
    public com.applitools.eyes.exceptions.StaleElementReferenceException getStaleElementException() {
        return new StaleElementReferenceException();
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
