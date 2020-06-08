package com.applitools.eyes.renderingGrid;

import com.applitools.eyes.*;
import com.applitools.eyes.selenium.BrowserType;
import com.applitools.eyes.selenium.Configuration;
import com.applitools.eyes.selenium.Eyes;
import com.applitools.eyes.selenium.fluent.Target;
import com.applitools.eyes.utils.SeleniumUtils;
import com.applitools.eyes.utils.TestUtils;
import com.applitools.eyes.visualgrid.model.DeviceName;
import com.applitools.eyes.visualgrid.model.ScreenOrientation;
import com.applitools.eyes.visualgrid.services.VisualGridRunner;
import com.applitools.utils.GeneralUtils;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.annotations.*;

public class TestVG_EmulationDevices {
    private static final BrowserType CHROME = BrowserType.CHROME;
    private static final BrowserType FIREFOX = BrowserType.FIREFOX;
    private static final DeviceName IPHONE4 = DeviceName.iPhone_4;
    private static final ScreenOrientation PORTRAIT = ScreenOrientation.PORTRAIT;
    private EyesRunner visualGridRunner;

    private BatchInfo batchInfo;
    private LogHandler logHandler;

    @BeforeClass
    public void beforeClass() {
        visualGridRunner = new VisualGridRunner(10);
//        visualGridRunner.setLogHandler(new StdoutLogHandler(true));
        logHandler = TestUtils.initLogger("TestVG_EmulationDevices");
        visualGridRunner.setLogHandler(logHandler);
        visualGridRunner.getLogger().log("enter");
        batchInfo = new BatchInfo("hello world batch");
        batchInfo.setId("hw_VG_Batch_ID");
    }

    @DataProvider(name = "dp", parallel = true)
    public static Object[][] dp() {
        return new Object[][]{
//                {"https://todomvc-app-for-testing.surge.sh/"},
                {"https://google.com"},
                {"https://facebook.com"},
                {"https://youtube.com"},
                {"https://amazon.com"},
//                {"https://ebay.com"},
//                {"https://twitter.com"},
                {"https://wikipedia.org"},
                {"https://instagram.com"},
                {"https://www.target.com/c/blankets-throws/-/N-d6wsb?lnk=ThrowsBlankets%E2%80%9C,tc"},
        };
    }

    private Eyes initEyes(WebDriver webDriver, String testedUrl) {
        Eyes eyes = new Eyes(visualGridRunner);
//        Eyes eyes = new Eyes();
        eyes.setBatch(batchInfo);
        eyes.setLogHandler(logHandler);
        Logger logger = eyes.getLogger();
        logger.log("creating WebDriver: " + testedUrl);

        try {
            Configuration configuration = new Configuration();
            configuration.setTestName("VG hello world - " + testedUrl);
            configuration.setAppName("VG hello world");
            configuration.setBatch(batchInfo);
            configuration.setBaselineEnvName("michael");
//            configuration.addDeviceEmulation(300, 400, 1f);
            configuration.addDeviceEmulation(IPHONE4, PORTRAIT);
            configuration.addDeviceEmulation(DeviceName.iPhone_4, ScreenOrientation.PORTRAIT);
            configuration.addDeviceEmulation(DeviceName.iPhone_X, ScreenOrientation.PORTRAIT);
            configuration.addDeviceEmulation(DeviceName.Nexus_10, ScreenOrientation.LANDSCAPE);
            logger.log("created configurations for url " + testedUrl);
//            eyes.setProxy(new ProxySettings("http://127.0.0.1", 8888, null, null));
            eyes.setConfiguration(configuration);
            eyes.open(webDriver, "Michael's App name", " michael test name");
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
        Eyes eyes = (Eyes) initEyes(webDriver, testedUrl);
        Logger logger = eyes.getLogger();
        logger.log("navigated to " + testedUrl);

        try {
            //CheckRGSettings setting = new CheckRGSettings(CheckRGSettings.SizeMode.FULL_PAGE, null, null, false);
            logger.log("running check for url " + testedUrl);
            try {
                eyes.check(Target.window().withName("Step1 - " + testedUrl).sendDom(true));
                eyes.check(Target.window().fully(false).withName("Step2 - " + testedUrl).sendDom(true));
            } catch (Exception e) {
                e.printStackTrace();
            }
            eyes.getLogger().log("calling VisualGridEyes.close() for url " + testedUrl);
//            VisualGridEyes.close();
            TestResults close = eyes.close(true);
            Assert.assertNotNull(close);
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
        visualGridRunner.getLogger().log(allTestResults.toString());
    }
}
