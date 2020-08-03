package com.applitools.eyes.images;

import com.applitools.eyes.utils.ReportingTestSuite;
import org.testng.Assert;
import org.testng.annotations.Test;

public class TestConfiguration extends ReportingTestSuite {

    public TestConfiguration() {
        super.setGroupName("images");
    }

    @Test
    public void TestSetEnablePatterns() {
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
        Assert.assertEquals(eyes.getConfiguration().getMatchTimeout(), originalMatchTimeout);
        eyes.getConfigurationInstance().setMatchTimeout(newMatchTimeout);
        Assert.assertEquals(eyes.getConfiguration().getMatchTimeout(), newMatchTimeout);
    }
}
