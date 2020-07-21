package com.applitools.eyes.renderingGrid;

import com.applitools.connectivity.ServerConnector;
import com.applitools.eyes.*;
import com.applitools.eyes.config.Configuration;
import com.applitools.eyes.selenium.BrowserType;
import com.applitools.eyes.selenium.Eyes;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.spy;

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
        runner.getAllTestResults();
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
        runner.getAllTestResults(false);
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
        doThrow(new IllegalStateException()).when(serverConnector).render(any(RenderRequest.class));

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
                    throw t;
                }
                Assert.assertTrue(t.getCause() instanceof InstantiationError);
            }
        }
    }
}
