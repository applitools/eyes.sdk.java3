package com.applitools.eyes.utils;

import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

public class ReportingListener implements ITestListener {
    @Override
    public void onTestStart(ITestResult result) {
        System.out.println("Starting Test: " + result.getTestName());
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        System.out.println("Test Succeeded: " + result.getTestName());
    }

    @Override
    public void onTestFailure(ITestResult result) {
        System.out.println("Test Failed: " + result.getTestName());
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        System.out.println("Test Skipped: " + result.getTestName());
    }

    @Override
    public void onTestFailedButWithinSuccessPercentage(ITestResult result) {

    }

    @Override
    public void onStart(ITestContext context) {

    }

    @Override
    public void onFinish(ITestContext context) {

    }
}
