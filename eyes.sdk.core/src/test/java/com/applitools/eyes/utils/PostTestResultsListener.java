package com.applitools.eyes.utils;

import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

public class PostTestResultsListener implements ITestListener {

    private static AtomicReference<String> suiteId = new AtomicReference<>();

    @Override
    public void onTestStart(ITestResult result) {
        if (suiteId.get() == null) {
            String travisCommit = System.getenv("TRAVIS_COMMIT");
            System.out.println("Unified report: travis commit is " + travisCommit);
            if (travisCommit == null || travisCommit.isEmpty()) {
                suiteId.set(UUID.randomUUID().toString().substring(0, 12));
            } else {
                suiteId.set(travisCommit.substring(0, 12));
            }
        }
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        Object instance = result.getInstance();
        if (instance instanceof ReportingTestSuite) {
            ((ReportingTestSuite)instance).appendTestResults(result);
        }
    }

    @Override
    public void onTestFailure(ITestResult result) {
        Object instance = result.getInstance();
        if (instance instanceof ReportingTestSuite) {
            ((ReportingTestSuite)instance).appendTestResults(result);
        }
        System.out.println("Test failed: " + result.getMethod().getMethodName() + " (" + result.getTestName() + ")");
    }

    @Override
    public void onTestSkipped(ITestResult result) {

    }

    @Override
    public void onTestFailedButWithinSuccessPercentage(ITestResult result) {
        Object instance = result.getInstance();
        if (instance instanceof ReportingTestSuite) {
            ((ReportingTestSuite)instance).appendTestResults(result);
        }
    }

    @Override
    public void onStart(ITestContext context) {

    }

    @Override
    public void onFinish(ITestContext context) {

    }
}
