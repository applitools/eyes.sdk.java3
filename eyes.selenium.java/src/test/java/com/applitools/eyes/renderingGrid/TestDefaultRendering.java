package com.applitools.eyes.renderingGrid;

import com.applitools.eyes.TestResultContainer;
import com.applitools.eyes.TestResultsSummary;
import com.applitools.eyes.selenium.BrowserType;
import com.applitools.eyes.selenium.Configuration;
import com.applitools.eyes.selenium.Eyes;
import com.applitools.eyes.selenium.fluent.Target;
import com.applitools.eyes.utils.SeleniumUtils;
import com.applitools.eyes.visualgrid.services.VisualGridRunner;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.annotations.Test;

public class TestDefaultRendering {
    @Test
    public void TestDefaultRenderingOfMultipleTargets() {
        WebDriver driver = SeleniumUtils.createChromeDriver();
        driver.get("https://applitools.com/helloworld");
        VisualGridRunner runner = new VisualGridRunner(10);
        Eyes eyes = new Eyes(runner);
        Configuration config = eyes.getConfiguration();
        config.addBrowser(800, 600, BrowserType.CHROME);
        config.addBrowser(800, 600, BrowserType.FIREFOX);
        config.addBrowser(1200, 800, BrowserType.CHROME);
        config.addBrowser(1200, 800, BrowserType.FIREFOX);
        config.setAppName("TestDefaultRendering").setTestName("TestDefaultRenderingOfMultipleTargets");
        eyes.setConfiguration(config);
        try {
            eyes.open(driver);
            eyes.check(Target.window());
            eyes.close();
        } finally {
            eyes.abort();
            driver.quit();
        }
        TestResultsSummary allTestResults = runner.getAllTestResults();
        String batchId = null;
        String batchName = null;
        for (TestResultContainer trc : allTestResults) {
            if (batchId == null) batchId = trc.getTestResults().getBatchId();
            if (batchName == null) batchName = trc.getTestResults().getBatchName();
            Assert.assertEquals(batchId, trc.getTestResults().getBatchId());
            Assert.assertEquals(batchName, trc.getTestResults().getBatchName());
        }
    }
}
