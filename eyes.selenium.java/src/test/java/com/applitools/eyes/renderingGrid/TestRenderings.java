package com.applitools.eyes.renderingGrid;

import com.applitools.connectivity.ServerConnector;
import com.applitools.eyes.*;
import com.applitools.eyes.config.Configuration;
import com.applitools.eyes.metadata.SessionResults;
import com.applitools.eyes.selenium.BrowserType;
import com.applitools.eyes.selenium.Eyes;
import com.applitools.eyes.selenium.StitchMode;
import com.applitools.eyes.selenium.TestDataProvider;
import com.applitools.eyes.selenium.fluent.Target;
import com.applitools.eyes.utils.ReportingTestSuite;
import com.applitools.eyes.utils.SeleniumUtils;
import com.applitools.eyes.utils.TestUtils;
import com.applitools.eyes.visualgrid.model.*;
import com.applitools.eyes.visualgrid.services.VisualGridRunner;
import com.applitools.utils.GeneralUtils;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class TestRenderings extends ReportingTestSuite {

    public TestRenderings() {
        super.setGroupName("selenium");
    }

    @Test
    public void TestMobileOnly() {
        VisualGridRunner runner = new VisualGridRunner(30);
        Eyes eyes = new Eyes(runner);

        eyes.setLogHandler(TestUtils.initLogger());

        Configuration sconf = new Configuration();
        sconf.setTestName("Mobile Render Test");
        sconf.setAppName("Visual Grid Render Test");
        sconf.setBatch(TestDataProvider.batchInfo);

        sconf.addDeviceEmulation(DeviceName.Galaxy_S5);

        eyes.setConfiguration(sconf);
        ChromeDriver driver = SeleniumUtils.createChromeDriver();
        eyes.open(driver);
        driver.get("https://applitools.github.io/demo/TestPages/DynamicResolution/mobile.html");
        eyes.check("Test Mobile Only", Target.window().fully());
        driver.quit();
        eyes.close();
        TestResultsSummary allResults = runner.getAllTestResults();
    }

    @Test
    public void ViewportsTest() {
        VisualGridRunner runner = new VisualGridRunner(30);
        Eyes eyes = new Eyes(runner);

        eyes.setLogHandler(TestUtils.initLogger());

        Configuration sconf = new Configuration();
        sconf.setBatch(TestDataProvider.batchInfo);
        sconf.setTestName("Viewport Size Test");
        sconf.setAppName("Visual Grid Viewports Test");
        sconf.setHideScrollbars(true);
        sconf.setStitchMode(StitchMode.CSS);
        sconf.setForceFullPageScreenshot(true);
        sconf.setMatchLevel(MatchLevel.STRICT);

        sconf.addBrowser(800, 600, BrowserType.CHROME);
        sconf.addBrowser(700, 500, BrowserType.CHROME);
        sconf.addBrowser(1200, 800, BrowserType.CHROME);
        sconf.addBrowser(1600, 1200, BrowserType.CHROME);
        sconf.addBrowser(800, 600, BrowserType.FIREFOX);
        sconf.addBrowser(700, 500, BrowserType.FIREFOX);
        sconf.addBrowser(1200, 800, BrowserType.FIREFOX);
        sconf.addBrowser(1600, 1200, BrowserType.FIREFOX);
        sconf.addBrowser(800, 600, BrowserType.EDGE);
        sconf.addBrowser(700, 500, BrowserType.EDGE);
        sconf.addBrowser(1200, 800, BrowserType.EDGE);
        //sconf.AddBrowser(1600, 1200, BrowserType.EDGE);
        sconf.addBrowser(800, 600, BrowserType.IE_11);
        sconf.addBrowser(700, 500, BrowserType.IE_11);
        sconf.addBrowser(1200, 800, BrowserType.IE_11);
        //sconf.AddBrowser(1600, 1200, BrowserType.IE_11);
        sconf.addBrowser(800, 600, BrowserType.IE_10);
        sconf.addBrowser(700, 500, BrowserType.IE_10);
        sconf.addBrowser(1200, 800, BrowserType.IE_10);
        //sconf.AddBrowser(1600, 1200, BrowserType.IE_10);
        eyes.setConfiguration(sconf);

        ChromeDriver driver = SeleniumUtils.createChromeDriver();
        eyes.open(driver);
        driver.get("https://www.applitools.com");
        eyes.check("Test Viewport", Target.window().fully());
        driver.quit();

        TestResultsSummary allResults = runner.getAllTestResults(false);
        Assert.assertTrue(sconf.getBrowsersInfo().size() > BrowserType.values().length);
        Assert.assertEquals(allResults.size(), sconf.getBrowsersInfo().size());

        Map<String, Set<RectangleSize>> results = new HashMap<>();
        for (TestResultContainer testResultContainer : allResults) {
            Assert.assertNotNull(testResultContainer, "testResultContainer");
            SessionResults sessionResults = null;
            try {
                sessionResults = TestUtils.getSessionResults(eyes.getApiKey(), testResultContainer.getTestResults());
            } catch (IOException e) {
                GeneralUtils.logExceptionStackTrace(eyes.getLogger(), e);
            }

            if (sessionResults == null) {
                eyes.getLogger().log("Error: sessionResults is null for item " + testResultContainer);
                continue;
            }
            AppEnvironment env = sessionResults.getEnv();
            String browser = env.getHostingAppInfo();
            if (browser == null) {
                eyes.log("Error: HostingAppInfo (browser) is null. " + testResultContainer);
                continue;
            }
            if (!results.containsKey(browser)) {
                results.put(browser, new HashSet<RectangleSize>());
            }
            Set sizesList = results.get(browser);
            RectangleSize displaySize = env.getDisplaySize();
            if (sizesList.contains(displaySize)) {
                Assert.fail("Browser " + browser + " viewport size " + displaySize + " already exist in results.");
            }
            sizesList.add(displaySize);
        }
        Assert.assertEquals(results.size(), 5);
    }

    @DataProvider
    public static Object[][] pages() {
        return new Object[][]{
                {"https://applitools.github.io/demo/TestPages/DomTest/shadow_dom.html", "Shadow DOM Test"},
                {"https://applitools.github.io/demo/TestPages/VisualGridTestPage/canvastest.html", "Canvas Test"}
        };
    }

    @Test(dataProvider = "pages")
    public void TestSpecialRendering(String url, String testName) {
        VisualGridRunner runner = new VisualGridRunner(30);

        String logsPath = TestUtils.initLogPath();
        LogHandler logHandler = TestUtils.initLogger("TestSpecialRendering", logsPath);
        Eyes eyes = new Eyes(runner);
        eyes.setLogHandler(logHandler);
        runner.setDebugResourceWriter(new FileDebugResourceWriter(eyes.getLogger(), logsPath, null, null));

        Configuration sconf = new Configuration();
        sconf.setTestName(testName);
        sconf.setAppName("Visual Grid Render Test");
        sconf.setBatch(TestDataProvider.batchInfo);

        sconf.addDeviceEmulation(DeviceName.Galaxy_S5);
        sconf.addBrowser(1200, 800, BrowserType.CHROME);
        sconf.addBrowser(1200, 800, BrowserType.FIREFOX);

        // Edge doesn't support Shadow-DOM - returns an empty image.
        //sconf.AddBrowser(1200, 800, BrowserType.EDGE);

        // Internet Explorer doesn't support Shadow-DOM - fails to render and throws an error.
        //sconf.AddBrowser(1200, 800, BrowserType.IE_11);
        //sconf.AddBrowser(1200, 800, BrowserType.IE_10);

        eyes.setConfiguration(sconf);
        ChromeDriver driver = SeleniumUtils.createChromeDriver();
        eyes.open(driver);
        driver.get(url);
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            GeneralUtils.logExceptionStackTrace(eyes.getLogger(), e);
        }
        eyes.check(testName, Target.window().fully());
        driver.quit();
        eyes.close(false);
        TestResultsSummary allResults = runner.getAllTestResults(false);
    }

    @Test
    public void testRenderingIosSimulator() {
        VisualGridRunner runner = new VisualGridRunner(10);
        Eyes eyes = new Eyes(runner);
        Configuration conf = eyes.getConfiguration();
        conf.addBrowser(new IosDeviceInfo(IosDeviceName.iPhone_XR, ScreenOrientation.LANDSCAPE));
        conf.setSaveDiffs(false);
        eyes.setConfiguration(conf);
        eyes.setLogHandler(new StdoutLogHandler());
        ChromeDriver driver = SeleniumUtils.createChromeDriver();
        driver.get("http://applitools.github.io/demo");
        try {
            eyes.open(driver, "Eyes SDK", "UFG Mobile Web Happy Flow", new RectangleSize(800, 600));
            eyes.checkWindow();
            eyes.closeAsync();
        } finally {
            driver.quit();
            eyes.abortAsync();
            runner.getAllTestResults();
        }
    }

    @Test
    public void testRenderingMultipleBrowsers() {
        VisualGridRunner runner = new VisualGridRunner(10);
        Eyes eyes = new Eyes(runner);
        Configuration conf = eyes.getConfiguration();
        conf.addBrowser(new IosDeviceInfo(IosDeviceName.iPhone_7));
        conf.addBrowser(new ChromeEmulationInfo(DeviceName.iPhone_4, ScreenOrientation.LANDSCAPE));
        conf.addBrowser(new DesktopBrowserInfo(new RectangleSize(800, 800), BrowserType.SAFARI));
        eyes.setConfiguration(conf);
        eyes.setLogHandler(new StdoutLogHandler());
        ChromeDriver driver = SeleniumUtils.createChromeDriver();
        driver.get("http://applitools.github.io/demo");
        try {
            eyes.open(driver, "Eyes SDK", "UFG Mobile Web Multiple Browsers", new RectangleSize(800, 800));
            eyes.checkWindow();
            eyes.closeAsync();
        } finally {
            driver.quit();
            eyes.abortAsync();
            runner.getAllTestResults();
        }
    }

    @SuppressWarnings("ThrowFromFinallyBlock")
    @Test
    public void testRenderFail() {
        ServerConnector serverConnector = spy(ServerConnector.class);
        doThrow(new IllegalStateException()).when(serverConnector).render((RenderRequest) any());

        EyesRunner runner = new VisualGridRunner(10);
        Eyes eyes = new Eyes(runner);
        eyes.setLogHandler(new StdoutLogHandler());
        eyes.setServerConnector(serverConnector);
        ChromeDriver driver = SeleniumUtils.createChromeDriver();
        driver.get("http://applitools.github.io/demo");
        try {
            eyes.open(driver, "Applitools Eyes Sdk", "Test Render Fail", new RectangleSize(800, 800));
            eyes.checkWindow();
            eyes.closeAsync();
        } finally {
            driver.quit();
            eyes.abortAsync();
            try {
                runner.getAllTestResults();
                Assert.fail("Expected an exception to be thrown");
            } catch (Throwable t) {
                if (t instanceof AssertionError) {
                    throw  t;
                }
                Assert.assertTrue(t.getCause() instanceof InstantiationError);
            }

        }
    }
}
