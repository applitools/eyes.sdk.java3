package com.applitools.eyes.playwright.visualgrid;

import com.applitools.eyes.*;
import com.applitools.eyes.playwright.universal.PSDKListener;
import com.applitools.eyes.playwright.universal.PlaywrightStaleElementReferenceException;
import com.applitools.eyes.playwright.universal.Refer;
import com.applitools.eyes.playwright.universal.driver.SpecDriverPlaywright;
import com.applitools.eyes.settings.EyesManagerSettings;
import com.applitools.eyes.universal.ManagerType;
import com.applitools.eyes.visualgrid.services.RunnerOptions;
import com.applitools.utils.ClassVersionGetter;

/**
 * Used to manage multiple Eyes sessions when working with the Ultrafast Grid
 */
public class VisualGridRunner extends EyesRunner {

    /**
     * name of the client sdk
     */
    protected static String BASE_AGENT_ID = "eyes.playwright.java";

    /**
     * version of the client sdk
     */
    protected static String VERSION = ClassVersionGetter.CURRENT_VERSION;

    /**
     * spec-driver commands
     */
    protected static String[] COMMANDS = SpecDriverPlaywright.getMethodNames();

    /**
     * webSocket listener
     */
    private static final PSDKListener listener = PSDKListener.getInstance();

    static final int DEFAULT_CONCURRENCY = 5;
    private boolean isDisabled;
    private RunnerOptions runnerOptions;

    public VisualGridRunner() {
        this(new RunnerOptions().testConcurrency(DEFAULT_CONCURRENCY));
    }

    public VisualGridRunner(int testConcurrency) {
        super(BASE_AGENT_ID, VERSION, null, COMMANDS, listener);
        this.runnerOptions = new RunnerOptions().testConcurrency(testConcurrency);
        EyesManagerSettings managerSettings = new EyesManagerSettings(null, testConcurrency, null);
        managerRef = commandExecutor.coreMakeManager(ManagerType.VISUAL_GRID.value, managerSettings);
    }

    public VisualGridRunner(RunnerOptions runnerOptions) {
        super(BASE_AGENT_ID, VERSION, null, COMMANDS, listener, runnerOptions);
        this.runnerOptions = runnerOptions;
        int testConcurrency = runnerOptions.getTestConcurrency() == null ? DEFAULT_CONCURRENCY : runnerOptions.getTestConcurrency();
        EyesManagerSettings managerSettings = new EyesManagerSettings(testConcurrency, null, null);
        managerRef = commandExecutor.coreMakeManager(ManagerType.VISUAL_GRID.value, managerSettings);
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
