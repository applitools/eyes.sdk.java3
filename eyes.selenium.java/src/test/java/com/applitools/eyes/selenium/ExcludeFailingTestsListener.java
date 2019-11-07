package com.applitools.eyes.selenium;

import org.testng.*;
import java.util.*;



/**
 * This listener helps exclude tests from the suite we know will fail.
 * Implement this listener by adding it to your TestNG suite.
 * To exclude a test add a parameter in the suite with key: "dont_run", value: "methodName+dataProviderInstance"
 * Example: <parameter name="dont_run" value="test+CSS"></parameter>
 */
public class ExcludeFailingTestsListener implements IInvokedMethodListener2 {

    @Override
    public void beforeInvocation(IInvokedMethod iInvokedMethod, ITestResult iTestResult, ITestContext iTestContext) {
        if (iTestResult.getMethod().isBeforeMethodConfiguration() || iTestResult.getMethod().isTest()) {
            Map<String, String> testParameters = iTestContext.getCurrentXmlTest().getAllParameters();
            Object testInstance = iInvokedMethod.getTestResult().getInstance();
            if (testInstance instanceof TestSetup){
                for (Map.Entry<String,String> entry : testParameters.entrySet()) {
                    if (entry.getKey().toLowerCase().equals("dont_run") &&
                            entry.getValue().toLowerCase().contains(iTestResult.getMethod().getMethodName().toLowerCase() + "+" + ((TestSetup) testInstance).mode.toLowerCase())) {
                        System.out.println("Test: " + entry.getValue() + " is excluded");
                        throw new SkipException("Skipping data set " + testParameters.entrySet().toString());
                    }
                }
            }
        }
    }

    @Override
    public void afterInvocation(IInvokedMethod iInvokedMethod, ITestResult iTestResult, ITestContext iTestContext) {
    }

    @Override
    public void beforeInvocation(IInvokedMethod iInvokedMethod, ITestResult iTestResult) {

    }

    @Override
    public void afterInvocation(IInvokedMethod iInvokedMethod, ITestResult iTestResult) {

    }

    public static class DataSetSkipException extends SkipException {
        public DataSetSkipException(String message) {
            super(message);
        }
    }
}