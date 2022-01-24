package com.applitools.eyes;

/**
 * Determines how detected failures are reported.
 */
public enum FailureReports {
    /**
     * Failures are reported immediately when they are detected.
     */
    IMMEDIATE("Immediate"),

    /**
     * Failures are reported when tests are completed
     */
    ON_CLOSE("OnClose");

    private final String name;

    FailureReports(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
