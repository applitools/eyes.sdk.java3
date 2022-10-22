package com.applitools.eyes.images;

import com.applitools.eyes.selenium.ClassicRunner;
import com.applitools.utils.ClassVersionGetter;

public class ImageRunner extends ClassicRunner {
    /**
     * name of the client sdk
     */
    protected static String BASE_AGENT_ID = "eyes.sdk.images";

    /**
     * version of the client sdk
     */
    protected static String VERSION = ClassVersionGetter.CURRENT_VERSION;

    /**
     * used for instantiating Appium Runner
     */
    public ImageRunner() {
        super(BASE_AGENT_ID, VERSION);
    }
}
