package com.applitools.eyes.appium;

import com.applitools.eyes.visualgrid.services.RunnerOptions;
import com.applitools.eyes.visualgrid.services.VisualGridRunner;
import com.applitools.utils.ClassVersionGetter;

public class AppiumVisualGridRunner extends VisualGridRunner {

  /**
   * name of the client sdk
   */
  protected static String BASE_AGENT_ID = "eyes.sdk.appium";

  /**
   * version of the client sdk
   */
  protected static String VERSION = ClassVersionGetter.CURRENT_VERSION;

  public AppiumVisualGridRunner() {
    super(BASE_AGENT_ID, VERSION);
  }

  public AppiumVisualGridRunner(String suiteName) {
    super(BASE_AGENT_ID, VERSION);
  }

  public AppiumVisualGridRunner(int testConcurrency) {
    this(testConcurrency, Thread.currentThread().getStackTrace()[2].getClassName());
  }

  public AppiumVisualGridRunner(int testConcurrency0, String suiteName) {
    super(testConcurrency0, BASE_AGENT_ID, VERSION);
  }

  public AppiumVisualGridRunner(RunnerOptions runnerOptions) {
    this(runnerOptions, Thread.currentThread().getStackTrace()[2].getClassName());
  }

  public AppiumVisualGridRunner(RunnerOptions runnerOptions, String suiteName) {
    super(runnerOptions, BASE_AGENT_ID, VERSION);
  }

}
