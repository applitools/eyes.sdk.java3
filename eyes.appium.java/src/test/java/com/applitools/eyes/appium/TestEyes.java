package com.applitools.eyes.appium;

import com.applitools.eyes.*;
import com.applitools.eyes.config.Configuration;
import com.applitools.eyes.fluent.ICheckSettingsInternal;
import org.testng.Assert;
import org.testng.annotations.Test;

public class TestEyes extends EyesBase {

    private Configuration configuration = new Configuration();

    public TestEyes() {
    }

    @Override
    protected String getBaseAgentId() {
        return null;
    }

    @Override
    public String tryCaptureDom() {
        return null;
    }

    @Override
    protected RectangleSize getViewportSize() {
        return new RectangleSize(100, 100);
    }

    @Override
    protected Configuration setViewportSize(RectangleSize size) {
        return configuration;
    }

    @Override
    protected String getInferredEnvironment() {
        return "TestEyes";
    }

    @Override
    protected EyesScreenshot getScreenshot(Region targetRegion, ICheckSettingsInternal checkSettingsInternal) {
        return new TestEyesScreenshot(this.logger, null);
    }

    @Override
    protected String getTitle() {
        return "TestEyes_Title";
    }

    public void setConfiguration(Configuration configuration) {
        this.configuration = configuration;
    }

    protected Configuration getConfigurationInstance() {
        return configuration;
    }

    @Test
    public void testEyesApi() {
        Eyes eyes = new Eyes();
        eyes.getParentBranchName();
        eyes.setParentBranchName("");
        eyes.getBranchName();
        eyes.setBranchName("");
        eyes.getSaveNewTests();
        eyes.setSaveNewTests(true);
        eyes.getSaveDiffs();
        eyes.setSaveDiffs(true);
        eyes.getDefaultMatchSettings();
        eyes.setDefaultMatchSettings(new ImageMatchSettings());
        eyes.getMatchTimeout();
        eyes.setMatchTimeout(0);
        eyes.getBaselineBranchName();
        eyes.setBaselineBranchName("");;
        eyes.getBaselineEnvName();
        eyes.setBaselineEnvName("");
        eyes.getHostApp();
        eyes.setHostApp("");
        eyes.getHostOS();
        eyes.setHostOS("");
        eyes.getStitchOverlap();
        eyes.setStitchOverlap(0);
        eyes.getBatch();
        eyes.setBatch(new BatchInfo());
        eyes.getAgentId();
        eyes.setAgentId("");
        eyes.getEnvName();
        eyes.setEnvName("");
        eyes.getApiKey();
        eyes.setApiKey("");
        eyes.getIgnoreCaret();
        eyes.setIgnoreCaret(true);
        eyes.getServerUrl();
        eyes.setServerUrl("");
        eyes.getMatchLevel();
        eyes.setMatchLevel(MatchLevel.CONTENT);
        eyes.getConfiguration();
        eyes.setConfiguration(new Configuration());
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
