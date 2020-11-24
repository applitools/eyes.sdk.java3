package com.applitools.eyes.api;

import com.applitools.eyes.*;
import com.applitools.eyes.config.Configuration;
import com.applitools.eyes.config.ConfigurationProvider;
import com.applitools.eyes.selenium.ClassicRunner;
import com.applitools.eyes.selenium.Eyes;
import com.applitools.eyes.selenium.TestDataProvider;
import com.applitools.eyes.selenium.fluent.Target;
import com.applitools.eyes.selenium.rendering.VisualGridEyes;
import com.applitools.eyes.utils.ReportingTestSuite;
import com.applitools.eyes.utils.SeleniumUtils;
import com.applitools.eyes.utils.TestUtils;
import com.applitools.eyes.visualgrid.services.VisualGridRunner;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;
import java.util.concurrent.Future;

public class TestApiMethods extends ReportingTestSuite {

    public TestApiMethods() {
        super.setGroupName("selenium");
    }

    @Test(dataProvider = "booleanDP", dataProviderClass = TestDataProvider.class)
    public void TestCloseAsync(boolean useVisualGrid) {
        super.addSuiteArg("isVisualGrid", useVisualGrid);
        WebDriver driver = SeleniumUtils.createChromeDriver();
        EyesRunner runner = useVisualGrid ? new VisualGridRunner(10) : new ClassicRunner();
        runner.setLogHandler(new StdoutLogHandler());
        Eyes eyes = new Eyes(runner);
        eyes.setBatch(TestDataProvider.batchInfo);
        try {
            driver.get("https://applitools.com/helloworld");
            eyes.open(driver, "TestApiMethods", "TestCloseAsync_1", new RectangleSize(800, 600));
            eyes.check(Target.window().withName("step 1"));
            eyes.closeAsync();
            driver.findElement(By.tagName("button")).click();
            eyes.open(driver, "TestApiMethods", "TestCloseAsync_2", new RectangleSize(800, 600));
            eyes.check(Target.window().withName("step 2"));
            eyes.closeAsync();
            runner.getAllTestResults();
        } finally {
            driver.quit();
        }
    }

    @Test
    public void TestWaitBeforeClose() throws InterruptedException {
        final Configuration configuration = new Configuration();
        VisualGridRunner runner = new VisualGridRunner(10);
        VisualGridEyes eyes = new VisualGridEyes(runner, new ConfigurationProvider() {
            @Override
            public Configuration get() {
                return configuration;
            }
        });
        eyes.setLogHandler(new StdoutLogHandler());

        WebDriver driver = SeleniumUtils.createChromeDriver();
        driver.get("https://applitools.com/helloworld");
        try {
            eyes.open(driver, "TestApiMethods", "TestWaitBeforeClose", new RectangleSize(800, 600));
            eyes.check(Target.window());
            Thread.sleep(30000);
            List<Future<TestResultContainer>> futureList = (List<Future<TestResultContainer>>) eyes.closeAsync();
            Assert.assertEquals(1, futureList.size());
            Assert.assertNotNull(futureList.get(0));
        } finally {
            driver.quit();
            eyes.abortAsync();
            runner.getAllTestResultsImpl();
        }
    }

    @Test
    public void TestGetHostApp() {
        final String TEST_HOST_APP = "TestHostApp";

        Eyes eyes = new Eyes();
        Assert.assertNull(eyes.getHostApp());

        eyes.setHostApp(TEST_HOST_APP);
        Assert.assertEquals(eyes.getHostApp(), TEST_HOST_APP);
    }

    @Test
    public void TestCloseNoOpen() {
        Eyes eyes = new Eyes();
        eyes.setLogHandler(new StdoutLogHandler());
        TestResults results = eyes.close(false);
        results.delete();
    }
}
