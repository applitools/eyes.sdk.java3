package com.applitools.eyes.exceptions;

import com.applitools.eyes.TestResults;

/**
 * Indicates that a new test (i.e., a test for which no baseline exists) ended.
 */
public class NewTestException extends TestFailedException {

    public NewTestException(TestResults testResults, String scenarioIdOrName, String appIdOrName){
        super(String.format("'%s' of '%s'. Please approve the new baseline at %s",
                scenarioIdOrName,
                appIdOrName,
                testResults.getUrl()));
    }
}
