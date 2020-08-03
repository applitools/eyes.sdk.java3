package com.applitools.eyes;

import com.applitools.eyes.RectangleSize;
import com.applitools.eyes.TestEyes;
import com.applitools.eyes.selenium.Configuration;
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

        TestEyes eyes = new TestEyes();
        clone = eyes.getConfiguration();
        cloneBase = eyes.getConfiguration();
    }

    @Test
    public void testConfigurationEdit() {
        TestEyes eyes = new TestEyes();
        int originalMatchTimeout = eyes.getConfiguration().getMatchTimeout();
        int newMatchTimeout = originalMatchTimeout + 1000;
        eyes.getConfiguration().setMatchTimeout(newMatchTimeout);
        Assert.assertEquals(eyes.getConfiguration().getMatchTimeout(), originalMatchTimeout);
        eyes.getConfigurationInstance().setMatchTimeout(newMatchTimeout);
        Assert.assertEquals(eyes.getConfiguration().getMatchTimeout(), newMatchTimeout);
    }
}
