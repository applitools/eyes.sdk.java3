package com.applitools.eyes.selenium.rendering;

import java.io.IOException;

import com.applitools.eyes.MatchLevel;
import com.applitools.eyes.StdoutLogHandler;
import com.applitools.eyes.TestResultContainer;
import com.applitools.eyes.TestResultsSummary;
import com.applitools.eyes.config.Configuration;
import com.applitools.eyes.metadata.ActualAppOutput;
import com.applitools.eyes.metadata.SessionResults;
import com.applitools.eyes.selenium.BrowserType;
import com.applitools.eyes.selenium.Eyes;
import com.applitools.eyes.selenium.TestDataProvider;
import com.applitools.eyes.utils.ReportingTestSuite;
import com.applitools.eyes.utils.SeleniumUtils;
import com.applitools.eyes.utils.TestUtils;
import com.applitools.eyes.visualgrid.model.RenderBrowserInfo;
import com.applitools.eyes.visualgrid.services.VisualGridRunner;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.annotations.Test;

public class TestVGServerConfigs extends ReportingTestSuite {

    public TestVGServerConfigs() {
        super.setGroupName("selenium");
    }

    @Test
    public void TestVGDoubleCloseNoCheck() {
        WebDriver driver = SeleniumUtils.createChromeDriver();
        final VisualGridRunner runner = new VisualGridRunner(10,"TestVGDoubleCloseNoCheck");
        final Eyes eyes = new Eyes(runner);
        try {
            Configuration conf = new Configuration();
            conf.setAppName("app").setTestName("test");
            conf.setBatch(TestDataProvider.batchInfo);
            eyes.setConfiguration(conf);

            eyes.open(driver);
            eyes.closeAsync();
        } finally {
            driver.quit();
            eyes.abortAsync();
            runner.getAllTestResults();
        }
    }

    @Test
    public void TestVGChangeConfigAfterOpen() throws IOException {
        WebDriver driver = SeleniumUtils.createChromeDriver();
        VisualGridRunner runner = new VisualGridRunner(10);
        Eyes eyes = new Eyes(runner);
        try {
            eyes.setLogHandler(new StdoutLogHandler());
            Configuration conf = eyes.getConfiguration();
            conf.addBrowser(new RenderBrowserInfo(800, 600, BrowserType.CHROME));
            conf.setBatch(TestDataProvider.batchInfo);
            conf.setAccessibilityValidation(null).setIgnoreDisplacements(false);
            eyes.setConfiguration(conf);

            driver.get("https://applitools.com/helloworld");
            eyes.open(driver, "Java Eyes SDK", "Test VG Change Config After Open");
            conf.setIgnoreDisplacements(true);
            eyes.setConfiguration(conf);

            eyes.checkWindow();
            conf.setMatchLevel(MatchLevel.LAYOUT).setIgnoreDisplacements(false);
            eyes.setConfiguration(conf);

            eyes.checkWindow();

            eyes.closeAsync();
        } finally {
            driver.quit();
            eyes.abortAsync();
            TestResultsSummary resultsSummary = runner.getAllTestResults();
            TestResultContainer[] results = resultsSummary.getAllResults();
            SessionResults sessionResults = TestUtils.getSessionResults(eyes.getApiKey(), results[0].getTestResults());
            Assert.assertNotNull(sessionResults);
            Assert.assertEquals(2, sessionResults.getActualAppOutput().length);

            ActualAppOutput output = sessionResults.getActualAppOutput()[0];
            Assert.assertTrue(output.getImageMatchSettings().getIgnoreDisplacements());
            Assert.assertEquals(output.getImageMatchSettings().getMatchLevel(), MatchLevel.STRICT);

            output = sessionResults.getActualAppOutput()[1];
            Assert.assertFalse(output.getImageMatchSettings().getIgnoreDisplacements());
            Assert.assertEquals(output.getImageMatchSettings().getMatchLevel(), MatchLevel.LAYOUT2);
        }
    }
}
