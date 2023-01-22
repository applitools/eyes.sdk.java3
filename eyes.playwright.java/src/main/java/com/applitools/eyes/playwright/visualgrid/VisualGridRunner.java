package com.applitools.eyes.playwright.visualgrid;

import com.applitools.eyes.*;
import com.applitools.eyes.playwright.universal.PSDKListener;
import com.applitools.eyes.playwright.universal.PlaywrightStaleElementReferenceException;
import com.applitools.eyes.playwright.universal.Refer;
import com.applitools.eyes.playwright.universal.driver.SpecDriverPlaywright;
import com.applitools.eyes.universal.ManagerType;
import com.applitools.eyes.visualgrid.services.RunnerOptions;
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

    /**
     * name of the client sdk
     */
    protected static String BASE_AGENT_ID = "eyes.sdk.playwright";

    /**
     * version of the client sdk
     */
    protected static String VERSION = ClassVersionGetter.CURRENT_VERSION;

    /**
     * the protocol to be used
     */
    protected static String PROTOCOL = "playwright";

    /**
     * spec-driver commands
     */
    protected static String[] COMMANDS = SpecDriverPlaywright.getMethodNames();

    /**
     * webSocket listener
     */
    private static final PSDKListener listener = PSDKListener.getInstance();

    private static final int CONCURRENCY_FACTOR = 5;
    static final int DEFAULT_CONCURRENCY = 5;
    final TestConcurrency testConcurrency;

    private boolean isDisabled;
    private RunnerOptions runnerOptions;

    public VisualGridRunner() {
        super(BASE_AGENT_ID, VERSION, PROTOCOL, COMMANDS, listener);
        this.testConcurrency = new TestConcurrency();
        this.runnerOptions = new RunnerOptions().testConcurrency(testConcurrency.actualConcurrency);
        managerRef = commandExecutor.coreMakeManager(ManagerType.VISUAL_GRID.value, testConcurrency.userConcurrency, testConcurrency.actualConcurrency, BASE_AGENT_ID);
    }

    public VisualGridRunner(int testConcurrency) {
        super(BASE_AGENT_ID, VERSION, PROTOCOL, COMMANDS, listener);
        this.testConcurrency = new TestConcurrency(testConcurrency, true);
        this.runnerOptions = new RunnerOptions().testConcurrency(this.testConcurrency.actualConcurrency);
        managerRef = commandExecutor.coreMakeManager(ManagerType.VISUAL_GRID.value, this.testConcurrency.userConcurrency, this.testConcurrency.actualConcurrency, BASE_AGENT_ID);
    }

    public VisualGridRunner(RunnerOptions runnerOptions) {
        super(BASE_AGENT_ID, VERSION, PROTOCOL, COMMANDS, listener, runnerOptions);
        this.runnerOptions = runnerOptions;
        int testConcurrency0 = runnerOptions.getTestConcurrency() == null ? DEFAULT_CONCURRENCY : runnerOptions.getTestConcurrency();
        this.testConcurrency = new TestConcurrency(testConcurrency0, false);
        managerRef = commandExecutor.coreMakeManager(ManagerType.VISUAL_GRID.value, testConcurrency.userConcurrency, testConcurrency.actualConcurrency, BASE_AGENT_ID);
    }

    protected VisualGridRunner(String baseAgentId, String version) {
        super(baseAgentId, version, PROTOCOL, COMMANDS, listener);
        this.testConcurrency = new TestConcurrency();
        this.runnerOptions = new RunnerOptions().testConcurrency(testConcurrency.actualConcurrency);
        managerRef = commandExecutor.coreMakeManager(ManagerType.VISUAL_GRID.value, testConcurrency.userConcurrency, testConcurrency.actualConcurrency, BASE_AGENT_ID);
    }

    protected VisualGridRunner(int testConcurrency, String baseAgentId, String version) {
        super(baseAgentId, version, PROTOCOL, COMMANDS, listener);
        this.testConcurrency = new TestConcurrency(testConcurrency, true);
        this.runnerOptions = new RunnerOptions().testConcurrency(this.testConcurrency.actualConcurrency);
        managerRef = commandExecutor.coreMakeManager(ManagerType.VISUAL_GRID.value, this.testConcurrency.userConcurrency, this.testConcurrency.actualConcurrency, BASE_AGENT_ID);
    }

    protected VisualGridRunner(RunnerOptions runnerOptions,  String baseAgentId, String version) {
        super(baseAgentId, version, PROTOCOL, COMMANDS, listener);
        ArgumentGuard.notNull(runnerOptions, "runnerOptions");
        this.runnerOptions = runnerOptions;
        int testConcurrency0 = runnerOptions.getTestConcurrency() == null ? DEFAULT_CONCURRENCY : runnerOptions.getTestConcurrency();
        this.testConcurrency = new TestConcurrency(testConcurrency0, false);
        managerRef = commandExecutor.coreMakeManager(ManagerType.VISUAL_GRID.value, testConcurrency.userConcurrency, testConcurrency.actualConcurrency, BASE_AGENT_ID);
    }

    @Override
    public com.applitools.eyes.exceptions.StaleElementReferenceException getStaleElementException() {
        return new PlaywrightStaleElementReferenceException();
    }

    @Override
    public AbstractProxySettings getProxy() { return this.runnerOptions.getProxy(); }

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

    public void setIsDisabled(boolean isDisabled) {
        this.isDisabled = isDisabled;
    }

    public boolean getIsDisabled() {
        return this.isDisabled;
    }

    @Override
    protected Refer getRefer() {
        return listener.getRefer();
    }

}
