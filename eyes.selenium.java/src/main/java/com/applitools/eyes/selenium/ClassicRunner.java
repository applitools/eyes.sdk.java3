package com.applitools.eyes.selenium;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.applitools.connectivity.ServerConnector;
import com.applitools.eyes.*;

import com.applitools.eyes.visualgrid.services.RunnerOptions;
import com.applitools.utils.ClassVersionGetter;


/**
 * used to manage multiple Eyes sessions when working without the Ultrafast Grid.
 */
public class ClassicRunner extends EyesRunner {
  private final List<TestResultContainer> allTestResult = new ArrayList<>();

  /**
   * name of the client sdk
   */
  protected static String BASE_AGENT_ID = "eyes.sdk.java";

  /**
   * version of the client sdk
   */
  protected static String VERSION = ClassVersionGetter.CURRENT_VERSION;


  public ClassicRunner() {
    this(BASE_AGENT_ID, VERSION, null);
  }

  public ClassicRunner(RunnerOptions runnerOptions) {
    this(BASE_AGENT_ID, VERSION, Optional.ofNullable(runnerOptions)
            .map(RunnerOptions::getLogHandler)
            .orElse(null));
  }

  protected ClassicRunner(String baseAgentId, String version, LogHandler logHandler) {
    super(baseAgentId, version, logHandler);
    managerRef = commandExecutor.coreMakeManager(ManagerType.CLASSIC.value, null, null);
  }

  @Override
  public TestResultsSummary getAllTestResultsImpl(boolean shouldThrowException) {
    if (shouldThrowException) {
      for (TestResultContainer testResults : allTestResult) {
        if (testResults.getException() != null) {
          throw new Error(testResults.getException());
        }
      }
    }

    return new TestResultsSummary(allTestResult);
  }

  public void aggregateResult(TestResultContainer testResult) {
    this.allTestResult.add(testResult);
  }

  @Override
  public void setServerConnector(ServerConnector serverConnector) {
    super.setServerConnector(serverConnector);
  }

  public RunningSession open(final String testId, SessionStartInfo sessionStartInfo) {
    final SyncTaskListener<RunningSession> listener = new SyncTaskListener<>(logger, String.format("openBase %s", sessionStartInfo));
    return listener.get();
  }

  public MatchResult check(final String testId, MatchWindowData matchWindowData) {
    final SyncTaskListener<Boolean> listener = new SyncTaskListener<>(logger, String.format("uploadImage %s", matchWindowData.getRunningSession()));

    Boolean result = listener.get();
    if (result == null || !result) {
      throw new EyesException("Failed performing match with the server");
    }

    final SyncTaskListener<MatchResult> matchListener =
        new SyncTaskListener<>(logger, String.format("performMatch %s", matchWindowData.getRunningSession()));
    return matchListener.get();
  }

  public TestResults close(final String testId, SessionStopInfo sessionStopInfo) {
    final SyncTaskListener<TestResults> listener = new SyncTaskListener<>(logger,
        String.format("stop session %s. isAborted: %b", sessionStopInfo.getRunningSession(), sessionStopInfo.isAborted()));
    return listener.get();
  }
}
