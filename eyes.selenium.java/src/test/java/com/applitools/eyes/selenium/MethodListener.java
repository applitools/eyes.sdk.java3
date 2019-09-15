package com.applitools.eyes.selenium;

import cucumber.api.java.ca.I;
import org.testng.IInvokedMethod;
import org.testng.IInvokedMethodListener2;
import org.testng.ITestContext;
import org.testng.ITestResult;

import java.lang.reflect.Parameter;

public class MethodListener implements IInvokedMethodListener2 {
    @Override
    public void beforeInvocation(IInvokedMethod method, ITestResult testResult, ITestContext context) {

    }

    @Override
    public void afterInvocation(IInvokedMethod method, ITestResult testResult, ITestContext context) {
        try {
            Parameter[] parameters = method.getTestMethod().getConstructorOrMethod().getMethod().getParameters();
            String name = parameters[0].getName();
            System.out.println(name);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void beforeInvocation(IInvokedMethod method, ITestResult testResult) {

    }

    @Override
    public void afterInvocation(IInvokedMethod method, ITestResult testResult) {

    }
}
