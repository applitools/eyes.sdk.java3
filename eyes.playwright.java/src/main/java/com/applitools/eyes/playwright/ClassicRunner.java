package com.applitools.eyes.playwright;

import com.applitools.eyes.EyesRunner;
import com.applitools.eyes.TestResultsSummary;
import com.applitools.eyes.exceptions.StaleElementReferenceException;
import com.applitools.eyes.playwright.universal.PlaywrightStaleElementReferenceException;
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
     * used for instantiating Image Runner
     */
    public ClassicRunner() {
        this(BASE_AGENT_ID, VERSION);
    }

    /**
     * used for instantiating Image Runner
     */
    public ClassicRunner(String baseAgentId, String version) {
        super(baseAgentId, version);
        managerRef = commandExecutor.coreMakeManager("classic", null, null, baseAgentId);
    }

    @Override
    public StaleElementReferenceException getStaleElementException() {
        return new PlaywrightStaleElementReferenceException();
    }

    @Override
    public TestResultsSummary getAllTestResultsImpl(boolean shouldThrowException) {
        return null;
    }
}
