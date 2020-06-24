package com.applitools.eyes.selenium;

import com.applitools.eyes.config.Configuration;
import com.applitools.eyes.config.ConfigurationProvider;
import com.applitools.eyes.selenium.fluent.Target;
import com.applitools.eyes.utils.ReportingTestSuite;
import org.testng.Assert;
import org.testng.annotations.Test;

public class TestSeleniumEyes extends ReportingTestSuite {

    public TestSeleniumEyes() {
        super.setGroupName("selenium");
    }

    Configuration configuration = new Configuration();
    ConfigurationProvider configurationProvider = new ConfigurationProvider() {
        @Override
        public Configuration get() {
            return configuration;
        }
    };

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
}
