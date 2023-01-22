package com.applitools.eyes.playwright;

import com.applitools.eyes.EyesRunner;
import com.applitools.eyes.exceptions.StaleElementReferenceException;
import com.applitools.eyes.playwright.universal.PSDKListener;
import com.applitools.eyes.playwright.universal.PlaywrightStaleElementReferenceException;
import com.applitools.eyes.playwright.universal.Refer;
import com.applitools.eyes.playwright.universal.driver.SpecDriverPlaywright;
import com.applitools.utils.ClassVersionGetter;

public class ClassicRunner extends EyesRunner {
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
     * spec-driver
     */
    protected static String[] COMMANDS = SpecDriverPlaywright.getMethodNames();

    /**
     * universal server listener
     */
    private static final PSDKListener listener = PSDKListener.getInstance();

    /**
     * used for instantiating Playwright Runner
     */
    public ClassicRunner() {
        this(BASE_AGENT_ID, VERSION);
    }

    /**
     * used for instantiating Playwright Runner
     */
    public ClassicRunner(String baseAgentId, String version) {
        super(baseAgentId, version, PROTOCOL, COMMANDS, listener);
        managerRef = commandExecutor.coreMakeManager("classic", null, null, baseAgentId);

    }

    @Override
    public StaleElementReferenceException getStaleElementException() {
        return new PlaywrightStaleElementReferenceException();
    }

    @Override
    protected Refer getRefer() {
        return listener.getRefer();
    }

}
