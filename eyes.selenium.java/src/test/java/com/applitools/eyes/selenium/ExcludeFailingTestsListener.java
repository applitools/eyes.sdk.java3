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
                for (String parameter: testParameters.values()) {
                    System.out.println("Searching for test: " + iTestResult.getTestClass().getName() + "." + iTestResult.getMethod().getMethodName() + "+" + ((TestSetup) testInstance).mode + " in parameter dont_run: " + parameter);
                }
                for (Map.Entry<String,String> entry : testParameters.entrySet()) {
                    if (entry.getKey().toLowerCase().equals("dont_run") &&
                            entry.getValue().toLowerCase().contains(iTestResult.getMethod().getMethodName().toLowerCase() + "+" + ((TestSetup) testInstance).mode.toLowerCase())) {
                        System.out.println("Test: " + iTestResult.getTestClass().getName() + "." + iTestResult.getMethod().getMethodName() + "+" + ((TestSetup) testInstance).mode + " is excluded");
                        throw new SkipException("Skipping data set " + testParameters.entrySet().toString());
                    } else {
                        System.out.println("Test: " + iTestResult.getTestClass().getName() + "." + iTestResult.getMethod().getMethodName() + "+" + ((TestSetup) testInstance).mode + " is running");
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