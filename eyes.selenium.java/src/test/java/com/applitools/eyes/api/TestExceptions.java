package com.applitools.eyes.api;

import com.applitools.eyes.EyesRunner;
import com.applitools.eyes.StdoutLogHandler;
import com.applitools.eyes.config.Configuration;
import com.applitools.eyes.selenium.ClassicRunner;
import com.applitools.eyes.selenium.Eyes;
import com.applitools.eyes.selenium.TestDataProvider;
import com.applitools.eyes.utils.ReportingTestSuite;
import com.applitools.eyes.utils.SeleniumUtils;
import com.applitools.eyes.visualgrid.services.VisualGridRunner;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.annotations.Test;

public class TestExceptions extends ReportingTestSuite {

    public TestExceptions() {
        super.setGroupName("selenium");
    }

    @Test(dataProvider = "booleanDP", dataProviderClass = TestDataProvider.class)
    public void TestEyesExceptions(boolean useVisualGrid) {
        super.addSuiteArg("isVisualGrid", useVisualGrid);
        final WebDriver driver = SeleniumUtils.createChromeDriver();
        final EyesRunner runner = useVisualGrid ? new VisualGridRunner(10) : new ClassicRunner();
        final Eyes eyes = new Eyes(runner);
        eyes.setLogHandler(new StdoutLogHandler());
        try {
            IllegalArgumentException ex1 = Assert.expectThrows(IllegalArgumentException.class, new Assert.ThrowingRunnable() {
                @Override
                public void run() {
                    eyes.open(driver);
                }
            });
            Assert.assertEquals(ex1.getMessage(), "appIdOrName is null");

            Configuration conf = new Configuration();
            conf.setAppName("");
            eyes.setConfiguration(conf);
            IllegalArgumentException ex2 = Assert.expectThrows(IllegalArgumentException.class, new Assert.ThrowingRunnable() {
                @Override
                public void run() {
                    eyes.open(driver);
                }
            });
            Assert.assertEquals(ex2.getMessage(), "appIdOrName is empty");

            conf.setAppName("app");
            eyes.setConfiguration(conf);
            IllegalArgumentException ex3 = Assert.expectThrows(IllegalArgumentException.class, new Assert.ThrowingRunnable() {
                @Override
                public void run() {
                    eyes.open(driver);
                }
            });
            Assert.assertEquals(ex3.getMessage(), "scenarioIdOrName is null");

            conf.setTestName("");
            eyes.setConfiguration(conf);
            IllegalArgumentException ex4 = Assert.expectThrows(IllegalArgumentException.class, new Assert.ThrowingRunnable() {
                @Override
                public void run() {
                    eyes.open(driver);
                }
            });
            Assert.assertEquals(ex4.getMessage(), "scenarioIdOrName is empty");

            conf.setTestName("test");
            eyes.setConfiguration(conf);
            eyes.open(driver);
            eyes.closeAsync();
        } finally {
            driver.quit();
            eyes.abortAsync();
            runner.getAllTestResults();
        }
    }
}
