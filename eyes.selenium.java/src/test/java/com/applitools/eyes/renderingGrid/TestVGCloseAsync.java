package com.applitools.eyes.renderingGrid;

import com.applitools.eyes.EyesRunner;
import com.applitools.eyes.selenium.BrowserType;
import com.applitools.eyes.selenium.Configuration;
import com.applitools.eyes.selenium.Eyes;
import com.applitools.eyes.selenium.TestDataProvider;
import com.applitools.eyes.selenium.rendering.VisualGridEyes;
import com.applitools.eyes.utils.SeleniumUtils;
import com.applitools.eyes.utils.TestUtils;
import com.applitools.eyes.visualgrid.services.VisualGridRunner;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.Test;

public class TestVGCloseAsync {

    @Test
    public void TestCloseAsync() {
        EyesRunner runner = new VisualGridRunner(10);
        Eyes eyes = new Eyes(runner);
        TestUtils.setupLogging(eyes);
        WebDriver driver = SeleniumUtils.createChromeDriver();
        driver.get("https://applitools.com/helloworld");
        try {
            Configuration config = new Configuration();
            config.setAppName("Visual Grid Tests").setTestName("Test CloseAsync").setBatch(TestDataProvider.batchInfo);
            config.addBrowser(800, 600, BrowserType.CHROME);
            eyes.setConfiguration(config);
            eyes.open(driver);
            eyes.checkWindow("step 1");
            driver.findElement(By.tagName("button")).click();
            eyes.checkWindow("step 2");
            driver.quit();
            driver = null;
            VisualGridEyes visualGridEyes = (VisualGridEyes) TestUtils.getFieldValue(eyes, "visualGridEyes");
            visualGridEyes.closeAsync();
            runner.getAllTestResults();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (driver != null) {
                driver.quit();
            }
            eyes.abort();
        }
    }
}
