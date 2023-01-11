package com.applitools.eyes.selenium;

import com.applitools.eyes.EyesRunner;
import com.applitools.eyes.exceptions.StaleElementReferenceException;
import com.applitools.eyes.universal.ManagerType;
import com.applitools.utils.ClassVersionGetter;


/**
 * used to manage multiple Eyes sessions when working without the Ultrafast Grid.
 */
public class ClassicRunner extends EyesRunner {

  /**
   * name of the client sdk
   */
  protected static String BASE_AGENT_ID = "eyes.sdk.java";

  /**
   * version of the client sdk
   */
  protected static String VERSION = ClassVersionGetter.CURRENT_VERSION;


  public ClassicRunner() {
    this(BASE_AGENT_ID, VERSION);
  }

  protected ClassicRunner(String baseAgentId, String version) {
    super(baseAgentId, version);
    //TODO - check if baseAgentId is the correct agentId here
    managerRef = commandExecutor.coreMakeManager(ManagerType.CLASSIC.value, null, null, baseAgentId);
  }

  @Override
  public StaleElementReferenceException getStaleElementException() {
    return new com.applitools.eyes.selenium.exceptions.StaleElementReferenceException();
  }
}
