package com.applitools.eyes.renderingGrid;

import com.applitools.eyes.*;
import com.applitools.eyes.metadata.ImageMatchSettings;
import com.applitools.eyes.metadata.SessionResults;
import com.applitools.eyes.selenium.BrowserType;
import com.applitools.eyes.selenium.Configuration;
import com.applitools.eyes.selenium.Eyes;
import com.applitools.eyes.selenium.TestDataProvider;
import com.applitools.eyes.utils.SeleniumUtils;
import com.applitools.eyes.utils.TestUtils;
import com.applitools.eyes.visualgrid.model.DesktopBrowserInfo;
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
        final VisualGridRunner runner = new VisualGridRunner(10,"TestVGDoubleCloseNoCheck");
        try {
            final Eyes eyes = new Eyes(runner);
            Configuration conf = new Configuration();
            conf.setAppName("app").setTestName("test");
            conf.setBatch(TestDataProvider.batchInfo);
            eyes.setConfiguration(conf);

            eyes.open(driver);
            Error ex = Assert.expectThrows(Error.class, new Assert.ThrowingRunnable() {
                @Override
                public void run() {
                    eyes.close();
                }
            });
            Assert.assertEquals(ex.getMessage(), "java.lang.IllegalStateException: Eyes not open");
        } finally {
            driver.quit();
            Assert.expectThrows(Error.class, new Assert.ThrowingRunnable() {
                @Override
                public void run() {
                    runner.getAllTestResults();
                }
            });
        }
    }

    @Test
    public void TestVGChangeConfigAfterOpen() {
        WebDriver driver = new ChromeDriver();
        driver.get("https://applitools.com/helloworld");
        VisualGridRunner runner = new VisualGridRunner(10,"TestVGChangeConfigAfterOpen");
        try {
            Eyes eyes = new Eyes(runner);
            eyes.setLogHandler(new FileLogger("fabric.log", true, true));
            Configuration conf = new Configuration();
            conf.addBrowser(new DesktopBrowserInfo(800, 600, BrowserType.CHROME));
            conf.setServerUrl("https://eyesfabric4eyes.applitools.com");
            conf.setApiKey("CAE7aS103TDz7XyegELya3tHpEIXTFi0gBBwvgq104PSHIU110");
            conf.setAppName("app").setTestName("test");
            conf.setBatch(TestDataProvider.batchInfo);
            conf.setAccessibilityValidation(null).setIgnoreDisplacements(false);
//            conf.setProxy(new ProxySettings("http://127.0.0.1", 8888, null, null));
            eyes.setConfiguration(conf);

            eyes.open(driver);

            AccessibilitySettings accessibilitySettings = new AccessibilitySettings(AccessibilityLevel.AAA, AccessibilityGuidelinesVersion.WCAG_2_0);
            conf.setAccessibilityValidation(accessibilitySettings).setIgnoreDisplacements(true);
            eyes.setConfiguration(conf);

            eyes.checkWindow();

            accessibilitySettings = new AccessibilitySettings(AccessibilityLevel.AA, AccessibilityGuidelinesVersion.WCAG_2_1);
            conf.setAccessibilityValidation(accessibilitySettings).setMatchLevel(MatchLevel.LAYOUT);
            eyes.setConfiguration(conf);

            eyes.checkWindow();

            TestResults results = eyes.close(false);

            SessionResults sessionResults = null;
            try {
                sessionResults = TestUtils.getSessionResults("CAE7aS103TDz7XyegELya3tHpEIXTFi0gBBwvgq104PSHIU110", results);
            } catch (IOException e) {
                e.printStackTrace();
            }

            Assert.assertNull(sessionResults.getStartInfo().getDefaultMatchSettings().getAccessibilitySettings());
            final ImageMatchSettings defaultMatchSettings = sessionResults.getStartInfo().getDefaultMatchSettings();
            Assert.assertFalse(defaultMatchSettings.getIgnoreDisplacements());
            Assert.assertEquals(MatchLevel.STRICT, sessionResults.getStartInfo().getDefaultMatchSettings().getMatchLevel());

            Assert.assertEquals(2, sessionResults.getActualAppOutput().length);

            accessibilitySettings = sessionResults.getActualAppOutput()[0].getImageMatchSettings().getAccessibilitySettings();
            Assert.assertEquals(AccessibilityLevel.AAA, accessibilitySettings.getLevel());
            Assert.assertEquals(AccessibilityGuidelinesVersion.WCAG_2_0, accessibilitySettings.getGuidelinesVersion());
            Assert.assertTrue(sessionResults.getActualAppOutput()[0].getImageMatchSettings().getIgnoreDisplacements());
            Assert.assertEquals(MatchLevel.STRICT, sessionResults.getActualAppOutput()[0].getImageMatchSettings().getMatchLevel());

            accessibilitySettings =sessionResults.getActualAppOutput()[1].getImageMatchSettings().getAccessibilitySettings();
            Assert.assertEquals(AccessibilityGuidelinesVersion.WCAG_2_1, accessibilitySettings.getGuidelinesVersion());
            Assert.assertTrue(sessionResults.getActualAppOutput()[1].getImageMatchSettings().getIgnoreDisplacements());
            Assert.assertEquals(MatchLevel.LAYOUT2, sessionResults.getActualAppOutput()[1].getImageMatchSettings().getMatchLevel());
        } finally {
            driver.quit();
            runner.getAllTestResults();
        }
    }
}
