package com.applitools.eyes.selenium;

import com.applitools.eyes.EyesBase;
import com.applitools.eyes.EyesScreenshot;
import com.applitools.eyes.ImageMatchSettings;
import com.applitools.eyes.RectangleSize;
import com.applitools.eyes.fluent.ICheckSettingsInternal;

public class TestEyes extends EyesBase {
    private Configuration configuration = new Configuration();

    public TestEyes()
    {
    }

    @Override
    protected String getBaseAgentId() {
        return null;
    }

    @Override
    protected String tryCaptureDom() {
        return null;
    }

    public void setConfiguration(Configuration configuration) {
        this.configuration = new Configuration(configuration);
    }

    public void setDefaultMatchSettings(ImageMatchSettings defaultMatchSettings) {
        configuration.setDefaultMatchSettings(defaultMatchSettings);
    }

    @Override
    public Configuration getConfiguration() {
        return configuration;
    }

    @Override
    protected String getInferredEnvironment()
    {
        return "TestEyes";
    }

    @Override
    protected EyesScreenshot getScreenshot(ICheckSettingsInternal checkSettingsInternal)
    {
        return new TestEyesScreenshot();
    }

    @Override
    protected String getTitle()
    {
        return "TestEyes_Title";
    }

    @Override
    protected String getAUTSessionId() {
        return null;
    }

    @Override
    protected RectangleSize getViewportSize()
    {
        return new RectangleSize(100, 100);
    }

    @Override
    protected Configuration setViewportSize(RectangleSize size)
    {
        return configuration;
    }
}
