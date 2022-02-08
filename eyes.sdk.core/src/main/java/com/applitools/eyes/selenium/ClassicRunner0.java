package com.applitools.eyes.selenium;

import java.util.ArrayList;
import java.util.List;

import com.applitools.connectivity.ServerConnector;
import com.applitools.eyes.EyesException;

import com.applitools.eyes.MatchResult;
import com.applitools.eyes.MatchWindowData;
import com.applitools.eyes.RunningSession;
import com.applitools.eyes.SessionStartInfo;
import com.applitools.eyes.SessionStopInfo;
import com.applitools.eyes.SyncTaskListener;
import com.applitools.eyes.TestResultContainer;
import com.applitools.eyes.TestResults;
import com.applitools.eyes.TestResultsSummary;
import com.applitools.eyes.visualgrid.services.EyesRunner0;


/**
 * used to manage multiple Eyes sessions when working without the Ultrafast Grid.
 */
public class ClassicRunner0 extends EyesRunner0 {
  private final List<TestResultContainer> allTestResult = new ArrayList<>();


  public ClassicRunner0() {

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
