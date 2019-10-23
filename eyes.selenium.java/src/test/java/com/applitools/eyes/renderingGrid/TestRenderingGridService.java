package com.applitools.eyes.renderingGrid;

import com.applitools.eyes.BatchInfo;
import com.applitools.eyes.StdoutLogHandler;
import com.applitools.eyes.TestResults;
import com.applitools.eyes.TestResultsSummary;
import com.applitools.eyes.selenium.BrowserType;
import com.applitools.eyes.selenium.Configuration;
import com.applitools.eyes.selenium.Eyes;
import com.applitools.eyes.selenium.fluent.Target;
import com.applitools.eyes.utils.SeleniumUtils;
import com.applitools.eyes.utils.TestUtils;
import com.applitools.eyes.visualgrid.model.FileDebugResourceWriter;
import com.applitools.eyes.visualgrid.services.VisualGridRunner;
import com.applitools.utils.GeneralUtils;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public final class TestRenderingGridService {

    private VisualGridRunner renderingManager;
    private WebDriver webDriver;

    @BeforeMethod
    public void Before(ITestContext testContext) {
        renderingManager = new VisualGridRunner(3);
        renderingManager.setLogHandler(new StdoutLogHandler(TestUtils.verboseLogs));

        String path = TestUtils.initLogPath("TestRenderingGridService");
        FileDebugResourceWriter fileDebugResourceWriter = new FileDebugResourceWriter(renderingManager.getLogger(), path, null, null);
        renderingManager.setDebugResourceWriter(fileDebugResourceWriter);

        webDriver = SeleniumUtils.createChromeDriver();
        webDriver.get("https://applitools.github.io/demo/TestPages/VisualGridTestPage");
        //webDriver.get("http://applitools-vg-test.surge.sh/test.html");

        System.setProperty("https.protocols", "TLSv1,TLSv1.1,TLSv1.2");
    }

    @Test
    public void test() {

        Eyes eyes = new Eyes(renderingManager);
        eyes.setBatch(new BatchInfo("SimpleVisualGridBatch"));

        try {
            Configuration configuration = new Configuration();
            configuration.setTestName("Open Concurrency with Batch 2");
            configuration.setAppName("RenderingGridIntegration");
            configuration.addBrowser(800, 600, BrowserType.CHROME);
            configuration.addBrowser(700, 500, BrowserType.CHROME);
            configuration.addBrowser(400, 300, BrowserType.CHROME);
//            eyes.setProxy(new ProxySettings("http://127.0.0.1", 8888, null, null));
            //VisualGridEyes.setServerUrl("https://eyes.applitools.com/");
            eyes.setConfiguration(configuration);
            eyes.open(webDriver);
            //CheckRGSettings setting = new CheckRGSettings(CheckRGSettings.SizeMode.FULL_PAGE, null, null, false);
            eyes.check(Target.window().withName("test").fully(false).sendDom(false));
            TestResults close = eyes.close();
            Assert.assertNotNull(close);

        } catch (Exception e) {
            GeneralUtils.logExceptionStackTrace(eyes.getLogger(), e);
        } finally {
            if (webDriver != null) {
                webDriver.quit();
            }
            TestResultsSummary allTestResults = renderingManager.getAllTestResults();
            System.out.println(allTestResults);
            // End the test.
        }
    }

    @Test
    public void testShadowDom() {

        Eyes eyes = new Eyes(renderingManager);
        eyes.setBatch(new BatchInfo("Visual Grid Shadow Dom Test"));

        try {
            Configuration configuration = new Configuration();
            configuration.setTestName("Visual Grid Shadow Dom Test");
            configuration.setAppName("Visual Grid Shadow Dom Test");
            configuration.addBrowser(1200, 800, BrowserType.CHROME);
            configuration.addBrowser(1200, 800, BrowserType.FIREFOX);
            configuration.addBrowser(1200, 800, BrowserType.EDGE);
            configuration.addBrowser(1200, 800, BrowserType.IE_10);
            configuration.addBrowser(1200, 800, BrowserType.IE_11);
//            eyes.setProxy(new ProxySettings("http://127.0.0.1", 8888, null, null));
            //VisualGridEyes.setServerUrl("https://eyes.applitools.com/");
            eyes.setConfiguration(configuration);
            eyes.open(webDriver);
            webDriver.get("https://applitools.github.io/demo/TestPages/DomTest/shadow_dom.html");
            Thread.sleep(500);
            //CheckRGSettings setting = new CheckRGSettings(CheckRGSettings.SizeMode.FULL_PAGE, null, null, false);
            eyes.check(Target.window());
            TestResults close = eyes.close();
            Assert.assertNotNull(close);

        } catch (Exception e) {
            GeneralUtils.logExceptionStackTrace(eyes.getLogger(), e);
        } finally {
            if (webDriver != null) {
                webDriver.quit();
            }
            TestResultsSummary allTestResults = renderingManager.getAllTestResults();
            System.out.println(allTestResults);
            // End the test.
        }
    }

    @AfterMethod
    public void After(ITestContext testContext) {
        renderingManager.getLogger().log(renderingManager.getAllTestResults().toString());
        webDriver.quit();
    }
}