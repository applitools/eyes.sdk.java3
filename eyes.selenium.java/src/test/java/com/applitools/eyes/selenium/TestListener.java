package com.applitools.eyes.selenium;

import com.applitools.eyes.*;
import com.applitools.eyes.logging.Stage;
import com.applitools.eyes.metadata.ActualAppOutput;
import com.applitools.eyes.metadata.ImageMatchSettings;
import com.applitools.eyes.metadata.SessionResults;
import com.applitools.eyes.utils.TestUtils;
import com.applitools.utils.GeneralUtils;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;

public class TestListener implements ITestListener {

    @Override
    public void onTestStart(ITestResult result) {
        if (!isBuildOnTravis()) {
            Object instance = result.getInstance();
            if (instance instanceof TestSetup) {
                TestSetup testSetup = (TestSetup) instance;
                Method method = result.getMethod().getConstructorOrMethod().getMethod();
                testSetup.beforeMethod(method.getName());
            }
        }
    }

    private boolean isBuildOnTravis() {
        return System.getenv("TRAVIS") != null && System.getenv("TRAVIS").equals("true");
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        //System.out.println("onTestSuccess");
        Object instance = result.getInstance();
        if (instance instanceof TestSetup) {
            if (!afterMethodSuccess((TestSetup) instance)) {
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
            GeneralUtils.logExceptionStackTrace(testSetup.getEyes().getLogger(), Stage.GENERAL, result.getThrowable());
            afterMethodFailure(testSetup);
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
            afterMethodFailure((TestSetup) instance);
        }
    }

    private void afterMethodFailure(TestSetup testSetup) {
        Eyes eyes = testSetup.getEyes();
        try {
            eyes.closeAsync();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            eyes.abortIfNotClosed();
            if (testSetup.getDriver() != null) {
                testSetup.getDriver().quit();
            }
            testSetup.getRunner().getAllTestResults(false);
        }
    }

    private boolean afterMethodSuccess(TestSetup testSetup) {
        Eyes eyes = testSetup.getEyes();
        try {
            TestResults results;
            results = eyes.close();
            if (eyes.getIsDisabled()) {
                return true;
            } else if (results == null) {
                return true;
            }

            SessionResults resultObject = TestUtils.getSessionResults(eyes.getApiKey(), results);

            ActualAppOutput[] actualAppOutput = resultObject.getActualAppOutput();

            if (actualAppOutput.length > 0) {
                ImageMatchSettings imageMatchSettings = actualAppOutput[0].getImageMatchSettings();
                compareRegions(testSetup, imageMatchSettings);
                compareProperties(testSetup, imageMatchSettings);
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
                GeneralUtils.logExceptionStackTrace(testSetup.getEyes().getLogger(), Stage.GENERAL, e);
            }

                Assert.assertEquals(currentObject, kvp.getValue(), String.format("Property comparison for test '%s' failed! Property %s expected %s but got %s",testSetup.getTestName(), kvp.getKey(), kvp.getValue(), currentObject));
        }
    }

    @Override
    public void onStart(ITestContext context) {
        //System.out.println("onStart");
    }

    @Override
    public void onFinish(ITestContext context) {
        //System.out.println("onFinish");
        Object instance = context.getAllTestMethods()[0].getInstance();
        if (instance instanceof TestSetup) {
            TestSetup testSetup = (TestSetup) instance;
            testSetup.getRunner().getAllTestResults(false);
        }
    }
}