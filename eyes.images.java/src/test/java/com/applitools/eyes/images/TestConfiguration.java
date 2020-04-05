package com.applitools.eyes.images;

import com.applitools.eyes.utils.ReportingTestSuite;
import org.testng.Assert;
import org.testng.annotations.Test;

public class TestConfiguration extends ReportingTestSuite {
    @Test
    public void TestSetEnablePatterns() {
        Eyes e1 = new Eyes();
        e1.setEnablePatterns(true);

        Eyes e2 = new Eyes();
        e2.setEnablePatterns(false);

        Assert.assertTrue(e1.getEnablePatterns());
        Assert.assertFalse(e2.getEnablePatterns());
    }
}
