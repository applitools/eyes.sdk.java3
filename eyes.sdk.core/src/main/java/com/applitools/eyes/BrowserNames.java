package com.applitools.eyes;

import com.applitools.eyes.selenium.BrowserType;

public class BrowserNames {
    public final static String EDGE = "Edge";
    public final static String IE = "IE";
    public final static String FIREFOX = "Firefox";
    public final static String CHROME = "Chrome";
    public final static String SAFARI = "Safari";
    public final static String CHROMIUM = "Chromium";
    public static final String EDGE_CHROMIUM = "Edge Chromium";

    public static String getBrowserName(BrowserType browserType) {
        if (browserType == null) {
            browserType = BrowserType.CHROME;
        }
        switch (browserType) {
            case CHROME:
            case CHROME_ONE_VERSION_BACK:
            case CHROME_TWO_VERSIONS_BACK:
                return CHROME;

            case FIREFOX:
            case FIREFOX_ONE_VERSION_BACK:
            case FIREFOX_TWO_VERSIONS_BACK:
                return FIREFOX;

            case SAFARI:
            case SAFARI_ONE_VERSION_BACK:
            case SAFARI_TWO_VERSIONS_BACK:
                return SAFARI;

            case IE_10:
                return IE + " 10";
            case IE_11:
                return IE + " 11";

            case EDGE_LEGACY:
            case EDGE:
                return EDGE;

            case EDGE_CHROMIUM:
            case EDGE_CHROMIUM_ONE_VERSION_BACK:
                return EDGE_CHROMIUM;
        }
        return null;
    }
}
