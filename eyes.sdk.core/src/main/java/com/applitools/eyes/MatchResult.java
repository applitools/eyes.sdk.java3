package com.applitools.eyes;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties({"$id"})
public class MatchResult {

    private boolean asExpected;
    private String windowId;

    public MatchResult() {}

    public boolean getAsExpected() {
        return asExpected;
    }

    public void setAsExpected(boolean asExpected) {
        this.asExpected = asExpected;
    }

    public String getWindowId() {
        return windowId;
    }

    public void setWindowId(String windowId) {
        this.windowId = windowId;
    }

}