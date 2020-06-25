package com.applitools.eyes.selenium;

import com.applitools.eyes.RectangleSize;
import com.applitools.eyes.utils.ReportingTestSuite;
import org.testng.annotations.Test;

public class TestConfiguration extends ReportingTestSuite {

    public TestConfiguration() {
        super.setGroupName("selenium");
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

    }
}
