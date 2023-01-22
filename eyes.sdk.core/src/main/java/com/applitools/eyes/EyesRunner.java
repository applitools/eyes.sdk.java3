package com.applitools.eyes;

import com.applitools.eyes.exceptions.DiffsFoundException;
import com.applitools.eyes.exceptions.NewTestException;
import com.applitools.eyes.exceptions.StaleElementReferenceException;
import com.applitools.eyes.exceptions.TestFailedException;
import com.applitools.eyes.logging.Stage;
import com.applitools.eyes.logging.Type;
import com.applitools.eyes.universal.*;
import com.applitools.eyes.universal.dto.TestResultsSummaryDto;
import com.applitools.eyes.universal.mapper.TestResultsSummaryMapper;
import com.applitools.eyes.universal.server.UniversalSdkNativeLoader;
import com.applitools.eyes.visualgrid.services.RunnerOptions;
import com.applitools.utils.ArgumentGuard;
import com.applitools.utils.ClassVersionGetter;
import org.apache.commons.lang3.tuple.Pair;

public abstract class EyesRunner {
  /**
   * command executor
   */
  protected CommandExecutor commandExecutor;

  /**
   * this reference has to be used in order to perform manager related actions (EyesManager.openEyes, EyesManager.closeAllEyes)
   */
  protected Reference managerRef;

  /**
   * name of the client sdk
   */
  protected static String BASE_AGENT_ID = "eyes.sdk.java";

  /**
   * version of the client sdk
   */
  protected static String VERSION = ClassVersionGetter.CURRENT_VERSION;

  /**
   * the protocol to be used
   */
  protected static String PROTOCOL = "webdriver";

  /**
   * list of commands sent to the server.
   */
  protected static String[] COMMANDS = null;

  /**
   * the universal server listener.
   */
  protected static USDKListener listener;

  private Boolean dontCloseBatches;

  protected Logger logger = new Logger();

  /**
   * used for instantiating Classic Runner
   */
  public EyesRunner() {
    setLogHandler(new NullLogHandler());
    runServer(BASE_AGENT_ID, VERSION);
  }

  /**
   * used for instantiating Classic Runner
   */
  public EyesRunner(String baseAgentId, String version) {
    setLogHandler(new NullLogHandler());
    runServer(baseAgentId, version);
  }

  public EyesRunner(String baseAgentId, String version, AbstractSDKListener listener) {
    runServer(baseAgentId, version, PROTOCOL, COMMANDS, listener);
  }

  /**
   * used for instantiating VisualGrid Runner
   */
  public EyesRunner(String baseAgentId, String version, RunnerOptions runnerOptions) {
    ArgumentGuard.notNull(runnerOptions, "runnerOptions");
    setLogHandler(runnerOptions.getLogHandler());
    runServer(baseAgentId, version);
  }

  /**
   * used for instantiating VisualGrid Runner
   */
  public EyesRunner(String baseAgentId, String version, RunnerOptions runnerOptions, AbstractSDKListener listener) {
    ArgumentGuard.notNull(runnerOptions, "runnerOptions");
    setLogHandler(new NullLogHandler());
    runServer(baseAgentId, version, PROTOCOL, COMMANDS, listener);
  }

  /**
   * used for instantiating Classic Runner
   */
  public EyesRunner(String baseAgentId, String version, String protocol, String[] commands, AbstractSDKListener listener) {
    setLogHandler(new NullLogHandler());
    runServer(baseAgentId, version, protocol, commands, listener);
  }

  /**
   * used for instantiating VisualGrid Runner
   */
  public EyesRunner(String baseAgentId, String version, String protocol, String[] commands, AbstractSDKListener listener, RunnerOptions runnerOptions) {
    ArgumentGuard.notNull(runnerOptions, "runnerOptions");
    setLogHandler(runnerOptions.getLogHandler());
    runServer(baseAgentId, version, protocol, commands, listener);
  }

  protected void runServer(String baseAgentId, String version) {
    runServer(baseAgentId, version, PROTOCOL, null, listener);
  }

  protected void runServer(String baseAgentId, String version, String protocol, String[] commands, AbstractSDKListener listener){
    UniversalSdkNativeLoader.setLogger(getLogger());
    UniversalSdkNativeLoader.start();
    commandExecutor = CommandExecutor.getInstance(baseAgentId, version, protocol, commands, listener, getStaleElementException());
  }

  public TestResultsSummary getAllTestResults(boolean shouldThrowException) {
    TestResultsSummaryDto dto = commandExecutor.closeManager(managerRef, shouldThrowException);
    return TestResultsSummaryMapper.fromDto(dto, shouldThrowException);
  }

  public void setLogHandler(LogHandler logHandler) {
    logger.setLogHandler(logHandler);
    if (!logHandler.isOpen()) {
      logHandler.open();
    }
  }

  public void logSessionResultsAndThrowException(boolean throwEx, TestResults results) {
    if (results == null) {
      return;
    }
    TestResultsStatus status = results.getStatus();
    String sessionResultsUrl = results.getUrl();
    String scenarioIdOrName = results.getName();
    String appIdOrName = results.getAppName();
    if (status == null) {
      throw new EyesException("Status is null in the test results");
    }

    logger.log(results.getId(), Stage.CLOSE, Type.TEST_RESULTS, Pair.of("status", status), Pair.of("url", sessionResultsUrl));
    switch (status) {
      case Failed:
        if (throwEx) {
          throw new TestFailedException(results, scenarioIdOrName, appIdOrName);
        }
        break;
      case Passed:
        break;
      case NotOpened:
        if (throwEx) {
          throw new EyesException("Called close before calling open");
        }
        break;
      case Unresolved:
        if (results.isNew()) {
          if (throwEx) {
            throw new NewTestException(results, scenarioIdOrName, appIdOrName);
          }
        } else {
          if (throwEx) {
            throw new DiffsFoundException(results, scenarioIdOrName, appIdOrName);
          }
        }
        break;
    }
  }

  public abstract StaleElementReferenceException getStaleElementException();

  public TestResultsSummary getAllTestResults() {
    return getAllTestResults(true);
  }

  public void setDontCloseBatches(boolean dontCloseBatches) {
    this.dontCloseBatches = dontCloseBatches;
  }

  public Logger getLogger() {
    return this.logger;
  }

  public void setProxy(AbstractProxySettings proxySettings) {
  }

  public AbstractProxySettings getProxy() {
    return null;
  }

  public String getAgentId() {
    return BASE_AGENT_ID;
  }

  /**
   * manager reference
   */
  public Reference getManagerRef() {
    return managerRef;
  }

  /**
   * @param  managerRef The manager reference
   */
  public void setManagerRef(Reference managerRef) {
    this.managerRef = managerRef;
  }

  /**
   * command executor
   */
  public CommandExecutor getCommandExecutor() {
    return commandExecutor;
  }

  /**
   * @param  commandExecutor The command executor
   */
  public void setCommandExecutor(CommandExecutor commandExecutor) {
    this.commandExecutor = commandExecutor;
  }

  public Boolean isDontCloseBatches() {
    return dontCloseBatches;
  }

  protected Refer getRefer() {
    return listener.getRefer();
  }
}
