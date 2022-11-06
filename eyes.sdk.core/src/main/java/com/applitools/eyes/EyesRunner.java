package com.applitools.eyes;

import com.applitools.eyes.exceptions.DiffsFoundException;
import com.applitools.eyes.exceptions.NewTestException;
import com.applitools.eyes.exceptions.StaleElementReferenceException;
import com.applitools.eyes.exceptions.TestFailedException;
import com.applitools.eyes.logging.Stage;
import com.applitools.eyes.logging.Type;
import com.applitools.eyes.universal.CommandExecutor;
import com.applitools.eyes.universal.Reference;
import com.applitools.eyes.universal.dto.TestResultsSummaryDto;
import com.applitools.eyes.universal.mapper.TestResultsSummaryMapper;
import com.applitools.eyes.universal.server.UniversalSdkNativeLoader;
import com.applitools.eyes.visualgrid.services.RunnerOptions;
import com.applitools.utils.ArgumentGuard;
import com.applitools.utils.ClassVersionGetter;
import org.apache.commons.lang3.tuple.Pair;

import java.util.HashMap;
import java.util.Map;

public abstract class EyesRunner {
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

  /**
   * used for instantiating VisualGrid Runner
   */
  public EyesRunner(String baseAgentId, String version, RunnerOptions runnerOptions) {
    ArgumentGuard.notNull(runnerOptions, "runnerOptions");
    setLogHandler(runnerOptions.getLogHandler());
    runServer(baseAgentId, version);
  }


  protected void runServer(String baseAgentId, String version){
    UniversalSdkNativeLoader.setLogger(getLogger());
    UniversalSdkNativeLoader.start();
    commandExecutor = CommandExecutor.getInstance(baseAgentId, version, getStaleElementException());
  }

  public abstract StaleElementReferenceException getStaleElementException();

  private Boolean dontCloseBatches;

  protected Logger logger = new Logger();

  private final Map<String, IBatchCloser> batchesServerConnectorsMap = new HashMap<>();

  public abstract TestResultsSummary getAllTestResultsImpl(boolean shouldThrowException);

  public TestResultsSummary getAllTestResults() {
    return getAllTestResults(true);
  }

  public TestResultsSummary getAllTestResults(boolean shouldThrowException) {
    TestResultsSummaryDto dto = commandExecutor.closeManager(managerRef, shouldThrowException);
    return TestResultsSummaryMapper.fromDto(dto, shouldThrowException);
  }

  private void deleteAllBatches() {
//    if (dontCloseBatches) {
//      return;
//    }
//
//    boolean dontCloseBatchesStr = GeneralUtils.getDontCloseBatches();
//    if (dontCloseBatchesStr) {
//      return;
//    }
//
//    logger.log(new HashSet<String>(), Stage.CLOSE, Type.CLOSE_BATCH, Pair.of("batchSize", batchesServerConnectorsMap.size()));
//    for (String batch : batchesServerConnectorsMap.keySet()) {
//      IBatchCloser connector = batchesServerConnectorsMap.get(batch);
//      connector.closeBatch(batch);
//    }
  }

  public void setLogHandler(LogHandler logHandler) {
    logger.setLogHandler(logHandler);
    if (!logHandler.isOpen()) {
      logHandler.open();
    }
  }

  public void setDontCloseBatches(boolean dontCloseBatches) {
    this.dontCloseBatches = dontCloseBatches;
  }

  public Logger getLogger() {
    return this.logger;
  }

  public void addBatch(String batchId, IBatchCloser batchCloser) {
    if (!batchesServerConnectorsMap.containsKey(batchId)) {
      batchesServerConnectorsMap.put(batchId, batchCloser);
    }
  }

//  public abstract void setServerUrl(String serverUrl);
//  public void setServerUrl(String serverUrl) {
//    if (serverUrl != null) {
//      URI defaultServerUrl = GeneralUtils.getServerUrl();
//      if (serverConnector.getServerUrl().equals(defaultServerUrl) && !serverUrl.equals(defaultServerUrl.toString())) {
//        try {
//          serverConnector.setServerUrl(new URI(serverUrl));
//        } catch (URISyntaxException e) {
//          GeneralUtils.logExceptionStackTrace(logger, Stage.GENERAL, e);
//        }
//      } else if (!serverConnector.getServerUrl().toString().equals(serverUrl)) {
//        throw new EyesException(String.format("Server url was already set to %s", serverConnector.getServerUrl()));
//      }
//    }
//  }

//  public abstract String getServerUrl();
//  public String getServerUrl() {
//    return serverConnector.getServerUrl().toString();
//  }

//  public abstract String getApiKey();
//  public String getApiKey() {
//    return serverConnector.getApiKey();
//  }

//  public abstract void setApiKey(String apiKey);
//  public void setApiKey(String apiKey) {
//    if (apiKey != null) {
//      if (!serverConnector.wasApiKeySet()) {
//        serverConnector.setApiKey(apiKey);
//      } else if (!serverConnector.getApiKey().equals(apiKey)) {
//        throw new EyesException(String.format("Api key was already set to %s", serverConnector.getApiKey()));
//      }
//    }
//  }

  public void setProxy(AbstractProxySettings proxySettings) {
//    if (proxySettings != null) {
//      if (serverConnector.getProxy() == null) {
//        serverConnector.setProxy(proxySettings);
//      } else if (!serverConnector.getProxy().equals(proxySettings)) {
//        throw new EyesException("Proxy was already set");
//      }
//    }
  }

  public AbstractProxySettings getProxy() {
    return null; //serverConnector.getProxy();
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

  public Boolean isDontCloseBatches() {
    return dontCloseBatches;
  }
}
