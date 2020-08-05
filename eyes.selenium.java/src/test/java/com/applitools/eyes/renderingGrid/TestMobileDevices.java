package com.applitools.eyes.renderingGrid;

import com.applitools.connectivity.ServerConnector;
import com.applitools.eyes.EyesRunner;
import com.applitools.eyes.RectangleSize;
import com.applitools.eyes.TestResults;
import com.applitools.eyes.TestResultsSummary;
import com.applitools.eyes.selenium.Configuration;
import com.applitools.eyes.selenium.Eyes;
import com.applitools.eyes.selenium.TestDataProvider;
import com.applitools.eyes.selenium.fluent.Target;
import com.applitools.eyes.utils.ReportingTestSuite;
import com.applitools.eyes.utils.SeleniumTestUtils;
import com.applitools.eyes.utils.SeleniumUtils;
import com.applitools.eyes.visualgrid.model.IosDeviceInfo;
import com.applitools.eyes.visualgrid.model.IosDeviceName;
import com.applitools.eyes.visualgrid.model.RenderRequest;
import com.applitools.eyes.visualgrid.services.VisualGridRunner;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.annotations.Test;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.spy;

public class TestMobileDevices extends ReportingTestSuite {

    public TestMobileDevices() {
        super.setGroupName("selenium");
    }

    @Test
    public void testIosDeviceReportedResolutionOnFailure() {
        ServerConnector serverConnector = spy(ServerConnector.class);
        doThrow(new IllegalStateException()).when(serverConnector).render(any(RenderRequest.class));

        EyesRunner runner = new VisualGridRunner(10);
        Eyes eyes = new Eyes(runner);
        SeleniumTestUtils.setupLogging(eyes);
        eyes.setServerConnector(serverConnector);

        Configuration config = eyes.getConfiguration();
        config.addBrowser(new IosDeviceInfo(IosDeviceName.iPhone_11_Pro));
        config.addBrowser(new IosDeviceInfo(IosDeviceName.iPhone_XR));
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
        } finally {
            driver.quit();
            eyes.abortAsync();
        }
        TestResultsSummary allTestResults = runner.getAllTestResults(false);
        Assert.assertEquals(2, allTestResults.getAllResults().length);
        TestResults result1 = allTestResults.getAllResults()[0].getTestResults();
        TestResults result2 = allTestResults.getAllResults()[1].getTestResults();

        RectangleSize r1 = result1.getHostDisplaySize();
        RectangleSize r2 = result2.getHostDisplaySize();
        Assert.assertNotEquals(r1.getWidth(), 0);
        Assert.assertNotEquals(r1.getHeight(), 0);
        Assert.assertNotEquals(r2.getWidth(), 0);
        Assert.assertNotEquals(r2.getHeight(), 0);
        if (r1.getWidth() < r2.getWidth()) {
            r1 = result2.getHostDisplaySize();
            r2 = result1.getHostDisplaySize();
        }
        Assert.assertEquals(r1.getWidth(), 414);
        Assert.assertEquals(r1.getHeight(), 896);
        Assert.assertEquals(r2.getWidth(), 375);
        Assert.assertEquals(r2.getHeight(), 812);
    }
}
