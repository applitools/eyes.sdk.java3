package com.applitools.eyes.appium;

import com.applitools.eyes.selenium.ClassicRunner;
import com.applitools.eyes.visualgrid.services.RunnerOptions;
import com.applitools.utils.ClassVersionGetter;

import java.util.Optional;

public class AppiumRunner extends ClassicRunner {
    /**
     * name of the client sdk
     */
    protected static String BASE_AGENT_ID = "eyes.sdk.appium";

    /**
     * version of the client sdk
     */
    protected static String VERSION = ClassVersionGetter.CURRENT_VERSION;

    /**
     * used for instantiating Appium Runner
     */
    public AppiumRunner() {
        super(BASE_AGENT_ID, VERSION, null);
    }

    public AppiumRunner(RunnerOptions runnerOptions) {
        super(BASE_AGENT_ID, VERSION, Optional.ofNullable(runnerOptions)
                .map(RunnerOptions::getLogHandler)
                .orElse(null));
    }

}
