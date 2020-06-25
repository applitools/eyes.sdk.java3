package com.applitools.eyes.selenium;

import com.applitools.eyes.RectangleSize;

/**
 * Soon to be deprecated. Please use {@link com.applitools.eyes.config.Configuration}
 */
public class Configuration extends com.applitools.eyes.config.Configuration {

    public Configuration() {
        super();
    }

    public Configuration(com.applitools.eyes.config.Configuration configuration) {
        super(configuration);
    }

    public Configuration(RectangleSize viewportSize) {
        super(viewportSize);
    }

    public Configuration(String testName) {
        super(testName);
    }

    public Configuration(String appName, String testName, RectangleSize viewportSize) {
        super(appName, testName, viewportSize);
    }
}
