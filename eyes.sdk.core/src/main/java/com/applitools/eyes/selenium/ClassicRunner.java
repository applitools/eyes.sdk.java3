package com.applitools.eyes.selenium;

import com.applitools.connectivity.ServerConnector;
import com.applitools.eyes.*;
import com.applitools.eyes.services.CheckService;
import com.applitools.eyes.services.CloseService;
import com.applitools.eyes.services.OpenService;
import com.applitools.eyes.visualgrid.services.ServiceTaskListener;
import com.applitools.utils.GeneralUtils;

import java.util.ArrayList;
import java.util.List;

public class ClassicRunner extends EyesRunner {
    private final OpenService openService;
    private final CheckService checkService;
    private final CloseService closeService;
    private final List<TestResultContainer> allTestResult = new ArrayList<>();

    public ClassicRunner() {
        openService = new OpenService(logger, serverConnector, 1);
        checkService = new CheckService(logger, serverConnector);
        closeService = new CloseService(logger, serverConnector);
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
        openService.setServerConnector(serverConnector);
        checkService.setServerConnector(serverConnector);
        closeService.setServerConnector(serverConnector);
    }

    public RunningSession open(SessionStartInfo sessionStartInfo) {
        final SyncTaskListener<RunningSession> listener = new SyncTaskListener<>(logger, String.format("openBase %s", sessionStartInfo));
        openService.operate(sessionStartInfo, new ServiceTaskListener<RunningSession>() {
            @Override
            public void onComplete(RunningSession taskResponse) {
                listener.onComplete(taskResponse);
            }

            @Override
            public void onFail(Throwable t) {
                GeneralUtils.logExceptionStackTrace(logger, t);
                listener.onFail();
            }
        });
        return listener.get();
    }

    public MatchResult check(MatchWindowData matchWindowData) {
        final SyncTaskListener<Boolean> listener = new SyncTaskListener<>(logger, String.format("uploadImage %s", matchWindowData.getRunningSession()));
        checkService.tryUploadImage(matchWindowData, new ServiceTaskListener<Void>() {
            @Override
            public void onComplete(Void taskResponse) {
                listener.onComplete(true);
            }

            @Override
            public void onFail(Throwable t) {
                GeneralUtils.logExceptionStackTrace(logger, t);
                listener.onFail();
            }
        });

        Boolean result = listener.get();
        if (result == null || !result) {
            throw new EyesException("Failed performing match with the server");
        }

        final SyncTaskListener<MatchResult> matchListener = new SyncTaskListener<>(logger, String.format("performMatch %s", matchWindowData.getRunningSession()));
        checkService.matchWindow(matchWindowData, matchListener);
        return matchListener.get();
    }

    public TestResults close(SessionStopInfo sessionStopInfo) {
        final SyncTaskListener<TestResults> listener = new SyncTaskListener<>(logger, String.format("stop session %s. isAborted: %b", sessionStopInfo.getRunningSession(), sessionStopInfo.isAborted()));
        closeService.operate(sessionStopInfo, new ServiceTaskListener<TestResults>() {
            @Override
            public void onComplete(TestResults taskResponse) {
                listener.onComplete(taskResponse);
            }

            @Override
            public void onFail(Throwable t) {
                GeneralUtils.logExceptionStackTrace(logger, t);
                listener.onFail();
            }
        });
        return listener.get();
    }
}
