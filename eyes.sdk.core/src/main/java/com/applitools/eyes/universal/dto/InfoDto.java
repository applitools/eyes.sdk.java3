package com.applitools.eyes.universal.dto;

import com.applitools.eyes.TestResults;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class InfoDto {
    private TestResults testResult;

    public TestResults getTestResult() {
        return testResult;
    }

    public void setTestResult(TestResults testResult) {
        this.testResult = testResult;
    }
}
