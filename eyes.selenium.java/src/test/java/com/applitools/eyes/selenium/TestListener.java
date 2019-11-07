package com.applitools.eyes.selenium;

import com.applitools.eyes.*;
import com.applitools.eyes.exceptions.TestFailedException;
import com.applitools.eyes.metadata.ActualAppOutput;
import com.applitools.eyes.metadata.ImageMatchSettings;
import com.applitools.eyes.metadata.SessionResults;
import com.applitools.eyes.utils.CommUtils;
import com.applitools.eyes.utils.TestUtils;
import com.applitools.utils.GeneralUtils;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

public class TestListener implements ITestListener {

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

        //System.out.println("onTestStart");
        Object instance = result.getInstance();
        if (instance instanceof TestSetup) {
            TestSetup testSetup = (TestSetup) instance;
            Method method = result.getMethod().getConstructorOrMethod().getMethod();
            testSetup.beforeMethod(method.getName());
        }
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        //System.out.println("onTestSuccess");
        Object instance = result.getInstance();
        if (instance instanceof TestSetup) {
            if (!afterMethodSuccess((TestSetup) instance, result.getMethod().getMethodName(), result)) {
                result.setStatus(ITestResult.FAILURE);
            }
        }
    }

    @Override
    public void onTestFailure(ITestResult result) {
        //System.out.println("onTestFailure");
        Object instance = result.getInstance();
        if (instance instanceof TestSetup) {
            TestSetup testSetup = (TestSetup) instance;
            GeneralUtils.logExceptionStackTrace(testSetup.getEyes().getLogger(), result.getThrowable());
            afterMethodFailure(testSetup, result);
        }
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        //System.out.println("onTestSkipped");
    }

    @Override
    public void onTestFailedButWithinSuccessPercentage(ITestResult result) {
        //System.out.println("onTestFailedButWithinSuccessPercentage");
        Object instance = result.getInstance();
        if (instance instanceof TestSetup) {
            afterMethodFailure((TestSetup) instance, result);
        }
    }

    private void afterMethodFailure(TestSetup testSetup, ITestResult iTestResult) {
        Eyes eyes = testSetup.getEyes();
        try {
            if (eyes.getIsOpen()) {
                TestResults close = eyes.close(false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            eyes.abortIfNotClosed();
            if (testSetup.getDriver() != null) {
                testSetup.getDriver().quit();
            }
            sendTestResluts(iTestResult, iTestResult.isSuccess());
            sendExtraData(iTestResult.getMethod().getMethodName(), iTestResult, new Exception());
        }
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

    private boolean afterMethodSuccess(TestSetup testSetup, String methodName, ITestResult iTestResult) {
        Eyes eyes = testSetup.getEyes();
        try {
            if (eyes.getIsOpen()) {

                TestResults results = null;
                try {
                    results = eyes.close();
                } catch (Throwable e) {
                    sendTestResluts(iTestResult, false);
                    sendExtraData(methodName, iTestResult, e);
                    throw e;
                }
                sendTestResluts(iTestResult, iTestResult.isSuccess());
                if (eyes.getIsDisabled()) {
                    eyes.getLogger().log("eyes is disabled.");
                    return true;
                } else if (results == null) {
                    eyes.getLogger().verbose("no results returned from eyes.close()");
                    return true;
                }

                SessionResults resultObject = TestUtils.getSessionResults(eyes.getApiKey(), results);

                ActualAppOutput[] actualAppOutput = resultObject.getActualAppOutput();

                if (actualAppOutput.length > 0) {
                    ImageMatchSettings imageMatchSettings = actualAppOutput[0].getImageMatchSettings();
                    compareRegions(testSetup, imageMatchSettings);
                    compareProperties(testSetup, imageMatchSettings);
                }
            }
            return true;
        } catch (Throwable e) {
            e.printStackTrace();
            return false;
        } finally {
            eyes.abortIfNotClosed();
            if (testSetup.getDriver() != null) {
                testSetup.getDriver().quit();
            }
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

    private void compareRegions(TestSetup testSetup, ImageMatchSettings imageMatchSettings) {
        FloatingMatchSettings[] floating = imageMatchSettings.getFloating();
        AccessibilityRegionByRectangle[] accessibility = imageMatchSettings.getAccessibility();
        Region[] ignoreRegions = imageMatchSettings.getIgnore();
        Region[] layoutRegions = imageMatchSettings.getLayout();
        Region[] strictRegions = imageMatchSettings.getStrict();
        Region[] contentRegions = imageMatchSettings.getContent();

        TestSetup.SpecificTestContextRequirements testData = testSetup.getTestData();

        if (testSetup.compareExpectedRegions) {
            if (testData.expectedAccessibilityRegions.size() > 0) {
                HashSet<AccessibilityRegionByRectangle> accessibilityRegionSet = new HashSet<>(Arrays.asList(accessibility));
                Assert.assertEquals(accessibilityRegionSet, testData.expectedAccessibilityRegions, "Accessibility regions lists differ");
            }
            if (testData.expectedFloatingRegions.size() > 0) {
                HashSet<FloatingMatchSettings> floatingRegionsSet = new HashSet<>(Arrays.asList(floating));
                Assert.assertEquals(floatingRegionsSet, testData.expectedFloatingRegions, "Floating regions lists differ");
            }

            if (testData.expectedIgnoreRegions.size() > 0) {
                HashSet<Region> ignoreRegionsSet = new HashSet<>(Arrays.asList(ignoreRegions));
                Assert.assertEquals(ignoreRegionsSet, testData.expectedIgnoreRegions, "Ignore regions lists differ");
            }

            if (testData.expectedLayoutRegions.size() > 0) {
                HashSet<Region> layoutRegionsSet = new HashSet<>(Arrays.asList(layoutRegions));
                Assert.assertEquals(layoutRegionsSet, testData.expectedLayoutRegions, "Layout regions lists differ");
            }

            if (testData.expectedStrictRegions.size() > 0) {
                HashSet<Region> strictRegionsSet = new HashSet<>(Arrays.asList(strictRegions));
                Assert.assertEquals(strictRegionsSet, testData.expectedStrictRegions, "Strict regions lists differ");
            }

            if (testData.expectedContentRegions.size() > 0) {
                HashSet<Region> contentRegionsSet = new HashSet<>(Arrays.asList(contentRegions));
                Assert.assertEquals(contentRegionsSet, testData.expectedContentRegions, "Content regions lists differ");
            }
        }
    }

    private void compareProperties(TestSetup testSetup, ImageMatchSettings imageMatchSettings) {
        TestSetup.SpecificTestContextRequirements testData = testSetup.getTestData();
        Map<String, Object> expectedProps = testData.expectedProperties;

        Class<?> imsType = ImageMatchSettings.class;
        for (Map.Entry<String, Object> kvp : expectedProps.entrySet()) {
            String propertyNamePath = kvp.getKey();
            String[] properties = propertyNamePath.split("\\.");

            Class<?> currentType = imsType;
            Object currentObject = imageMatchSettings;
            try {
                for (String propName : properties) {
                    Method getter = currentType.getMethod("get" + propName);
                    currentObject = getter.invoke(currentObject);
                    if (currentObject == null) break;
                    currentType = currentObject.getClass();
                }
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                GeneralUtils.logExceptionStackTrace(testSetup.getEyes().getLogger(), e);
            }

            Assert.assertEquals(currentObject, kvp.getValue());
        }
    }

    @Override
    public void onStart(ITestContext context) {
        //System.out.println("onStart");
    }

    @Override
    public void onFinish(ITestContext context) {
        //System.out.println("onFinish");
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
        if (travisGitTag == null || !travisGitTag.contains("RELEASE_CANDIDATE-")){
            finalJsonObject.addProperty("sandbox", true);
        }
        finalJsonObject.add("results", resultsJsonArray);
        return finalJsonObject;
    }
}