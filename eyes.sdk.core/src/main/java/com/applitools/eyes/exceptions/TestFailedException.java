/*
 * Applitools software.
 */
package com.applitools.eyes.exceptions;

import com.applitools.eyes.TestResults;

/**
 * Indicates that a test did not pass (i.e., test either failed or is a new test).
 */
public class TestFailedException extends AssertionError {

    private TestResults testResults = null;

    public TestFailedException(TestResults testResults, String scenarioIdOrName, String appIdOrName) {
        super(String.format("'%s' of '%s'. See details at %s",
                scenarioIdOrName,
                appIdOrName,
                testResults.getUrl()));
        this.testResults = testResults;
    }

    /**
     * Creates a new TestFailedException instance.
     * @param testResults The results of the current test if available, {@code null} otherwise.
     * @param message A description string.
     */
    public TestFailedException(TestResults testResults,
                               String message) {
        super(message);
        this.testResults = testResults;
    }

    /**
     * Creates a new TestFailedException instance.
     * @param message A description string.
     */
    public TestFailedException(String message) {
        this(null, message);
    }

    /**
     * Creates an EyesException instance.
     * {@code testResults} default to {@code null}.
     * @param message A description of the error.
     * @param cause The cause for this exception.
     */
    public TestFailedException(String message, Throwable cause) {
        super(message, cause);
        this.testResults = null;
    }

    /**
     * @return The failed test results, or {@code null} if the test has not
     * yet ended (e.g., when thrown due to
     * {@link com.applitools.eyes.FailureReports#IMMEDIATE} settings).
     */
    public TestResults getTestResults() {
        return testResults;
    }
}
