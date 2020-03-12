package com.applitools.eyes.utils;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

public class TestResult {

    private final boolean passed;
    private final Map<String, Object> parameters;
    private String testName;

    public TestResult(String testName, boolean passed, Map<String, Object> parameters) {
        this.testName = testName;
        this.parameters = parameters;
        this.passed = passed;
    }

    @JsonProperty("test_name")
    public String getTestName() {
        return this.testName;
    }

    @JsonProperty("parameters")

    public Map<String, Object> getParameters() {
        return this.parameters;
    }

    @JsonProperty("passed")
    public boolean getPassed() {
        return this.passed;
    }

    public boolean equals(TestResult other) {
        if (other == null) return false;
        return this.testName.equals(other.testName) &&
                AreDictionariesEqual(parameters, other.parameters);
    }

    private boolean AreDictionariesEqual(Map<String, Object> d1, Map<String, Object> d2) {
        if (d1.size() != d2.size()) {
            return false;
        }

        for (String key : d1.keySet()) {
            if (!d2.containsKey(key)) return false;
            if (d2.get(key) != d1.get(key)) return  false;
        }

        return true;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof TestResult) {
            return equals((TestResult) obj);
        }

        return false;
    }

    @Override
    public int hashCode() {
        return testName.hashCode();
    }
}
