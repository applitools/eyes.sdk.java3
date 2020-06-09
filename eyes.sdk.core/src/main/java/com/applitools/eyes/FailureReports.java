package com.applitools.eyes;

/**
 * Determines how detected failures are reported.
 */
public enum FailureReports {
    /**
     * Failures are reported immediately when they are detected.
     */
    IMMEDIATE,

    /**
     * Failures are reported when tests are completed
     */
    ON_CLOSE,
}
