package com.applitools.eyes.utils;

import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

public class ReportingListener implements ITestListener {
    @Override
    public void onTestStart(ITestResult result) {
        System.out.printf("----------Starting Test: %s of %s----------%n", result.getMethod().getMethodName(), result.getTestClass().getName());
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        System.out.printf("----------Test Succeeded: %s of %s----------%n", result.getMethod().getMethodName(), result.getTestClass().getName());
        Object instance = result.getInstance();
        if (instance instanceof ReportingTestSuite) {
            ((ReportingTestSuite)instance).appendTestResults(result);
        }
    }

    @Override
    public void onTestFailure(ITestResult result) {
        System.out.printf("----------Test Failed: %s of %s----------%n", result.getMethod().getMethodName(), result.getTestClass().getName());
        Object instance = result.getInstance();
        if (instance instanceof ReportingTestSuite) {
            ((ReportingTestSuite)instance).appendTestResults(result);
        }
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        System.out.printf("----------Test Skipped: %s of %s----------%n", result.getMethod().getMethodName(), result.getTestClass().getName());
    }

    @Override
    public void onTestFailedButWithinSuccessPercentage(ITestResult result) {
        System.out.printf("----------onTestFailedButWithinSuccessPercentage: %s of %s----------%n", result.getMethod().getMethodName(), result.getTestClass().getName());
        Object instance = result.getInstance();
        if (instance instanceof ReportingTestSuite) {
            ((ReportingTestSuite)instance).appendTestResults(result);
        }
    }

    @Override
    public void onStart(ITestContext context) {
        if (TestUtils.runOnCI && System.getenv("TRAVIS") != null) {
            System.setProperty("webdriver.chrome.driver", "/home/travis/build/chromedriver"); // for travis build.
        }
        TestUtils.createTestResultsDirIfNotExists();
    }

    @Override
    public void onFinish(ITestContext context) {

    }
}
