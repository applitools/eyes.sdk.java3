package com.applitools.eyes.utils;

import com.applitools.utils.GeneralUtils;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

public class TestResultReportSummary {

    @JsonProperty("group")
    private String group;

    @JsonProperty("id")
    private String id = null;

    @JsonProperty("sdk")
    public String getSdkName() {
        return "java";
    }

    @JsonProperty("id")
    public String getId() {
        if (this.id == null) {
            this.id = GeneralUtils.getEnvString("APPLITOOLS_REPORT_ID");
        }
        if (this.id == null) {
            return "0000-0000";
        }
        return this.id;
    }

    @JsonProperty("id")
    public void setId(String id) {
        this.id = id;
    }

    @JsonProperty("sandbox")
    public boolean getSandbox() {
        String isSandbox = GeneralUtils.getEnvString("APPLITOOLS_REPORT_TO_SANDBOX");
        return !"false".equalsIgnoreCase(isSandbox);
    }

    @JsonProperty("group")
    public String getGroup() {
        return this.group;
    }

    @JsonProperty("group")
    public void setGroup(String group) {
        this.group = group;
    }

    @JsonProperty("results")
    private final List<TestResult> testResults = new ArrayList<>();

    @JsonProperty("results")
    public List<TestResult> getTestResults() {
        return testResults;
    }

    public boolean addResult(TestResult result) {
        boolean newResult = !testResults.contains(result);
        testResults.add(result);
        return newResult;
    }

    @Override
    public String toString() {
        return String.format("Group: %s ; Result count: %s ; Sandbox: %b", group, testResults.size(), getSandbox());
    }
}
