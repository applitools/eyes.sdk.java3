package com.applitools.eyes.universaltest;

import com.applitools.eyes.BatchInfo;
import com.applitools.eyes.ExactMatchSettings;
import com.applitools.eyes.ImageMatchSettings;
import com.applitools.eyes.RectangleSize;
import com.applitools.eyes.SessionType;
import com.applitools.eyes.config.Configuration;
import com.applitools.eyes.selenium.ClassicRunner;
import com.applitools.eyes.selenium.ClassicRunner0;
import com.applitools.eyes.selenium.Eyes;
import com.applitools.eyes.triggers.VisualGridRunner0;
import com.applitools.eyes.visualgrid.services.VisualGridRunner;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

/**
 * @author Kanan
 */
public class Test {

  @org.junit.Test
  public void testRunners() {
    ClassicRunner classicRunner = new ClassicRunner();
    Eyes eyes = new Eyes(classicRunner);

    VisualGridRunner visualGridRunner = new VisualGridRunner();
    Eyes eyes1 = new Eyes(visualGridRunner);
  }

  @org.junit.Test
  public void testOpenEyes() {
    WebDriver driver = new ChromeDriver();
    Eyes eyes = new Eyes();
    ExactMatchSettings exactMatchSettings = new ExactMatchSettings();
    exactMatchSettings.setMinDiffHeight(20);
    Configuration configuration = new Configuration(RectangleSize.EMPTY);
    configuration.setAppName("app name");
    configuration.setTestName("test name");
    ImageMatchSettings imageMatchSettings = new ImageMatchSettings();
    //imageMatchSettings.setExact(exactMatchSettings);
    configuration.setDefaultMatchSettings(imageMatchSettings);
    eyes.setConfiguration(configuration);
    eyes.open(driver);
  }


  @org.junit.Test
  public void testConfiguration() {
    WebDriver webDriver = new ChromeDriver();
    Configuration configuration = new Configuration();
    configuration.setBranchName("branch name");
    configuration.setParentBranchName("parent branch name");
    //configuration.setBaselineBranchName("Baseline"); // FIXME "message":"Error in request startSession: Request failed with status code 400 (Bad Request)
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
    //configuration.setBatch(batchInfo);
    //configuration.setBaselineEnvName("baseline env name");
    configuration.setAppName("app name");
    configuration.setTestName("test name");
    Eyes eyes = new Eyes();
    eyes.setConfiguration(configuration);
    eyes.open(webDriver);

  }

  @org.junit.Test
  public void testBasicConf() {
    Eyes eyes = new Eyes();
    eyes.open(new ChromeDriver());
  }

}
