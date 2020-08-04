package com.applitools.eyes.renderingGrid;

import com.applitools.connectivity.ServerConnector;
import com.applitools.eyes.EyesRunner;
import com.applitools.eyes.TestResults;
import com.applitools.eyes.TestResultsSummary;
import com.applitools.eyes.metadata.SessionResults;
import com.applitools.eyes.selenium.Configuration;
import com.applitools.eyes.selenium.Eyes;
import com.applitools.eyes.selenium.TestDataProvider;
import com.applitools.eyes.selenium.fluent.Target;
import com.applitools.eyes.utils.SeleniumTestUtils;
import com.applitools.eyes.utils.SeleniumUtils;
import com.applitools.eyes.utils.TestUtils;
import com.applitools.eyes.visualgrid.model.IosDeviceInfo;
import com.applitools.eyes.visualgrid.model.IosDeviceName;
import com.applitools.eyes.visualgrid.model.RenderRequest;
import com.applitools.eyes.visualgrid.services.VisualGridRunner;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.spy;


public class TestMobileDevices {
    @Test
    public void TestIosDeviceReportedResolutionOnFailure() throws IOException {
        ServerConnector serverConnector = spy(ServerConnector.class);
        doThrow(new IllegalStateException()).when(serverConnector).render(any(RenderRequest.class));

        EyesRunner runner = new VisualGridRunner(10);
        Eyes eyes = new Eyes(runner);
        SeleniumTestUtils.setupLogging(eyes);
        eyes.setServerConnector(serverConnector);

        Configuration config = eyes.getConfiguration();
        config.addBrowser(new IosDeviceInfo(IosDeviceName.iPhone_11_Pro));
        //config.addBrowser(new IosDeviceInfo(IosDeviceName.iPhone_XR));
        config.setBatch(TestDataProvider.batchInfo);
        config.setAppName("Visual Grid Java Tests");
        config.setTestName("UFG Mobile Device No Result");
        eyes.setConfiguration(config);

        WebDriver driver = SeleniumUtils.createChromeDriver();
        driver.get("https://demo.applitools.com");
        try {
            eyes.open(driver);
            eyes.check(Target.window());
            eyes.closeAsync();
        }
        finally {
            driver.quit();
            eyes.abortAsync();
        }
        TestResultsSummary allTestResults = runner.getAllTestResults(false);
        Assert.assertEquals(2, allTestResults.getAllResults().length);
        TestResults resultSanity = allTestResults.getAllResults()[0].getTestResults();
    }
}
