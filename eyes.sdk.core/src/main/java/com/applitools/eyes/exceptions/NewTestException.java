package com.applitools.eyes.exceptions;

import com.applitools.eyes.SessionStartInfo;
import com.applitools.eyes.TestResults;

/**
 * Indicates that a new test (i.e., a test for which no baseline exists) ended.
 */
public class NewTestException extends TestFailedException {

    public NewTestException(TestResults testResults, SessionStartInfo sessionStartInfo){
        super(testResults, String.format("'%s' of '%s'. Please approve the new baseline at %s",
                sessionStartInfo.getScenarioIdOrName(),
                sessionStartInfo.getAppIdOrName(),
                testResults.getUrl()));
    }

    /**
     * Creates a new NewTestException instance.
     * @param results The test results if available, {@code null} otherwise.
     * @param message A description string.
     */
    public NewTestException(TestResults results, String message) {
        super(results, message);
    }
}
