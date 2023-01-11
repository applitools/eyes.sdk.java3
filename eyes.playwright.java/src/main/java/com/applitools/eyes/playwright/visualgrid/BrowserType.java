package com.applitools.eyes.playwright.visualgrid;

import com.applitools.eyes.IBrowserType;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;

/**
 *
 */
public enum BrowserType implements IBrowserType {

    CHROME(IBrowserType.CHROME),
    CHROME_ONE_VERSION_BACK(IBrowserType.CHROME_ONE_VERSION_BACK),
    CHROME_TWO_VERSIONS_BACK(IBrowserType.CHROME_TWO_VERSIONS_BACK),
    FIREFOX(IBrowserType.FIREFOX),
    FIREFOX_ONE_VERSION_BACK(IBrowserType.FIREFOX_ONE_VERSION_BACK),
    FIREFOX_TWO_VERSIONS_BACK(IBrowserType.FIREFOX_TWO_VERSIONS_BACK),
    SAFARI(IBrowserType.SAFARI),
    SAFARI_ONE_VERSION_BACK(IBrowserType.SAFARI_ONE_VERSION_BACK),
    SAFARI_TWO_VERSIONS_BACK(IBrowserType.SAFARI_TWO_VERSIONS_BACK),
    SAFARI_EARLY_ACCESS(IBrowserType.SAFARI_EARLY_ACCESS),
    IE_10(IBrowserType.IE_10),
    IE_11(IBrowserType.IE_11),

    /**
     * @deprecated The 'EDGE' option that is being used in your browsers' configuration will soon be deprecated.
     * Please change it to either "EDGE_LEGACY" for the legacy version or to "EDGE_CHROMIUM" for the new
     * Chromium-based version.
     */
    EDGE(IBrowserType.EDGE),

    EDGE_LEGACY(IBrowserType.EDGE_LEGACY),
    EDGE_CHROMIUM(IBrowserType.EDGE_CHROMIUM),
    EDGE_CHROMIUM_ONE_VERSION_BACK(IBrowserType.EDGE_CHROMIUM_ONE_VERSION_BACK),
    EDGE_CHROMIUM_TWO_VERSION_BACK(IBrowserType.EDGE_CHROMIUM_TWO_VERSION_BACK);

    private final String name;

    BrowserType(String name) {
        this.name = name;
    }

    @JsonValue
    public String getName() {
        return name;
    }

    /**
     * @return the Enum representation for the given string.
     * @throws IllegalArgumentException if unknown string.
     */
    public static BrowserType fromName(String value) throws IllegalArgumentException {
        return Arrays.stream(BrowserType.values())
                .filter(v -> v.name.equalsIgnoreCase(value))
                .findFirst()
                .orElse(null);
    }

    @Override
    public String toString() {
        return "BrowserType{" +
                "name='" + name + '\'' +
                '}';
    }


}