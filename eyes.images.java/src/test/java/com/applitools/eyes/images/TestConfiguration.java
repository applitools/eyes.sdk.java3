package com.applitools.eyes.images;

import com.applitools.eyes.config.Configuration;
import org.testng.Assert;
import org.testng.annotations.Test;

public class TestConfiguration {

    @Test
    public void testSetEnablePatterns() {
        Eyes e = new Eyes();

        e.setEnablePatterns(true);
        Assert.assertTrue(e.getEnablePatterns());

        e.setEnablePatterns(false);
        Assert.assertFalse(e.getEnablePatterns());
    }

    @Test
    public void testConfigurationEdit() {
        Eyes eyes = new Eyes();
        int originalMatchTimeout = eyes.getConfiguration().getMatchTimeout();
        int newMatchTimeout = originalMatchTimeout + 1000;
        eyes.getConfiguration().setMatchTimeout(newMatchTimeout);
        Assert.assertEquals((int) eyes.getConfiguration().getMatchTimeout(), originalMatchTimeout);
        eyes.getConfigurationInstance().setMatchTimeout(newMatchTimeout);
        Assert.assertEquals((int) eyes.getConfiguration().getMatchTimeout(), newMatchTimeout);
    }

    @Test
    public void testConfiguration() {
        Configuration config = new Configuration();

        Eyes e1 = new Eyes();
        e1.setConfiguration(config.setAppName("app1"));

        Eyes e2 = new Eyes();
        e2.setConfiguration(config.setAppName("app2"));

        Eyes e3 = new Eyes();
        config.setAppName("DefaultAppName");
        e3.setConfiguration(config);

        Assert.assertEquals("app1", e1.getConfiguration().getAppName(), "e1 app name");
        Assert.assertEquals("app2", e2.getConfiguration().getAppName(), "e2 app name");
        Assert.assertEquals("DefaultAppName", e3.getConfiguration().getAppName(), "e3 app name");
    }
}