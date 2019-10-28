package com.applitools.eyes;

import com.applitools.eyes.config.Configuration;
import com.applitools.eyes.config.IConfigurationGetter;
import com.applitools.eyes.config.IConfigurationSetter;
import com.applitools.eyes.fluent.ICheckSettingsInternal;

public class TestEyes extends EyesBase {
    private Configuration configuration = new Configuration();

    public TestEyes() {
    }

    @Override
    protected String getBaseAgentId() {
        return null;
    }

    @Override
    protected String tryCaptureDom() {
        return null;
    }

    public Configuration getConfigurationGetter() {
        return this.configuration;
    }

    public void setConfiguration(Configuration configuration) {
        this.configuration = configuration;
    }

    @Override
    protected String getInferredEnvironment() {
        return "TestEyes";
    }

    @Override
    protected EyesScreenshot getScreenshot(ICheckSettingsInternal checkSettingsInternal) {
        return new TestEyesScreenshot(this.logger, null);
    }

    @Override
    protected String getTitle() {
        return "TestEyes_Title";
    }

    @Override
    protected String getAUTSessionId() {
        return null;
    }

    @Override
    protected Configuration getConfigGetter() {
        return configuration;
    }

    @Override
    protected Configuration getConfigSetter() {
        return configuration;
    }


    @Override
    protected RectangleSize getViewportSize() {
        return new RectangleSize(100, 100);
    }

    @Override
    protected Configuration setViewportSize(RectangleSize size) {
        return this.configuration;
    }

    @Override
    public void setIsDisabled(Boolean isDisabled) {
        this.setIsDisabled(isDisabled);
    }

    protected Configuration getConfiguration() {
        return configuration;
    }
}


