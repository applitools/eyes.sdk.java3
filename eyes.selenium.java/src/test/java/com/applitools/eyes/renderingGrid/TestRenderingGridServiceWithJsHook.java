package com.applitools.eyes.renderingGrid;

import com.applitools.eyes.Logger;
import com.applitools.eyes.TestResults;
import com.applitools.eyes.selenium.BrowserType;
import com.applitools.eyes.selenium.Configuration;
import com.applitools.eyes.selenium.Eyes;
import com.applitools.eyes.selenium.TestDataProvider;
import com.applitools.eyes.selenium.fluent.Target;
import com.applitools.eyes.utils.SeleniumUtils;
import com.applitools.eyes.utils.TestUtils;
import com.applitools.eyes.visualgrid.services.VisualGridRunner;
import com.applitools.utils.GeneralUtils;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public final class TestRenderingGridServiceWithJsHook {

    private VisualGridRunner renderingManager;
    private WebDriver webDriver;

    @BeforeMethod
    public void Before(ITestContext testContext) {
        renderingManager = new VisualGridRunner(3);
        renderingManager.setLogHandler(TestUtils.initLogger("TestRenderingGridServiceWithJsHook"));

        webDriver = SeleniumUtils.createChromeDriver();
        webDriver.get("http://applitools-vg-test.surge.sh/test.html");

        System.setProperty("https.protocols", "TLSv1,TLSv1.1,TLSv1.2");
    }

    @Test
    public void test() {

        Eyes eyes = initEyes(webDriver);

        try {
            Configuration configuration = new Configuration();
            configuration.setBatch(TestDataProvider.batchInfo);
            configuration.setTestName("Visual Grid With Web Hook");
            configuration.setAppName("RenderingGridIntegration");
            configuration.addBrowser(400, 300, BrowserType.CHROME);
            //VisualGridEyes.setProxy(new ProxySettings("http://127.0.0.1", 8888, null, null));
            eyes.setServerUrl("https://eyes.applitools.com/");
            eyes.setConfiguration(configuration);
            eyes.open(webDriver);
            //CheckRGSettings setting = new CheckRGSettings(CheckRGSettings.SizeMode.FULL_PAGE, null, null, false);
            String jshook = "document.body.style='background-color: red'";
            eyes.check(Target.window().withName("test").fully().sendDom(false).scriptHook(jshook));
            TestResults close = eyes.close();
            Assert.assertNotNull(close);
        } catch (Exception e) {
            GeneralUtils.logExceptionStackTrace(eyes.getLogger(), e);
        } finally {
            if (webDriver != null) {
                webDriver.quit();
            }
        }
    }

    private Eyes initEyes(WebDriver webDriver) {
        Eyes eyes = new Eyes(renderingManager);

        Logger logger = eyes.getLogger();

        try {
            Configuration configuration = new Configuration();
            configuration.setBatch(TestDataProvider.batchInfo);
            configuration.setTestName("Vans Gallery page");
            configuration.setAppName("RenderingGridIntegration");
            configuration.addBrowser(1200, 800, BrowserType.CHROME, null);
            eyes.setConfiguration(configuration);
            eyes.open(webDriver);
        } catch (Exception e) {
            GeneralUtils.logExceptionStackTrace(logger, e);
        }
        return eyes;
    }

    @AfterMethod
    public void After(ITestContext testContext) {

        renderingManager.getLogger().log(renderingManager.getAllTestResults().toString());
        webDriver.quit();
    }
}