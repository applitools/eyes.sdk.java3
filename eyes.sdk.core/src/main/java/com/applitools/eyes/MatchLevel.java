package com.applitools.eyes;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonValue;
import com.google.gson.annotations.SerializedName;

/**
 * The extent in which two images match (or are expected to match).
 */
public enum MatchLevel {
    /**
     * Images do not necessarily match.
     */
    NONE("None"),

    /**
     * Images have the same layout.
     */
    LAYOUT("Layout"),

    /**
     * Images have the same layout.
     */
    LAYOUT2("Layout2"),

    /**
     * Images have the same outline.
     */
    CONTENT("Content"),

    /**
     * Images are nearly identical.
     */
    STRICT("Strict"),

    /**
     * Images are identical.
     */
    EXACT("Exact");

    private final String name;

    MatchLevel(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @JsonCreator
    public static MatchLevel fromString(String key) {
        return key == null ? null : MatchLevel.valueOf(key.toUpperCase());
    }
}
