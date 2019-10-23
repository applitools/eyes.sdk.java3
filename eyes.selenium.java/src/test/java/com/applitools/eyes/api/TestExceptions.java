package com.applitools.eyes.api;

import com.applitools.eyes.EyesException;
import com.applitools.eyes.EyesRunner;
import com.applitools.eyes.TestResults;
import com.applitools.eyes.selenium.ClassicRunner;
import com.applitools.eyes.selenium.Configuration;
import com.applitools.eyes.selenium.Eyes;
import com.applitools.eyes.selenium.TestDataProvider;
import com.applitools.eyes.utils.SeleniumUtils;
import com.applitools.eyes.visualgrid.services.VisualGridRunner;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.annotations.Test;

public class TestExceptions {

    @Test(dataProvider = "booleanDP", dataProviderClass = TestDataProvider.class)
    public void TestEyesExceptions(boolean useVisualGrid) {
        final WebDriver driver = SeleniumUtils.createChromeDriver();
        final EyesRunner runner = useVisualGrid ? new VisualGridRunner(10) : new ClassicRunner();
        final Eyes eyes = new Eyes(runner);
        try {
            eyes.setApiKey("");
            EyesException ex1 = Assert.expectThrows(EyesException.class, new Assert.ThrowingRunnable() {
                @Override
                public void run() throws Throwable {
                    eyes.open(driver);
                }
            });

            Assert.assertEquals(ex1.getMessage(), "API key not set! Log in to https://applitools.com to obtain your API key and use the 'Eyes.ApiKey' property to set it.");

            eyes.setApiKey("someAPIkey");
            IllegalArgumentException ex2 = Assert.expectThrows(IllegalArgumentException.class, new Assert.ThrowingRunnable() {
                @Override
                public void run() throws Throwable {
                    eyes.open(driver);
                }
            });

            Assert.assertEquals(ex2.getMessage(), (useVisualGrid ? "appName" : "appIdOrName") + " is null");

            Configuration conf = new Configuration();
            conf.setAppName("");
            eyes.setConfiguration(conf);
            IllegalArgumentException ex3 = Assert.expectThrows(IllegalArgumentException.class, new Assert.ThrowingRunnable() {
                @Override
                public void run() throws Throwable {
                    eyes.open(driver);
                }
            });
            Assert.assertEquals(ex3.getMessage(), (useVisualGrid ? "appName" : "appIdOrName") + " is null");

            conf.setAppName("app");
            eyes.setConfiguration(conf);

            IllegalArgumentException ex4 = Assert.expectThrows(IllegalArgumentException.class, new Assert.ThrowingRunnable() {
                @Override
                public void run() throws Throwable {
                    eyes.open(driver);
                }
            });
            Assert.assertEquals(ex4.getMessage(), (useVisualGrid ? "testName" : "scenarioIdOrName") + " is null");

            conf.setTestName("");
            eyes.setConfiguration(conf);


            IllegalArgumentException ex5 = Assert.expectThrows(IllegalArgumentException.class, new Assert.ThrowingRunnable() {
                @Override
                public void run() throws Throwable {
                    eyes.open(driver);
                }
            });
            Assert.assertEquals(ex5.getMessage(), (useVisualGrid ? "testName" : "scenarioIdOrName") + " is null");

            conf.setTestName("test");
            eyes.setConfiguration(conf);
            eyes.open(driver);
            if (useVisualGrid) {

                IllegalStateException ex6 = Assert.expectThrows(IllegalStateException.class, new Assert.ThrowingRunnable() {
                    @Override
                    public void run() throws Throwable {
                        TestResults results = eyes.close();
                        if (results != null) {
                            results.delete();
                        }
                        runner.getAllTestResults();
                    }
                });
                Assert.assertEquals(ex6.getMessage(), "Eyes not open");

            } else {
                TestResults results = eyes.close();
                if (results != null) {
                    results.delete();
                }
                runner.getAllTestResults();
            }
        } finally {
            eyes.abort();
            driver.quit();
        }
    }
}
