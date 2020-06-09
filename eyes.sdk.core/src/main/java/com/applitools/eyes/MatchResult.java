/*
 * Applitools SDK for Selenium integration.
 */
package com.applitools.eyes;

import com.applitools.ICheckSettings;
import com.applitools.ICheckSettingsInternal;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * The result of a window match by the agent.
 */
@JsonIgnoreProperties({"$id", "screenshot"})
public class MatchResult {

    private boolean asExpected;
    private String windowId;
    private EyesScreenshot screenshot;

    public MatchResult() {}

    public boolean getAsExpected() {
        return asExpected;
    }

    public void setAsExpected(boolean asExpected) {
        this.asExpected = asExpected;
    }

    public EyesScreenshot getScreenshot(ICheckSettings checkSettings ) {
        return screenshot;
    }

    public void setScreenshot(EyesScreenshot screenshot) {
        this.screenshot = screenshot;
    }

    public String getWindowId() {
        return windowId;
    }

    public void setWindowId(String windowId) {
        this.windowId = windowId;
    }

}