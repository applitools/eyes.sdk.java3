package com.applitools.eyes.utils;

import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Listeners;

import java.util.HashMap;
import java.util.Map;

@Listeners({PostTestResultsListener.class, RetryListener.class})
public abstract class ReportingTestSuite {

    private final TestResultReportSummary reportSummary = new TestResultReportSummary();
    private final Map<String, Object> suiteArgs = new HashMap<>();

    protected void setGroupName(String groupName){
        this.reportSummary.setGroup(groupName);
    }

    protected void addSuiteArg(String key, Object value){
        this.suiteArgs.put(key, value);
    }

    protected void appendTestResults(ITestResult result) {
        boolean passed = result.isSuccess();
        TestResult testResult = new TestResult(result.getMethod().getMethodName(), passed, getTestParameters());
        reportSummary.addResult(testResult);
    }

    private Map<String,Object> getTestParameters() {
        return suiteArgs;
    }

    @AfterClass
    public void oneTimeTearDown(ITestContext testContext){
        System.out.println("Unified report: sending JSON to report " + reportSummary.toString());
        try {
            CommunicationUtils.postJson("http://sdk-test-results.herokuapp.com/result", reportSummary, null);
        } catch (Throwable t) {
            CommunicationUtils.postJson("http://sdk-test-results.herokuapp.com/result", reportSummary, null);
        }
    }
}
