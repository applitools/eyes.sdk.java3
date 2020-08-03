package com.applitools.eyes.selenium;

import com.applitools.eyes.RectangleSize;
import com.applitools.eyes.config.ConfigurationProvider;
import com.applitools.eyes.utils.ReportingTestSuite;
import org.testng.Assert;
import org.testng.annotations.Test;

public class TestConfiguration extends ReportingTestSuite {

    public TestConfiguration() {
        super.setGroupName("core");
    }

    @Test
    public void testConfigurationConstructors() {
        Configuration configuration = new Configuration();
        Configuration clone = new Configuration(configuration);
        clone = new Configuration("");
        clone = new Configuration(new RectangleSize(800, 800));
        clone = new Configuration("", "", new RectangleSize(800, 800));

        com.applitools.eyes.config.Configuration base = new com.applitools.eyes.config.Configuration();
        com.applitools.eyes.config.Configuration cloneBase = new com.applitools.eyes.config.Configuration(base);
        cloneBase = new com.applitools.eyes.config.Configuration(clone);
        cloneBase = new com.applitools.eyes.config.Configuration("");
        cloneBase = new com.applitools.eyes.config.Configuration(new RectangleSize(800, 800));
        cloneBase = new com.applitools.eyes.config.Configuration("", "", new RectangleSize(800, 800));

        Eyes eyes = new Eyes();
        clone = eyes.getConfiguration();
        cloneBase = eyes.getConfiguration();
    }

    @Test
    public void testConfigurationEdit() {
        Eyes eyes = new Eyes();
        int originalMatchTimeout = eyes.getConfiguration().getMatchTimeout();
        int newMatchTimeout = originalMatchTimeout + 1000;
        eyes.getConfiguration().setMatchTimeout(newMatchTimeout);
        Assert.assertEquals(eyes.getConfiguration().getMatchTimeout(), originalMatchTimeout);

        final Configuration configuration = new Configuration();
        SeleniumEyes seleniumEyes = new SeleniumEyes(new ConfigurationProvider() {
            @Override
            public com.applitools.eyes.config.Configuration get() {
                return configuration;
            }
        }, new ClassicRunner());
        originalMatchTimeout = seleniumEyes.getConfiguration().getMatchTimeout();
        newMatchTimeout = originalMatchTimeout + 1000;
        seleniumEyes.getConfiguration().setMatchTimeout(newMatchTimeout);
        Assert.assertEquals(seleniumEyes.getConfiguration().getMatchTimeout(), originalMatchTimeout);
        seleniumEyes.getConfigurationInstance().setMatchTimeout(newMatchTimeout);
        Assert.assertEquals(seleniumEyes.getConfiguration().getMatchTimeout(), newMatchTimeout);
    }
}
