package com.applitools.eyes;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SessionAccessibilityStatus {
    public enum AccessibilityStatus {
        Passed,
        Failed
    }

    @JsonProperty("status")
    private AccessibilityStatus status;
    @JsonProperty("level")
    private AccessibilityLevel level;
    @JsonProperty("version")
    private AccessibilityGuidelinesVersion version;

    public AccessibilityStatus getStatus() {
        return status;
    }

    public AccessibilityLevel getLevel() {
        return level;
    }

    public AccessibilityGuidelinesVersion getVersion() {
        return version;
    }
}
