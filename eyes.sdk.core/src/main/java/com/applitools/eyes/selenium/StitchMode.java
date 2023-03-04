package com.applitools.eyes.selenium;

/**
 * Represents the types of available stitch modes.
 */
public enum StitchMode {
    /**
     * Standard JS scrolling.
     */
    SCROLL("Scroll"),

    /**
     * CSS translation based stitching.
     */
    CSS("CSS"),

    /**
     * Resize based full-content.
     */
    RESIZE("Resize");

    private final String name;

    StitchMode(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
