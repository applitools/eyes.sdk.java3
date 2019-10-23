package com.applitools.eyes.renderingGrid;

import com.applitools.eyes.BatchInfo;
import com.applitools.eyes.RectangleSize;
import com.applitools.eyes.selenium.Configuration;
import com.applitools.eyes.selenium.Eyes;
import com.applitools.eyes.selenium.fluent.Target;
import com.applitools.eyes.utils.SeleniumUtils;
import com.applitools.eyes.visualgrid.services.VisualGridRunner;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.annotations.Test;

public class TestVGWithBadWebhook {
    @Test
    public void Test() {
        WebDriver driver = SeleniumUtils.createChromeDriver();
        driver.get("https://applitools.com/helloworld");

        BatchInfo batch = new BatchInfo("Visual Grid - Test bad webhook");
        final VisualGridRunner runner = new VisualGridRunner(10);

        final Eyes eyes = new Eyes(runner);

        Configuration config = new Configuration();
        config.setBatch(batch);
        config.setAppName("Visual Grid Java Tests");
        config.setTestName("Bad Webhook");
        config.setViewportSize(new RectangleSize(800, 600));

        eyes.setConfiguration(config);
        eyes.open(driver);
        eyes.check(Target.window().fully().beforeRenderScreenshotHook("gibberish uncompilable java script"));
        driver.quit();
        Error ex = Assert.expectThrows(Error.class, new Assert.ThrowingRunnable() {
            @Override
            public void run() throws Throwable {
                eyes.close();
                runner.getAllTestResults();
            }
        });
        Assert.assertNotNull(ex);
        Assert.assertNotNull(ex.getMessage());
        Assert.assertTrue(ex.getMessage().contains(
                "failed to run beforeCaptureScreenshot hook script"),
        "Actual string was: " + ex.getMessage());
    }
}
