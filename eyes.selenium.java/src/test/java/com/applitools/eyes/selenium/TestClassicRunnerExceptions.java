package com.applitools.eyes.selenium;

import com.applitools.eyes.EyesRunner;
import com.applitools.eyes.RectangleSize;
import com.applitools.eyes.TestResultsSummary;
import com.applitools.eyes.utils.SeleniumUtils;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.annotations.Test;

public class TestClassicRunnerExceptions {
    @Test
    public void TestExceptionInGetAllTestResults() {
        final EyesRunner runner = new ClassicRunner();
        Eyes eyes = new Eyes(runner);
        eyes.setSaveNewTests(false);
        WebDriver driver = SeleniumUtils.createChromeDriver();
        driver.get("https://applitools.com/helloworld?diff1");

        eyes.open(driver,
                TestClassicRunnerExceptions.class.getSimpleName(),
                "TestExceptionInGetAllTestResults",
                new RectangleSize(800, 600));
        eyes.checkWindow();
        eyes.closeAsync();

        driver.quit();
        Assert.assertThrows(new Assert.ThrowingRunnable() {
            @Override
            public void run() throws Throwable {
                TestResultsSummary results = runner.getAllTestResults();
            }
        });
    }
}
