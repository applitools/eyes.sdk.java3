package com.applitools.eyes.selenium;

import com.applitools.eyes.RectangleSize;
import com.applitools.eyes.TestResults;
import com.applitools.eyes.selenium.fluent.Target;
import com.applitools.eyes.utils.SeleniumUtils;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.UUID;

public class TestServerStatus {

    @Test
    public void TestSessionSummary_Status_Failed() {
        WebDriver webDriver = SeleniumUtils.createChromeDriver();
        webDriver.get("https://applitools.github.io/demo/TestPages/DynamicResolution/mobile.html");

        String uuid = "_" + UUID.randomUUID().toString();

        Eyes eyes = new Eyes();
        eyes.setSaveNewTests(true);
        eyes.open(webDriver,
                TestServerStatus.class.getSimpleName(),
                TestServerStatus.class.getSimpleName(), new RectangleSize(800, 600));
        eyes.setBatch(TestDataProvider.batchInfo);
        try {
            eyes.check("TestSessionSummary_Status_Failed" + uuid, Target.window().fully(false));
            eyes.close(false);
        } finally {
            eyes.abortIfNotClosed();
        }

        webDriver.get("https://applitools.github.io/demo/TestPages/DynamicResolution/desktop.html");

        eyes.open(webDriver,
                TestServerStatus.class.getSimpleName(),
                TestServerStatus.class.getSimpleName(), new RectangleSize(800, 600));
        try {
            eyes.check("TestSessionSummary_Status_Failed" + uuid, Target.window().fully(false));
            TestResults results = eyes.close(false);
            Assert.assertTrue(results.isDifferent());
        } finally {
            eyes.abortIfNotClosed();
            webDriver.quit();
        }
    }

    @Test
    public void TestSessionSummary_Status_New() {
        Eyes eyes = new Eyes();
        eyes.setSaveNewTests(false);
        WebDriver webDriver = SeleniumUtils.createChromeDriver();
        String uuid = "_" + UUID.randomUUID().toString();

        WebDriver driver = eyes.open(webDriver,
                TestServerStatus.class.getSimpleName() + uuid,
                TestServerStatus.class.getSimpleName() + uuid, new RectangleSize(800, 600));

        driver.get("http://applitools.github.io/demo/TestPages/FramesTestPage/");
        try {
            eyes.check("TestSessionSummary_Status_New" + uuid, Target.window());
            TestResults results = eyes.close(false);
            Assert.assertTrue(results.isNew());
        } finally {
            eyes.abortIfNotClosed();
            webDriver.quit();
        }
    }
}
