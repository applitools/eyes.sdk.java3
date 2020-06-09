package com.applitools.eyes.selenium;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum BrowserType {
    @JsonProperty("chrome-0") CHROME,
    @JsonProperty("chrome-1") CHROME_ONE_VERSION_BACK,
    @JsonProperty("chrome-2") CHROME_TWO_VERSIONS_BACK,
    @JsonProperty("firefox-0") FIREFOX,
    @JsonProperty("firefox-1") FIREFOX_ONE_VERSION_BACK,
    @JsonProperty("firefox-2") FIREFOX_TWO_VERSIONS_BACK,
    @JsonProperty("safari") SAFARI,
    @JsonProperty("safari-1") SAFARI_ONE_VERSION_BACK,
    @JsonProperty("safari-2") SAFARI_TWO_VERSIONS_BACK,
    @JsonProperty("ie10") IE_10,
    @JsonProperty("ie11") IE_11,

    /**
     * @deprecated The 'EDGE' option that is being used in your browsers' configuration will soon be deprecated.
     * Please change it to either "EDGE_LEGACY" for the legacy version or to "EDGE_CHROMIUM" for the new
     * Chromium-based version.
     */
    @Deprecated @JsonProperty("edge") EDGE,

    @JsonProperty("edgelegacy") EDGE_LEGACY,
    @JsonProperty("edgechromium") EDGE_CHROMIUM,
    @JsonProperty("edgechromium-1") EDGE_CHROMIUM_ONE_VERSION_BACK,
}
