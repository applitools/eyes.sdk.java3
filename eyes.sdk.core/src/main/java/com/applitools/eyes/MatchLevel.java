package com.applitools.eyes;

import com.fasterxml.jackson.annotation.JsonCreator;

/**
 * The extent in which two images match (or are expected to match).
 */
public enum MatchLevel {
    /**
     * Images do not necessarily match.
     */
    NONE,

    /**
     * Images have the same layout.
     */
    LAYOUT,

    /**
     * Images have the same layout.
     */
    LAYOUT2,

    /**
     * Images have the same outline.
     */
    CONTENT,

    /**
     * Images are nearly identical.
     */
    STRICT,

    /**
     * Images are identical.
     */
    EXACT;

    @JsonCreator
    public static MatchLevel fromString(String key) {
        return key == null ? null : MatchLevel.valueOf(key.toUpperCase());
    }
}
