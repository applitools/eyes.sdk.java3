package com.applitools.eyes;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import com.applitools.connectivity.ServerConnector;
import com.applitools.eyes.logging.Stage;
import com.applitools.eyes.logging.Type;
import com.applitools.universal.CommandExecutor;
import com.applitools.universal.ManagerType;
import com.applitools.universal.Reference;
import com.applitools.utils.GeneralUtils;
import org.apache.commons.lang3.tuple.Pair;

public abstract class EyesRunner {
  protected ServerConnector serverConnector = new ServerConnector();
  private TestResultsSummary allTestResults = null;
  protected CommandExecutor commandExecutor;

  /**
   * this reference has to be used in order to perform manager related actions (EyesManager.openEyes, EyesManager.closeAllEyes)
   */
  protected Reference managerRef;

  /**
   * name of the client sdk
   */
  protected final String BASE_AGENT_ID = "eyes.sdk.java";

  /**
   * version of the client sdk
   */
  protected final String VERSION = "1.0.0";

  /**
   * used for instantiating Classic Runner
   */
  public EyesRunner() {
    commandExecutor = new CommandExecutor(BASE_AGENT_ID, VERSION);
  }

  private boolean dontCloseBatches = false;

  protected Logger logger = new Logger();

  private final Map<String, IBatchCloser> batchesServerConnectorsMap = new HashMap<>();

  public abstract TestResultsSummary getAllTestResultsImpl(boolean shouldThrowException);

  public TestResultsSummary getAllTestResults() {
    return getAllTestResults(true);
  }

  public TestResultsSummary getAllTestResults(boolean shouldThrowException) {
    logger.log(new HashSet<String>(), Stage.CLOSE, Type.CALLED);
    if (allTestResults != null) {
      return allTestResults;
    }

    try {
      allTestResults = getAllTestResultsImpl(shouldThrowException);
    } finally {
      deleteAllBatches();
    }

    serverConnector.closeConnector();
    logger.getLogHandler().close();
    return allTestResults;
  }

  private void deleteAllBatches() {
    if (dontCloseBatches) {
      return;
    }

    boolean dontCloseBatchesStr = GeneralUtils.getDontCloseBatches();
    if (dontCloseBatchesStr) {
      return;
    }

    logger.log(new HashSet<String>(), Stage.CLOSE, Type.CLOSE_BATCH, Pair.of("batchSize", batchesServerConnectorsMap.size()));
    for (String batch : batchesServerConnectorsMap.keySet()) {
      IBatchCloser connector = batchesServerConnectorsMap.get(batch);
      connector.closeBatch(batch);
    }
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

  public void setServerUrl(String serverUrl) {
    if (serverUrl != null) {
      URI defaultServerUrl = GeneralUtils.getServerUrl();
      if (serverConnector.getServerUrl().equals(defaultServerUrl) && !serverUrl.equals(defaultServerUrl.toString())) {
        try {
          serverConnector.setServerUrl(new URI(serverUrl));
        } catch (URISyntaxException e) {
          GeneralUtils.logExceptionStackTrace(logger, Stage.GENERAL, e);
        }
      } else if (!serverConnector.getServerUrl().toString().equals(serverUrl)) {
        throw new EyesException(String.format("Server url was already set to %s", serverConnector.getServerUrl()));
      }
    }
  }

  public String getServerUrl() {
    return serverConnector.getServerUrl().toString();
  }

  public String getApiKey() {
    return serverConnector.getApiKey();
  }

  public void setApiKey(String apiKey) {
    if (apiKey != null) {
      if (!serverConnector.wasApiKeySet()) {
        serverConnector.setApiKey(apiKey);
      } else if (!serverConnector.getApiKey().equals(apiKey)) {
        throw new EyesException(String.format("Api key was already set to %s", serverConnector.getApiKey()));
      }
    }
  }

  public void setServerConnector(ServerConnector serverConnector) {
    this.serverConnector = serverConnector;
  }

  public ServerConnector getServerConnector() {
    return serverConnector;
  }

  public void setProxy(AbstractProxySettings proxySettings) {
    if (proxySettings != null) {
      if (serverConnector.getProxy() == null) {
        serverConnector.setProxy(proxySettings);
      } else if (!serverConnector.getProxy().equals(proxySettings)) {
        throw new EyesException("Proxy was already set");
      }
    }
  }

  public AbstractProxySettings getProxy() {
    return serverConnector.getProxy();
  }

  public void setAgentId(String agentId) {
    if (agentId != null) {
      serverConnector.setAgentId(agentId);
    }
  }

  public String getAgentId() {
    return serverConnector.getAgentId();
  }

  /**
   * manager reference
   */
  public Reference getManagerRef() {
    return managerRef;
  }

  /**
   * @param  managerRef
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
   * @param  commandExecutor
   */
  public void setCommandExecutor(CommandExecutor commandExecutor) {
    this.commandExecutor = commandExecutor;
  }
}
