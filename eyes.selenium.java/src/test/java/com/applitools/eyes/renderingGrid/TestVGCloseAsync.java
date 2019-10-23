package com.applitools.eyes.renderingGrid;

import com.applitools.eyes.EyesRunner;
import com.applitools.eyes.TestResultContainer;
import com.applitools.eyes.selenium.BrowserType;
import com.applitools.eyes.selenium.Configuration;
import com.applitools.eyes.selenium.Eyes;
import com.applitools.eyes.selenium.TestDataProvider;
import com.applitools.eyes.selenium.rendering.VisualGridEyes;
import com.applitools.eyes.utils.SeleniumUtils;
import com.applitools.eyes.utils.TestUtils;
import com.applitools.eyes.visualgrid.model.RenderBrowserInfo;
import com.applitools.eyes.visualgrid.services.VisualGridRunner;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.Future;

public class TestVGCloseAsync {

    @Test
    public void TestCloseAsync() {
        EyesRunner runner = new VisualGridRunner(10);
        Eyes eyes = new Eyes(runner);
        WebDriver driver = SeleniumUtils.createChromeDriver();
        driver.get("https://applitools.com/helloworld");
        try {
            Configuration config = new Configuration();
            config.setAppName("Visual Grid Tests").setTestName("Test CloseAsync").setBatch(TestDataProvider.batchInfo);
            for (BrowserType b : BrowserType.values()) {
                config.addBrowser(800, 600, b);
            }
            eyes.setConfiguration(config);
            List<RenderBrowserInfo> combinations = config.getBrowsersInfo();
            Assert.assertTrue(combinations.size() > 1);
            eyes.open(driver);
            eyes.checkWindow();
            driver.quit();
            driver = null;
            VisualGridEyes visualGridEyes = (VisualGridEyes) TestUtils.getFieldValue(eyes, "visualGridEyes");
            Collection<Future<TestResultContainer>> closeTasks = visualGridEyes.closeAsync();
            Assert.assertEquals(closeTasks.size(), combinations.size());
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
