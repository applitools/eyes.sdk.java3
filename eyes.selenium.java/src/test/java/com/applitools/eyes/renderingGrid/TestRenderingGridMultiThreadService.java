package com.applitools.eyes.renderingGrid;

import com.applitools.eyes.BatchInfo;
import com.applitools.eyes.ProxySettings;
import com.applitools.eyes.StdoutLogHandler;
import com.applitools.eyes.TestResults;
import com.applitools.eyes.selenium.BrowserType;
import com.applitools.eyes.selenium.Configuration;
import com.applitools.eyes.selenium.Eyes;
import com.applitools.eyes.selenium.fluent.Target;
import com.applitools.eyes.utils.SeleniumUtils;
import com.applitools.eyes.utils.TestUtils;
import com.applitools.eyes.visualgrid.model.RenderBrowserInfo;
import com.applitools.eyes.visualgrid.services.VisualGridRunner;
import com.applitools.utils.GeneralUtils;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public final class TestRenderingGridMultiThreadService {

    private VisualGridRunner renderingManager;
    private WebDriver webDriver;

    @BeforeMethod
    public void Before(ITestContext testContext) {
        renderingManager = new VisualGridRunner(1);
        renderingManager.setLogHandler(new StdoutLogHandler(TestUtils.verboseLogs));

        webDriver = SeleniumUtils.createChromeDriver();
        webDriver.get("https://applitools.github.io/demo/TestPages/VisualGridTestPage");
        //webDriver.get("http://applitools-vg-test.surge.sh/test.html");

        System.setProperty("https.protocols", "TLSv1,TLSv1.1,TLSv1.2");
    }

    @Test
    public void test() {

        final Eyes eyes = new Eyes(renderingManager);
        eyes.setBatch(new BatchInfo("VG-2ThreadBatchThreads"));

        final String baselineEnvName = "";
        Thread threadA = new Thread(new Runnable() {
            @Override
            public void run() {

                TestThreadMethod("VG-2ThreadBatchC11",
                        new RenderBrowserInfo(800, 600, BrowserType.CHROME, baselineEnvName),
                        new RenderBrowserInfo(700, 500, BrowserType.CHROME, baselineEnvName),
                        new RenderBrowserInfo(400, 300, BrowserType.CHROME, baselineEnvName));
            }
        });

        Thread threadB = new Thread(new Runnable() {
            @Override
            public void run() {
                TestThreadMethod("VG-2ThreadBatchC22",
                        new RenderBrowserInfo(840, 680, BrowserType.CHROME, baselineEnvName),
                        new RenderBrowserInfo(750, 530, BrowserType.CHROME, baselineEnvName));
            }
        });

        threadA.start();
        threadB.start();

        try {
            threadA.join();
            threadB.join();
        } catch (InterruptedException e) {
            GeneralUtils.logExceptionStackTrace(renderingManager.getLogger(), e);
        }
    }

    private void TestThreadMethod(String batchName, RenderBrowserInfo... browsersInfo) {
        Eyes eyes = new Eyes(renderingManager);
        eyes.setBatch(new BatchInfo(batchName));
        Configuration configuration = new Configuration();
        configuration.setTestName("Open Concurrency with Batch 3");
        configuration.setAppName("RenderingGridIntegration");
        configuration.addBrowsers(browsersInfo);
        eyes.setProxy(new ProxySettings("http://127.0.0.1", 8888, null, null));
        eyes.open(webDriver);
        eyes.check(Target.window().withName("test").sendDom(false));
        TestResults close = eyes.close();
        Assert.assertNotNull(close);

    }

    @AfterMethod
    public void After(ITestContext testContext) {
        renderingManager.getLogger().log(renderingManager.getAllTestResults().toString());
        webDriver.quit();
    }
}