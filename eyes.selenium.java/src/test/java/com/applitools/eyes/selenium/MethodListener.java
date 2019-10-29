package com.applitools.eyes.selenium;

import com.applitools.eyes.utils.CommUtils;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.testng.IInvokedMethod;
import org.testng.IInvokedMethodListener2;
import org.testng.ITestContext;
import org.testng.ITestResult;

import java.util.Map;
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
        if (iInvokedMethod.isConfigurationMethod()) {
            JsonObject resultJson = getResultJson(iTestResult);
            System.out.println("Unified report: sending JSON to report " + resultJson.toString());
            try {
                CommUtils.postJson("http://sdk-test-results.herokuapp.com/result", new Gson().fromJson(resultJson, Map.class), null);
            } catch (Throwable t) {
                CommUtils.postJson("http://sdk-test-results.herokuapp.com/result", new Gson().fromJson(resultJson, Map.class), null);
            }
        }
    }

    @Override
    public void beforeInvocation(IInvokedMethod iInvokedMethod, ITestResult iTestResult) {

    }

    @Override
    public void afterInvocation(IInvokedMethod iInvokedMethod, ITestResult iTestResult) {

    }

    private JsonObject getResultJson(ITestResult testResult){
        JsonArray resultsJsonArray = new JsonArray();
        JsonObject innerResultsJsonObject = new JsonObject();
        innerResultsJsonObject.addProperty("test_name", testResult.getMethod().getMethodName());
        innerResultsJsonObject.addProperty("passed", testResult.isSuccess());
        resultsJsonArray.add(innerResultsJsonObject);
        JsonObject finalJsonObject = new JsonObject();
        finalJsonObject.addProperty("sdk", "java");
        finalJsonObject.addProperty("id", suiteId.get());
        String travisGitTag = System.getenv("TRAVIS_TAG");
        if (travisGitTag == null || !travisGitTag.contains("RELEASE_CANDIDATE-")){
            finalJsonObject.addProperty("sandbox", true);
        }
        finalJsonObject.add("results", resultsJsonArray);
        return finalJsonObject;
    }
}