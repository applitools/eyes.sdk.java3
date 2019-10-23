package com.applitools.eyes.selenium;

import com.applitools.eyes.RectangleSize;
import com.applitools.eyes.TestResultsSummary;
import com.applitools.eyes.selenium.fluent.Target;
import com.applitools.eyes.utils.SeleniumUtils;
import com.applitools.eyes.utils.TestUtils;
import com.applitools.eyes.visualgrid.model.RenderBrowserInfo;
import com.applitools.eyes.visualgrid.services.VisualGridRunner;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.annotations.Test;

public final class TestCounts {

    private TestObjects InitEyes_() {
        WebDriver webDriver = SeleniumUtils.createChromeDriver();
        webDriver.get("https://applitools.com/helloworld");
        VisualGridRunner runner = new VisualGridRunner(10);
        Eyes eyes = new Eyes(runner);
        TestUtils.setupLogging(eyes);
        eyes.setSendDom(false);
        return new TestObjects(webDriver, runner, eyes);
    }

    @Test
    public void Test_VGTestsCount_1() {
        TestObjects testObjects = InitEyes_();
        testObjects.eyes.setBatch(TestDataProvider.batchInfo);
        try {
            testObjects.eyes.open(testObjects.webDriver, "Test Count", "Test_VGTestsCount_1", new RectangleSize(640, 480));
            testObjects.eyes.check("Test", Target.window());
            testObjects.eyes.close();
            TestResultsSummary resultsSummary = testObjects.runner.getAllTestResults();
            Assert.assertEquals(resultsSummary.size(), 1);
        } finally {
            testObjects.webDriver.quit();
            testObjects.eyes.abort();
        }
    }

    @Test
    public void Test_VGTestsCount_2() {
        TestObjects testObjects = InitEyes_();
        try {
            Configuration conf = new Configuration();
            conf.setBatch(TestDataProvider.batchInfo);
            conf.addBrowser(new RenderBrowserInfo(900, 600));
            conf.addBrowser(new RenderBrowserInfo(1024, 768));
            testObjects.eyes.setConfiguration(conf);
            testObjects.eyes.open(testObjects.webDriver, "Test Count", "Test_VGTestsCount_2");
            testObjects.eyes.check("Test", Target.window());
            testObjects.eyes.close();
            TestResultsSummary resultsSummary = testObjects.runner.getAllTestResults();
            Assert.assertEquals(resultsSummary.size(), 2);
        } finally {
            testObjects.webDriver.quit();
            testObjects.eyes.abort();
        }
    }

    @Test
    public void Test_VGTestsCount_3() {
        TestObjects testObjects = InitEyes_();
        try {
            Configuration conf = new Configuration();
            conf.setBatch(TestDataProvider.batchInfo);
            conf.addBrowser(new RenderBrowserInfo(900, 600));
            conf.addBrowser(new RenderBrowserInfo(1024, 768));
            conf.setAppName("Test Count").setTestName("Test_VGTestsCount_3");
            testObjects.eyes.setConfiguration(conf);
            testObjects.eyes.open(testObjects.webDriver);
            testObjects.eyes.check("Test", Target.window());
            testObjects.eyes.close();
            TestResultsSummary resultsSummary = testObjects.runner.getAllTestResults();
            Assert.assertEquals(resultsSummary.size(), 2);
        } finally {
            testObjects.webDriver.quit();
            testObjects.eyes.abort();
        }
    }

    @Test
    public void Test_VGTestsCount_4() {
        TestObjects testObjects = InitEyes_();
        try {
            Configuration conf = new Configuration();
            conf.setBatch(TestDataProvider.batchInfo);
            conf.setAppName("Test Count").setTestName("Test_VGTestsCount_4");
            testObjects.eyes.setConfiguration(conf);
            testObjects.eyes.open(testObjects.webDriver);
            testObjects.eyes.check("Test", Target.window());
            testObjects.eyes.close();
            TestResultsSummary resultsSummary = testObjects.runner.getAllTestResults();
            Assert.assertEquals(resultsSummary.size(), 1);
        } finally {
            testObjects.webDriver.quit();
            testObjects.eyes.abort();
        }
    }

    @Test
    public void Test_VGTestsCount_5() {
        TestObjects testObjects = InitEyes_();
        try {
            Configuration conf = new Configuration();
            conf.setBatch(TestDataProvider.batchInfo);
            conf.addBrowser(new RenderBrowserInfo(900, 600));
            conf.addBrowser(new RenderBrowserInfo(1024, 768));
            testObjects.eyes.setConfiguration(conf);
            testObjects.eyes.open(testObjects.webDriver, "Test Count", "Test_VGTestsCount_5", new RectangleSize(640, 480));
            testObjects.eyes.check("Test", Target.window());
            testObjects.eyes.close();
            TestResultsSummary resultsSummary = testObjects.runner.getAllTestResults();
            Assert.assertEquals(resultsSummary.size(), 2);
        } finally {
            testObjects.webDriver.quit();
            testObjects.eyes.abort();
        }
    }

    private class TestObjects {
        public TestObjects(WebDriver webDriver, VisualGridRunner runner, Eyes eyes) {
            this.webDriver = webDriver;
            this.runner = runner;
            this.eyes = eyes;
        }

        public final Eyes eyes;
        public final VisualGridRunner runner;
        public final WebDriver webDriver;
    }
}
