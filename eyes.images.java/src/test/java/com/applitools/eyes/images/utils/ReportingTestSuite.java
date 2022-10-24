package com.applitools.eyes.images.utils;

import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Listeners;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Listeners({RetryListener.class})
public abstract class ReportingTestSuite {

    private static final String TEST_RESULT_FILENAME_PARTS_SEPARATOR = "_";
    private static final String TEST_RESULT_FILE_EXTENSION = ".json";

    private final TestResultReportSummary reportSummary = new TestResultReportSummary();
    private final Map<String, Object> suiteArgs = new HashMap<>();
    private final Map<ITestContext, Map<String, Object>> testArgs = new HashMap<>();

    protected void setGroupName(String groupName) {
        this.reportSummary.setGroup(groupName);
    }

    protected void addSuiteArg(String key, Object value) {
        this.suiteArgs.put(key, value);
    }

    protected void addTestParameter(ITestContext testContext, String key, Object value) {
        Map<String, Object> args;
        if (!testArgs.containsKey(testContext)) {
            args = new HashMap<>();
            testArgs.put(testContext, args);
        } else {
            args = testArgs.get(testContext);
        }
        args.put(key, value);
    }

    protected void appendTestResults(ITestResult result) {
        boolean passed = result.isSuccess();
        TestResult testResult = new TestResult(result.getMethod().getMethodName(), passed, getTestParameters(result.getTestContext()));
        reportSummary.addResult(testResult);
    }

    private Map<String, Object> getTestParameters(ITestContext testContext) {
        Map<String, Object> resultArgs = new HashMap<>();

        for (Map.Entry<String, Object> entry : suiteArgs.entrySet()) {
            resultArgs.put(entry.getKey(), entry.getValue());
        }

        Map<String, Object> testArgs = this.testArgs.get(testContext);
        if (testArgs != null) {
            for (Map.Entry<String, Object> entry : testArgs.entrySet()) {
                resultArgs.put(entry.getKey(), entry.getValue());
            }
        }

        return resultArgs;
    }

    @AfterClass
    public void oneTimeTearDown(ITestContext testContext) {
        if (reportSummary.getTestResults().isEmpty()) {
            System.out.printf("No results for test %s%n", this.getClass().getName());
            return;
        }

        System.out.printf("Reporting test %s: %s%n", this.getClass().getName(), reportSummary);
        createTestResultFile();
    }

    private void createTestResultFile() {
        if (!TestUtils.createTestResultsDirIfNotExists()) {
            System.out.println("Cannot add test report file.");
            return;
        }

        try {
            String filename = getTestResultFilename();
            File file = new File(filename);
            if (file.createNewFile()) {
                Path report = file.toPath();
                Files.write(report, CommunicationUtils.createJsonString(reportSummary).getBytes());
                System.out.println("Test result was created");
            } else {
                System.out.println("Test result was not created");
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    private String getTestResultFilename() {
        return TestUtils.REPORTING_DIR + reportSummary.getGroup() +
                TEST_RESULT_FILENAME_PARTS_SEPARATOR + this.getClass().getSimpleName() +
                UUID.randomUUID().toString() + TEST_RESULT_FILE_EXTENSION;
    }
}
