package com.applitools.eyes.visualgrid.services;

public class RunnerOptions {

    private Integer testConcurrency = null;

    public RunnerOptions testConcurrency(int testConcurrency) {
        this.testConcurrency = testConcurrency;
        return this;
    }

    public Integer getTestConcurrency() {
        return testConcurrency;
    }
}
