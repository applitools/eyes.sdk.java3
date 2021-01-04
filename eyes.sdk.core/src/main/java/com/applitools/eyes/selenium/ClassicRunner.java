package com.applitools.eyes.selenium;

import com.applitools.connectivity.ServerConnector;
import com.applitools.eyes.*;
import com.applitools.eyes.logging.Stage;
import com.applitools.eyes.logging.Type;
import com.applitools.eyes.services.CheckService;
import com.applitools.eyes.services.CloseService;
import com.applitools.eyes.services.OpenService;
import com.applitools.eyes.visualgrid.services.RunnerOptions;
import com.applitools.eyes.visualgrid.services.ServiceTaskListener;
import com.applitools.utils.GeneralUtils;

import java.util.ArrayList;
import java.util.List;

public class ClassicRunner extends EyesRunner implements IClassicRunner {
    private OpenService openService;
    private CheckService checkService;
    private CloseService closeService;
    private final List<TestResultContainer> allTestResult = new ArrayList<>();

    public ClassicRunner() {
        this(Thread.currentThread().getStackTrace()[2].getClassName());
    }

    public ClassicRunner(String suiteName) {
        this(new RunnerOptions().testConcurrency(Integer.MAX_VALUE), suiteName);
        testConcurrency.isDefault = true;
        init();
    }

    public ClassicRunner(RunnerOptions runnerOptions) {
        this(runnerOptions, Thread.currentThread().getStackTrace()[2].getClassName());
    }

    public ClassicRunner(RunnerOptions runnerOptions, String suiteName) {
        super(runnerOptions, suiteName);
        init();
        setProxy(runnerOptions.getProxy());
    }

    public void init() {
        openService = new OpenService(logger, serverConnector, testConcurrency.actualConcurrency);
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

    public RunningSession open(final EyesBase runningTest) {
        sendConcurrencyLog();
        SessionStartInfo sessionStartInfo = runningTest.prepareForOpen();
        if (sessionStartInfo == null) {
            return null;
        }

        while (openService.isConcurrencyLimitReached()) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException ignored) {}
        }
        final SyncTaskListener<RunningSession> listener = new SyncTaskListener<>(logger, String.format("openBase %s", sessionStartInfo));
        openService.operate(runningTest.getTestId(), sessionStartInfo, new ServiceTaskListener<RunningSession>() {
            @Override
            public void onComplete(RunningSession taskResponse) {
                listener.onComplete(taskResponse);
            }

            @Override
            public void onFail(Throwable t) {
                GeneralUtils.logExceptionStackTrace(logger, Stage.OPEN, t, runningTest.getTestId());
                listener.onFail();
            }
        });
        RunningSession runningSession = listener.get();
        if (runningSession == null) {
            throw new EyesException("Failed starting session with the server");
        }

        return runningSession;
    }

    public MatchResult check(final MatchWindowData matchWindowData) {
        final SyncTaskListener<Boolean> listener = new SyncTaskListener<>(logger, String.format("uploadImage %s", matchWindowData.getRunningSession()));
        checkService.tryUploadImage(matchWindowData, new ServiceTaskListener<Void>() {
            @Override
            public void onComplete(Void taskResponse) {
                listener.onComplete(true);
            }

            @Override
            public void onFail(Throwable t) {
                GeneralUtils.logExceptionStackTrace(logger, Stage.CHECK, Type.UPLOAD_COMPLETE, t, matchWindowData.getTestId());
                listener.onFail();
            }
        });

        Boolean result = listener.get();
        if (result == null || !result) {
            throw new EyesException("Failed performing match with the server");
        }

        final SyncTaskListener<MatchResult> matchListener = new SyncTaskListener<>(logger, String.format("performMatch %s", matchWindowData.getRunningSession()));
        checkService.matchWindow(matchWindowData, new ServiceTaskListener<MatchResult>() {
            @Override
            public void onComplete(MatchResult taskResponse) {
                matchListener.onComplete(taskResponse);
            }

            @Override
            public void onFail(Throwable t) {
                GeneralUtils.logExceptionStackTrace(logger, Stage.CHECK, Type.MATCH_COMPLETE, t, matchWindowData.getTestId());
                matchListener.onFail();
            }
        });
        return matchListener.get();
    }

    public TestResults close(final String testId, SessionStopInfo sessionStopInfo) {
        final SyncTaskListener<TestResults> listener = new SyncTaskListener<>(logger, String.format("stop session %s. isAborted: %b", sessionStopInfo.getRunningSession(), sessionStopInfo.isAborted()));
        closeService.operate(testId, sessionStopInfo, new ServiceTaskListener<TestResults>() {
            @Override
            public void onComplete(TestResults taskResponse) {
                openService.decrementConcurrency();
                listener.onComplete(taskResponse);
            }

            @Override
            public void onFail(Throwable t) {
                openService.decrementConcurrency();
                GeneralUtils.logExceptionStackTrace(logger, Stage.CLOSE, t, testId);
                listener.onFail();
            }
        });
        return listener.get();
    }

    @Override
    public void setServerConnector(ServerConnector serverConnector) {
        openService.setServerConnector(serverConnector);
        checkService.setServerConnector(serverConnector);
        closeService.setServerConnector(serverConnector);
    }
}
