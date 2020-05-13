package com.applitools.eyes.images;

import com.applitools.eyes.utils.ReportingTestSuite;
import org.testng.Assert;
import org.testng.annotations.Test;

public class TestConfiguration extends ReportingTestSuite {
    @Test
    public void TestSetEnablePatterns() {
        Eyes e = new Eyes();

        e.setEnablePatterns(true);
        Assert.assertTrue(e.getEnablePatterns());

        e.setEnablePatterns(false);
        Assert.assertFalse(e.getEnablePatterns());
    }
}
