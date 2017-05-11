package com.applitools.eyes;

/**
 * Indicates that a new test (i.e., a test for which no baseline exists) ended.
 */
public class NewTestException extends TestFailedException {

    /**
     * Creates a new NewTestException instance.
     * @param results The test results if available, {@code null} otherwise.
     * @param message A description string.
     */
    public NewTestException(TestResults results, String message) {
        super(results, message);
    }
}
