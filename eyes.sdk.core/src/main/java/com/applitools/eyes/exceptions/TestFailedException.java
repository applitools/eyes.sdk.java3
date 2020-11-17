/*
 * Applitools software.
 */
package com.applitools.eyes.exceptions;

import com.applitools.eyes.TestResults;

/**
 * Indicates that a test did not pass (i.e., test either failed or is a new test).
 */
public class TestFailedException extends AssertionError {

    public TestFailedException(TestResults testResults, String scenarioIdOrName, String appIdOrName) {
        super(String.format("'%s' of '%s'. See details at %s",
                scenarioIdOrName,
                appIdOrName,
                testResults.getUrl()));
    }

    /**
     * Creates a new TestFailedException instance.
     * @param message A description string.
     */
    public TestFailedException(String message) {
        super(message);
    }

    /**
     * Creates an EyesException instance.
     * {@code testResults} default to {@code null}.
     * @param message A description of the error.
     * @param cause The cause for this exception.
     */
    public TestFailedException(String message, Throwable cause) {
        super(message, cause);
    }
}
