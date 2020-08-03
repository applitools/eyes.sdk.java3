package com.applitools.eyes.renderingGrid;

import com.applitools.eyes.EyesRunner;
import com.applitools.eyes.TestResults;
import com.applitools.eyes.TestResultsSummary;
import com.applitools.eyes.metadata.SessionResults;
import com.applitools.eyes.selenium.Configuration;
import com.applitools.eyes.selenium.Eyes;
import com.applitools.eyes.selenium.TestDataProvider;
import com.applitools.eyes.selenium.fluent.Target;
import com.applitools.eyes.utils.SeleniumUtils;
import com.applitools.eyes.utils.TestUtils;
import com.applitools.eyes.visualgrid.model.IosDeviceInfo;
import com.applitools.eyes.visualgrid.model.IosDeviceName;
import com.applitools.eyes.visualgrid.services.VisualGridRunner;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;


public class TestMobileDevices {
    @Test
    public void TestIosDeviceReportedResolutionOnFailure() throws IOException {
        EyesRunner runner = new VisualGridRunner(10);
        Eyes eyes = new Eyes(runner);

        Configuration config = eyes.getConfiguration();
        config.addBrowser(new IosDeviceInfo(IosDeviceName.iPhone_11_Pro));
        config.setBatch(TestDataProvider.batchInfo);
        config.setAppName("Visual Grid Java Tests");
        config.setTestName("UFG Mobile Device No Result");
        eyes.setConfiguration(config);

        WebDriver driver = SeleniumUtils.createChromeDriver();
        driver.get("https://demo.applitools.com");
        try {
            eyes.open(driver);
            eyes.check(Target.window().beforeRenderScreenshotHook("gibberish uncompilable java script"));
            eyes.closeAsync();
        }
        finally {
            eyes.abortIfNotClosed();
            driver.quit();
        }
        TestResultsSummary allTestResults = runner.getAllTestResults(false);
        Assert.assertEquals(1, allTestResults.getAllResults().length);
        TestResults resultSanity = allTestResults.getAllResults()[0].getTestResults();
        SessionResults sessionResults = TestUtils.getSessionResults(eyes.getApiKey(), resultSanity);
    }
}
