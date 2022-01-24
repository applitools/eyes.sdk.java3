package com.applitools.eyes;

import com.applitools.eyes.selenium.Configuration;
import com.applitools.eyes.selenium.Eyes;
import com.applitools.universal.Reference;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * eyes tests
 */
public class EyesTests {

  @Test
  public void should_OpenEyes_When_ChromeDriver() {
    Configuration configuration = new Configuration();
    configuration.setAppName("app name");
    configuration.setTestName("test name");
    configuration.setBranchName("branch name");
    configuration.setParentBranchName("parent branch name");
    configuration.setAgentId("agentId");
    configuration.setEnvironmentName("environment name");
    configuration.setSaveDiffs(false);
    configuration.setSessionType(SessionType.SEQUENTIAL);
    BatchInfo batchInfo = new BatchInfo();
    batchInfo.setId("batchId");
    batchInfo.setCompleted(false);
    batchInfo.setNotifyOnCompletion(true);
    batchInfo.setSequenceName("sequence name");
    batchInfo.setStartedAt(null);
    configuration.setBatch(batchInfo);
    configuration.setBaselineEnvName("baseline env name");
    RectangleSize rectangleSize = new RectangleSize(500, 500);
    configuration.setViewportSize(rectangleSize);
    //configuration.setIgnoreDisplacements(false);
    ImageMatchSettings imageMatchSettings = new ImageMatchSettings();
    imageMatchSettings.setEnablePatterns(false);
    imageMatchSettings.setIgnoreCaret(false);
    imageMatchSettings.setMatchLevel(MatchLevel.EXACT);
    configuration.setDefaultMatchSettings(imageMatchSettings);
    configuration.setBrowsersInfo(null);
    Eyes eyes = new Eyes();
    eyes.setConfiguration(configuration);
    eyes.open(new ChromeDriver());
    Reference eyesRef = eyes.getEyesRef();
    Assert.assertNotNull(eyesRef);
  }

  @Test
  public void should_OpenEyes_When_Configured() {
    Eyes eyes = new Eyes();
    eyes.open(new ChromeDriver(), "app name", "test name");
    Reference eyesRef = eyes.getEyesRef();
    Assert.assertNotNull(eyesRef);
    Assert.assertEquals("app name", eyes.getConfiguration().getAppName());
    Assert.assertEquals("test name", eyes.getConfiguration().getTestName());
  }
}
