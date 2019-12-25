package com.applitools.eyes.selenium;

import com.applitools.eyes.TestResults;
import com.applitools.eyes.exceptions.TestFailedException;
import com.applitools.eyes.utils.CommUtils;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

public class PostTestResultsListener implements ITestListener {

    private static AtomicReference<String> suiteId = new AtomicReference<>();

    @Override
    public void onTestStart(ITestResult result) {
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
    public void onTestSuccess(ITestResult result) {
        sendTestResluts(result, result.isSuccess());
    }

    @Override
    public void onTestFailure(ITestResult result) {
        sendTestResluts(result, result.isSuccess());
        sendExtraData(result.getMethod().getMethodName(), result, result.getThrowable());
    }

    @Override
    public void onTestSkipped(ITestResult result) {

    }

    @Override
    public void onTestFailedButWithinSuccessPercentage(ITestResult result) {
        sendTestResluts(result, result.isSuccess());
        sendExtraData(result.getMethod().getMethodName(), result, result.getThrowable());
    }

    @Override
    public void onStart(ITestContext context) {

    }

    @Override
    public void onFinish(ITestContext context) {

    }

    private void sendTestResluts(ITestResult iTestResult, boolean success) {
        JsonObject resultJson = getResultJson(iTestResult, success);
        System.out.println("Unified report: sending JSON to report " + resultJson.toString());
        try {
            CommUtils.postJson("http://sdk-test-results.herokuapp.com/result", new Gson().fromJson(resultJson, Map.class), null);
        } catch (Throwable t) {
            CommUtils.postJson("http://sdk-test-results.herokuapp.com/result", new Gson().fromJson(resultJson, Map.class), null);
        }
    }

    private void sendExtraData(String methodName, ITestResult iTestResult, Throwable e) {
        if (e instanceof TestFailedException) {
            TestFailedException testException = (TestFailedException) e;
            String testMode = "";
            try {
                testMode = ((TestSetup)iTestResult.getInstance()).mode;
            } catch (Throwable t){}
            JsonObject json = createExtraDataJson(methodName, testException.getTestResults(), testMode);
            CommUtils.postJson("http://sdk-test-results.herokuapp.com/extra_test_data", new Gson().fromJson(json, Map.class), null);
        } else {
            String testMode = "";
            try {
                testMode = ((TestSetup)iTestResult.getInstance()).mode;
            } catch (Throwable t){}
            JsonObject json = createExtraDataJson(methodName, new TestResults(), testMode);
            CommUtils.postJson("http://sdk-test-results.herokuapp.com/extra_test_data", new Gson().fromJson(json, Map.class), null);
        }
    }

    private JsonObject createExtraDataJson(String methodName, TestResults results, String mode) {
        JsonObject finalJsonObject = new JsonObject();
        finalJsonObject.addProperty("sdk", "java");
        JsonArray extraDataJsonArr = new JsonArray();
        finalJsonObject.add("extra_data", extraDataJsonArr);
        JsonObject innerResultsJsonObject = new JsonObject();
        innerResultsJsonObject.addProperty("test_name", methodName);
        innerResultsJsonObject.addProperty("data", results.getUrl() + " mode: " + mode);
        extraDataJsonArr.add(innerResultsJsonObject);
        return finalJsonObject;
    }

    private JsonObject getResultJson(ITestResult testResult, boolean success){
        JsonArray resultsJsonArray = new JsonArray();
        JsonObject innerResultsJsonObject = new JsonObject();
        innerResultsJsonObject.addProperty("test_name", testResult.getMethod().getMethodName());
        innerResultsJsonObject.addProperty("passed", success);
        resultsJsonArray.add(innerResultsJsonObject);
        JsonObject finalJsonObject = new JsonObject();
        finalJsonObject.addProperty("sdk", "java");
        finalJsonObject.addProperty("id", suiteId.get());
        String travisGitTag = System.getenv("TRAVIS_TAG");
        if (travisGitTag == null || !travisGitTag.contains("RELEASE_CANDIDATE")){
            finalJsonObject.addProperty("sandbox", true);
        }
        finalJsonObject.add("results", resultsJsonArray);
        return finalJsonObject;
    }
}
