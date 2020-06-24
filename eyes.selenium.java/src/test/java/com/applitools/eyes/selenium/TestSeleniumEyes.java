package com.applitools.eyes.selenium;

import com.applitools.eyes.RectangleSize;
import com.applitools.eyes.config.Configuration;
import com.applitools.eyes.config.ConfigurationProvider;
import com.applitools.eyes.debug.DebugScreenshotsProvider;
import com.applitools.eyes.selenium.fluent.Target;
import com.applitools.eyes.utils.ReportingTestSuite;
import com.applitools.eyes.utils.SeleniumUtils;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.awt.image.BufferedImage;
import java.util.concurrent.atomic.AtomicBoolean;

public class TestSeleniumEyes extends ReportingTestSuite {

    private static final String TESTED_PAGE_URL = "https://applitools.github.io/demo/TestPages/FramesTestPage/";

    Configuration configuration = new Configuration();
    ConfigurationProvider configurationProvider = new ConfigurationProvider() {
        @Override
        public Configuration get() {
            return configuration;
        }
    };

    public TestSeleniumEyes() {
        super.setGroupName("selenium");
    }

    @BeforeMethod
    public void beforeEach() {
        configuration = new Configuration();
    }

    @Test
    public void testShouldTakeFullPageScreenshot() {
        SeleniumEyes eyes = new SeleniumEyes(configurationProvider, new ClassicRunner());

        Assert.assertFalse(eyes.shouldTakeFullPageScreenshot(Target.window()));
        Assert.assertTrue(eyes.shouldTakeFullPageScreenshot(Target.window().fully()));
        Assert.assertTrue(eyes.shouldTakeFullPageScreenshot(Target.window().fully(true)));
        Assert.assertFalse(eyes.shouldTakeFullPageScreenshot(Target.window().fully(false)));

        configuration.setForceFullPageScreenshot(true);
        Assert.assertTrue(eyes.shouldTakeFullPageScreenshot(Target.window()));
        Assert.assertTrue(eyes.shouldTakeFullPageScreenshot(Target.window().fully()));
        Assert.assertTrue(eyes.shouldTakeFullPageScreenshot(Target.window().fully(true)));
        Assert.assertFalse(eyes.shouldTakeFullPageScreenshot(Target.window().fully(false)));

        configuration.setForceFullPageScreenshot(false);
        Assert.assertFalse(eyes.shouldTakeFullPageScreenshot(Target.window()));
        Assert.assertTrue(eyes.shouldTakeFullPageScreenshot(Target.window().fully()));
        Assert.assertTrue(eyes.shouldTakeFullPageScreenshot(Target.window().fully(true)));
        Assert.assertFalse(eyes.shouldTakeFullPageScreenshot(Target.window().fully(false)));
    }

    @Test
    public void testDebugScreenshot() {
        SeleniumEyes eyes = new SeleniumEyes(configurationProvider, new ClassicRunner());

        final AtomicBoolean wasSavedDebugScreenshot = new AtomicBoolean();
        wasSavedDebugScreenshot.set(false);
        eyes.setDebugScreenshotProvider(new DebugScreenshotsProvider() {
            @Override
            public void save(BufferedImage image, String suffix) {
                wasSavedDebugScreenshot.set(true);
            }
        });

        WebDriver driver = SeleniumUtils.createChromeDriver();
        driver.get(TESTED_PAGE_URL);
        try {
            eyes.open(driver, "Applitools Eyes SDK", "Test Debug Screenshot", new RectangleSize(800, 800));
            eyes.checkWindow();
            Assert.assertTrue(wasSavedDebugScreenshot.get());
            wasSavedDebugScreenshot.set(false);
            eyes.check(Target.window().fully());
            Assert.assertTrue(wasSavedDebugScreenshot.get());
            eyes.close(true);
        } finally {
            driver.quit();
            eyes.abortIfNotClosed();
        }
    }
}
