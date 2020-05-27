package com.applitools.eyes.renderingGrid;

import com.applitools.eyes.EyesRunner;
import com.applitools.eyes.RectangleSize;
import com.applitools.eyes.StdoutLogHandler;
import com.applitools.eyes.selenium.Eyes;
import com.applitools.eyes.utils.ReportingTestSuite;
import com.applitools.eyes.utils.SeleniumUtils;
import com.applitools.eyes.visualgrid.services.VisualGridRunner;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.Test;

public class TestDownloadResources extends ReportingTestSuite {

    public TestDownloadResources() {
        super.setGroupName("selenium");
    }

    /**
     * Testing a complex case when we need to download resources recursively from a website
     */
    @Test
    public void testDownloadResourcesRecursively() {
        EyesRunner runner = new VisualGridRunner(10);
        Eyes eyes = new Eyes(runner);
        eyes.setLogHandler(new StdoutLogHandler());
        WebDriver driver = SeleniumUtils.createChromeDriver();
        try {
            eyes.open(driver, "Applitools Eyes SDK", "TestDownloadResourcesRecursively", new RectangleSize(800, 800));
            driver.get("https://support.goodrx.com/hc/en-us");
            eyes.checkWindow();
            eyes.closeAsync();
        } finally {
            driver.quit();
            eyes.abortAsync();
            runner.getAllTestResults(false);
        }
    }
}
