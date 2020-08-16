package com.applitools.eyes.selenium;

import com.fasterxml.jackson.annotation.JsonValue;

public enum BrowserType {
    CHROME("chrome-0"),
    CHROME_ONE_VERSION_BACK("chrome-1"),
    CHROME_TWO_VERSIONS_BACK("chrome-2"),
    FIREFOX("firefox-0"),
    FIREFOX_ONE_VERSION_BACK("firefox-1"),
    FIREFOX_TWO_VERSIONS_BACK("firefox-2"),
    SAFARI("safari"),
    SAFARI_ONE_VERSION_BACK("safari-1"),
    SAFARI_TWO_VERSIONS_BACK("safari-2"),
    IE_10("ie10"),
    IE_11("ie11"),

    /**
     * @deprecated The 'EDGE' option that is being used in your browsers' configuration will soon be deprecated.
     * Please change it to either "EDGE_LEGACY" for the legacy version or to "EDGE_CHROMIUM" for the new
     * Chromium-based version.
     */
    EDGE("edge"),

    EDGE_LEGACY("edgelegacy"),
    EDGE_CHROMIUM("edgechromium"),
    EDGE_CHROMIUM_ONE_VERSION_BACK("edgechromium-1");

    private final String name;

    BrowserType(String name) {
        this.name = name;
    }

    @JsonValue
    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "BrowserType{" +
                "name='" + name + '\'' +
                '}';
    }
}
