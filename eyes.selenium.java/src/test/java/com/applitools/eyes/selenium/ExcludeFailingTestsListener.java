package com.applitools.eyes.selenium;

import org.testng.*;

import java.lang.reflect.Method;
import java.util.Map;


/**
 * This listener helps exclude tests from the suite we know will fail.
 * Implement this listener by adding it to your TestNG suite.
 * To exclude a test add a parameter in the suite with key: "dont_run", value: "methodName+dataProviderInstance"
 * Example: <parameter name="dont_run" value="test+CSS"></parameter>
 * Example: <parameter name="dont_run" value="TestIOSSafariCrop_SauceLabs+iPhone XR Simulator+12.2+PORTRAIT+true+scrolled_mobile"></parameter>
 */
public class ExcludeFailingTestsListener implements IInvokedMethodListener2 {

    @Override
    public void beforeInvocation(IInvokedMethod iInvokedMethod, ITestResult iTestResult, ITestContext iTestContext) {
        if (iTestResult.getMethod().isBeforeMethodConfiguration() || iTestResult.getMethod().isTest()) {
            Map<String, String> testParameters = iTestContext.getCurrentXmlTest().getAllParameters();
            Object testInstance = iInvokedMethod.getTestResult().getInstance();
            String testData;
            if (testInstance instanceof TestSetup){
                testData = ((TestSetup) testInstance).mode;
                handleTests(iTestResult, testParameters, testData);
                Method method = iTestResult.getMethod().getConstructorOrMethod().getMethod();
                ((TestSetup) testInstance).beforeMethod(method.getName());
            }
            if (testInstance instanceof TestMobileDevices) {
                testData = ((TestMobileDevices) testInstance).deviceName
                        + "+" + ((TestMobileDevices) testInstance).platformVersion
                        + "+" + ((TestMobileDevices) testInstance).deviceOrientation.toString()
                        + "+" + ((TestMobileDevices) testInstance).page;
                handleTests(iTestResult, testParameters, testData);
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

    private void handleTests(ITestResult iTestResult, Map<String, String> testParameters, String testData) {
        for (String parameter: testParameters.values()) {
            System.out.println("Searching for test: " + iTestResult.getTestClass().getName() + "." + iTestResult.getMethod().getMethodName() + "+" + testData + " in parameter dont_run: " + parameter);
        }
        for (Map.Entry<String,String> entry : testParameters.entrySet()) {
            if (entry.getKey().equalsIgnoreCase("dont_run") &&
                    entry.getValue().toLowerCase().contains((iTestResult.getMethod().getMethodName().toLowerCase() + "+" + testData).toLowerCase())) {
                System.out.println("Test: " + iTestResult.getTestClass().getName() + "." + iTestResult.getMethod().getMethodName() + "+" + testData + " is excluded");
                throw new SkipException("Skipping data set " + testParameters.entrySet().toString());
            } else {
                System.out.println("Test: " + iTestResult.getTestClass().getName() + "." + iTestResult.getMethod().getMethodName() + "+" + testData + " is running");
            }
        }
    }
}