package com.applitools.eyes.utils;

import org.testng.ISuite;
import org.testng.ISuiteListener;

public class TestResultsReportingListener implements ISuiteListener {

    @Override
    public void onStart(ISuite suite) {

    }

    @Override
    public void onFinish(ISuite suite) {
        int x  = 1;
//        if (iInvokedMethod.isConfigurationMethod()) {
//            TestResultReportSummary reportSummary = getReportSummary(iTestResult);
//            System.out.println("Unified report: sending JSON to report " + reportSummary.toString());
//            try {
//                CommUtils.postJson("http://sdk-test-results.herokuapp.com/result", reportSummary, null);
//            } catch (Throwable t) {
//                CommUtils.postJson("http://sdk-test-results.herokuapp.com/result", reportSummary, null);
//            }
//        }
    }
}
