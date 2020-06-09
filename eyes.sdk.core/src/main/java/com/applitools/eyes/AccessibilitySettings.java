package com.applitools.eyes;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

public class AccessibilitySettings {

    @JsonInclude
    private AccessibilityLevel level;

    @JsonProperty("version")
    private AccessibilityGuidelinesVersion guidelinesVersion;

    public AccessibilitySettings() {}

    public AccessibilitySettings(AccessibilityLevel level, AccessibilityGuidelinesVersion guidelinesVersion) {
        this.level = level;
        this.guidelinesVersion = guidelinesVersion;
    }

    public AccessibilityLevel getLevel() {
        return level;
    }

    public AccessibilityGuidelinesVersion getGuidelinesVersion() {
        return guidelinesVersion;
    }
}
