package com.applitools.eyes.appium;

import com.applitools.eyes.selenium.ClassicRunner;
import com.applitools.utils.ClassVersionGetter;

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
        super(BASE_AGENT_ID, VERSION);
    }

}
