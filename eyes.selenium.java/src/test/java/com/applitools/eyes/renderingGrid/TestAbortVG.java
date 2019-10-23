package com.applitools.eyes.renderingGrid;

import com.applitools.eyes.TestResultContainer;
import com.applitools.eyes.TestResultsSummary;
import com.applitools.eyes.selenium.Eyes;
import com.applitools.eyes.selenium.TestDataProvider;
import com.applitools.eyes.utils.SeleniumUtils;
import com.applitools.eyes.visualgrid.services.VisualGridRunner;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.ITestResult;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;

public class TestAbortVG {
    VisualGridRunner runner = new VisualGridRunner(3);
    private Eyes eyes;

    @Test
    public void test() {
        eyes = new Eyes(runner);
        eyes.setBatch(TestDataProvider.batchInfo);
        ChromeDriver driver = SeleniumUtils.createChromeDriver();
        driver.get("http://google.com");
        eyes.open(driver, "Java SDK", "Test Abort");
        eyes.checkWindow();
        driver.close();
    }

    @AfterMethod
    public void after(ITestResult testContext) {
        eyes.abortIfNotClosed();
    }

    @AfterClass
    public void afterClass() {
        TestResultsSummary allTestResults = runner.getAllTestResults();
        for (TestResultContainer allTestResult : allTestResults) {
            if (!allTestResult.getTestResults().isAborted()) {
                throw new Error("Test not aborted");
            }
        }
    }
}
