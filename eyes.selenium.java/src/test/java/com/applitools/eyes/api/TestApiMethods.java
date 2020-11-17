package com.applitools.eyes.api;

import com.applitools.eyes.EyesRunner;
import com.applitools.eyes.RectangleSize;
import com.applitools.eyes.StdoutLogHandler;
import com.applitools.eyes.TestResults;
import com.applitools.eyes.selenium.ClassicRunner;
import com.applitools.eyes.selenium.Eyes;
import com.applitools.eyes.selenium.TestDataProvider;
import com.applitools.eyes.selenium.fluent.Target;
import com.applitools.eyes.utils.ReportingTestSuite;
import com.applitools.eyes.utils.SeleniumUtils;
import com.applitools.eyes.visualgrid.services.VisualGridRunner;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.annotations.Test;

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
