package com.applitools.eyes.utils;

import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

public class ReportingListener implements ITestListener {
    @Override
    public void onTestStart(ITestResult result) {
        System.out.println(String.format("Starting Test: %s of %s", result.getMethod().getMethodName(), result.getTestClass().getName()));
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        System.out.println(String.format("Test Succeeded: %s of %s", result.getMethod().getMethodName(), result.getTestClass().getName()));
    }

    @Override
    public void onTestFailure(ITestResult result) {
        System.out.println(String.format("Test Failed: %s of %s", result.getMethod().getMethodName(), result.getTestClass().getName()));
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        System.out.println(String.format("Test Skipped: %s of %s", result.getMethod().getMethodName(), result.getTestClass().getName()));
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
