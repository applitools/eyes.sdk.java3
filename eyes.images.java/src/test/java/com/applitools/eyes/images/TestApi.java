package com.applitools.eyes.images;

import com.applitools.eyes.config.Configuration;
import org.testng.Assert;
import org.testng.annotations.Test;

public class TestApi {
    @Test
    public void EnsureApiExists()
    {
        Eyes eyes = new Eyes();
        Configuration config = eyes.getConfiguration();
        Assert.assertNotNull(config);
        config.setAppName("Test API").setTestName("Ensure API Exists");
        eyes.setConfiguration(config);
        Assert.assertEquals(eyes.getAppName(), "Test API");
        Assert.assertEquals(eyes.getTestName(), "Ensure API Exists");
    }
}
