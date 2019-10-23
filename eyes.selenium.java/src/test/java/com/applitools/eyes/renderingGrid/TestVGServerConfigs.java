package com.applitools.eyes.renderingGrid;

import com.applitools.eyes.AccessibilityLevel;
import com.applitools.eyes.FileLogger;
import com.applitools.eyes.MatchLevel;
import com.applitools.eyes.TestResults;
import com.applitools.eyes.metadata.SessionResults;
import com.applitools.eyes.selenium.BrowserType;
import com.applitools.eyes.selenium.Configuration;
import com.applitools.eyes.selenium.Eyes;
import com.applitools.eyes.selenium.TestDataProvider;
import com.applitools.eyes.utils.SeleniumUtils;
import com.applitools.eyes.utils.TestUtils;
import com.applitools.eyes.visualgrid.model.RenderBrowserInfo;
import com.applitools.eyes.visualgrid.services.VisualGridRunner;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;

public class TestVGServerConfigs {
    @Test
    public void TestVGDoubleCloseNoCheck() {
        WebDriver driver = SeleniumUtils.createChromeDriver();
        try {
            VisualGridRunner runner = new VisualGridRunner(10);
            final Eyes eyes = new Eyes(runner);
            Configuration conf = new Configuration();
            conf.setAppName("app").setTestName("test");
            conf.setBatch(TestDataProvider.batchInfo);
            eyes.setConfiguration(conf);

            eyes.open(driver);
            Error ex = Assert.expectThrows(Error.class, new Assert.ThrowingRunnable() {
                @Override
                public void run() throws Throwable {
                    eyes.close();
                }
            });
            Assert.assertEquals(ex.getMessage(), "java.lang.IllegalStateException: Eyes not open");
        } finally {
            driver.quit();
        }
    }
    @Test
    public void TestVGChangeConfigAfterOpen()
    {
        WebDriver driver = new ChromeDriver();
        driver.get("https://applitools.com/helloworld");
        try
        {
            VisualGridRunner runner = new VisualGridRunner(10);
            Eyes eyes = new Eyes(runner);
            eyes.setLogHandler(new FileLogger("fabric.log", true ,true));
            Configuration conf = new Configuration();
            conf.addBrowser(new RenderBrowserInfo(800,600, BrowserType.CHROME));
            conf.setServerUrl("https://eyesfabric4eyes.applitools.com");
            conf.setApiKey("CAE7aS103TDz7XyegELya3tHpEIXTFi0gBBwvgq104PSHIU110");
            conf.setAppName("app").setTestName("test");
            conf.setBatch(TestDataProvider.batchInfo);
            conf.setAccessibilityValidation(AccessibilityLevel.None).setIgnoreDisplacements(false);
//            conf.setProxy(new ProxySettings("http://127.0.0.1", 8888, null, null));
            eyes.setConfiguration(conf);

            eyes.open(driver);

            conf.setAccessibilityValidation(AccessibilityLevel.AAA).setIgnoreDisplacements(true);
            eyes.setConfiguration(conf);

            eyes.checkWindow();

            conf.setAccessibilityValidation(AccessibilityLevel.AA).setMatchLevel(MatchLevel.LAYOUT);
            eyes.setConfiguration(conf);

            eyes.checkWindow();

            TestResults results = eyes.close(false);

            SessionResults sessionResults = null;
            try {
                sessionResults = TestUtils.getSessionResults("CAE7aS103TDz7XyegELya3tHpEIXTFi0gBBwvgq104PSHIU110", results);
            } catch (IOException e) {
                e.printStackTrace();
            }

            Assert.assertEquals(AccessibilityLevel.None, sessionResults.getStartInfo().getDefaultMatchSettings().getAccessibilityLevel());
            Assert.assertFalse(sessionResults.getStartInfo().getDefaultMatchSettings().getIgnoreDisplacements());
            Assert.assertEquals(MatchLevel.STRICT, sessionResults.getStartInfo().getDefaultMatchSettings().getMatchLevel());

            Assert.assertEquals(2, sessionResults.getActualAppOutput().length);

            Assert.assertEquals(AccessibilityLevel.AAA, sessionResults.getActualAppOutput()[0].getImageMatchSettings().getAccessibilityLevel());
            Assert.assertTrue(sessionResults.getActualAppOutput()[0].getImageMatchSettings().getIgnoreDisplacements());
            Assert.assertEquals(MatchLevel.STRICT, sessionResults.getActualAppOutput()[0].getImageMatchSettings().getMatchLevel());

            Assert.assertEquals(AccessibilityLevel.AA, sessionResults.getActualAppOutput()[1].getImageMatchSettings().getAccessibilityLevel());
            Assert.assertTrue(sessionResults.getActualAppOutput()[1].getImageMatchSettings().getIgnoreDisplacements());
            Assert.assertEquals(MatchLevel.LAYOUT2, sessionResults.getActualAppOutput()[1].getImageMatchSettings().getMatchLevel());
        }
        finally
        {
            driver.quit();
        }
    }
}
