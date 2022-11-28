package com.applitools.eyes.selenium;
//
//import com.applitools.eyes.TestResult;
//import com.applitools.eyes.TestResultReportSummary;
import org.testng.IInvokedMethod;
import org.testng.IInvokedMethodListener2;
import org.testng.ITestContext;
import org.testng.ITestResult;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

public class MethodListener implements IInvokedMethodListener2 {

    private static AtomicReference<String> suiteId = new AtomicReference<>();

    @Override
    public void beforeInvocation(IInvokedMethod iInvokedMethod, ITestResult iTestResult, ITestContext iTestContext) {
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
    public void afterInvocation(IInvokedMethod iInvokedMethod, ITestResult iTestResult, ITestContext iTestContext) {
//        if (iInvokedMethod.isConfigurationMethod()) {
//            TestResultReportSummary reportSummary = getReportSummary(iTestResult);
//            System.out.println("Unified report: sending JSON to report " + reportSummary.toString());
//            try {
//                CommUtils.postJson("http://applitools-quality-server.herokuapp.com/result", reportSummary, null);
//            } catch (Throwable t) {
//                CommUtils.postJson("http://applitools-quality-server.herokuapp.com/result", reportSummary, null);
//            }
//        }
    }

    @Override
    public void beforeInvocation(IInvokedMethod iInvokedMethod, ITestResult iTestResult) {

    }

    @Override
    public void afterInvocation(IInvokedMethod iInvokedMethod, ITestResult iTestResult) {

    }
//
//    private TestResultReportSummary getReportSummary(ITestResult testResult) {
//        TestResult result = new TestResult(testResult.getMethod().getMethodName(), testResult.isSuccess(), null);
//        TestResultReportSummary summary = new TestResultReportSummary();
//        summary.setGroup("selenium");
//        summary.addResult(result);
//        summary.setId(suiteId.get());
//        return summary;
//    }
}