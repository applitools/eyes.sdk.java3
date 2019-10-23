package com.applitools.eyes.renderingGrid;

import com.applitools.eyes.*;
import com.applitools.eyes.selenium.BrowserType;
import com.applitools.eyes.selenium.Configuration;
import com.applitools.eyes.selenium.Eyes;
import com.applitools.eyes.selenium.fluent.Target;
import com.applitools.eyes.utils.SeleniumUtils;
import com.applitools.eyes.utils.TestUtils;
import com.applitools.eyes.visualgrid.services.VisualGridRunner;
import com.applitools.utils.GeneralUtils;
import org.openqa.selenium.WebDriver;
import org.testng.ITestContext;
import org.testng.annotations.*;

import java.net.MalformedURLException;
import java.net.URL;

public class TestTopSites {
    public static BatchInfo batch = new BatchInfo("TTS");
    private EyesRunner visualGridRunner;

    @BeforeClass
    public void beforeClass() {
        visualGridRunner = new VisualGridRunner(10);
        LogHandler logHandler = new FileLogger("eyes.log", false, true);//TestUtils.initLogger("TestTopSites");
        visualGridRunner.setLogHandler(logHandler);
        visualGridRunner.getLogger().log("enter");
    }

    @DataProvider(name = "dp", parallel = true)
    public static Object[][] dp() {
        return new Object[][]{
                {"https://amazon.com"},
//                {"https://ebay.com"},
                {"https://twitter.com"},
                {"https://wikipedia.org"},
                {"https://instagram.com"},
                {"https://www.target.com/c/blankets-throws/-/N-d6wsb?lnk=ThrowsBlankets%E2%80%9C,tc"},
        };
    }

    private Eyes initEyes(WebDriver webDriver, String testedUrl) {
        Eyes eyes = new Eyes(visualGridRunner);
        eyes.setMatchLevel(MatchLevel.LAYOUT);
        Logger logger = eyes.getLogger();

        try {
            URL url = new URL(testedUrl);
            TestUtils.setupLogging(eyes, url.getHost());
        } catch (MalformedURLException e) {
            GeneralUtils.logExceptionStackTrace(logger, e);
        }

        logger.log("creating WebDriver: " + testedUrl);

        try {
            Configuration configuration = new Configuration();
            configuration.setTestName("Top 5 websites - " + testedUrl);
            configuration.setAppName("Top Five Sites");
            configuration.setBatch(TestTopSites.batch);
            configuration.setBranchName("TTS - config branch");
            configuration.setIgnoreDisplacements(true);
            configuration.addBrowser(800, 600, BrowserType.CHROME);
            configuration.addBrowser(700, 500, BrowserType.FIREFOX);
            configuration.addBrowser(700, 500, BrowserType.IE_10);
            configuration.addBrowser(700, 500, BrowserType.IE_11);
            configuration.addBrowser(1600, 1200, BrowserType.CHROME);
            configuration.addBrowser(1200, 800, BrowserType.EDGE);
//            configuration.addDeviceEmulation(emulation);
            logger.log("created configurations for url " + testedUrl);
            //VisualGridEyes.setServerUrl("https://eyes.applitools.com/");
//            configuration.setProxy(new ProxySettings("http://127.0.0.1", 8888, null, null));
            eyes.setConfiguration(configuration);
            eyes.open(webDriver);
        } catch (Exception e) {
            GeneralUtils.logExceptionStackTrace(logger, e);
        }
        return eyes;
    }

    @Test(dataProvider = "dp")
    public void test(String testedUrl) {
        visualGridRunner.getLogger().log("entering with url " + testedUrl);
        WebDriver webDriver = SeleniumUtils.createChromeDriver();
        webDriver.get(testedUrl);
        Eyes eyes = initEyes(webDriver, testedUrl);
        Logger logger = eyes.getLogger();
        logger.log("navigated to " + testedUrl);

        try {
            //CheckRGSettings setting = new CheckRGSettings(CheckRGSettings.SizeMode.FULL_PAGE, null, null, false);
            logger.log("running check for url " + testedUrl);
            try {
                eyes.check(Target.window().withName("Step1 - " + testedUrl).sendDom(true).useDom(true));
                eyes.check(Target.window().fully(false).withName("Step2 - " + testedUrl).sendDom(true).useDom(true));
            } catch (Exception e) {
                e.printStackTrace();
            }
            eyes.getLogger().log("calling VisualGridEyes.close() for url " + testedUrl);
//            VisualGridEyes.close();
//            TestResults close = eyes.close(true);
//            Assert.assertNotNull(close);
            eyes.closeAsync();
            logger.log("end of `try` block for url " + testedUrl);
        } catch (Exception e) {
            GeneralUtils.logExceptionStackTrace(logger, e);
        } finally {
            logger.log("closing WebDriver for url " + testedUrl);
            webDriver.quit();
            logger.log("url " + testedUrl + " - done with browser.");
            // End the test.
        }
    }

    @AfterMethod
    public void afterMethod(ITestContext testContext) {
        visualGridRunner.getLogger().log("enter");
    }

    @AfterClass
    public void afterClass(ITestContext testContext) {
        TestResultsSummary allTestResults = visualGridRunner.getAllTestResults();
        for (TestResultContainer allTestResult : allTestResults) {
            System.out.println(allTestResult.toString());
        }
        visualGridRunner.getLogger().log(allTestResults.toString());
    }
}
