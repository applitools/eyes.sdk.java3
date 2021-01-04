package com.applitools.eyes.selenium;

import com.applitools.connectivity.ServerConnector;
import com.applitools.eyes.*;
import com.applitools.eyes.logging.Stage;
import com.applitools.eyes.services.CheckService;
import com.applitools.eyes.services.CloseService;
import com.applitools.eyes.services.OpenService;
import com.applitools.eyes.visualgrid.services.CheckTask;
import com.applitools.utils.GeneralUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class ClassicServiceRunner extends Thread {

    private Logger logger;
    private final Map<String, RunningTest> runningTests;
    private final Map<String, List<MatchWindowData>> stepsWaitingForOpen = new HashMap<>();
    private final List<CheckTask> waitingCheckTasks = Collections.synchronizedList(new ArrayList<CheckTask>());
    private final Map<String, CheckTask> inProgressCheckTasks = Collections.synchronizedMap(new HashMap<String, CheckTask>());
    private final AtomicBoolean isRunning = new AtomicBoolean(true);
    private Throwable error = null;
    private final OpenService openService;
    private final CheckService checkService;
    private final CloseService closeService;

    public ClassicServiceRunner(Logger logger, ServerConnector serverConnector, Map<String, RunningTest> tests, int testConcurrency) {
        this.logger = logger;
        this.runningTests = tests;
        openService = new OpenService(logger, serverConnector, testConcurrency);
        checkService = new CheckService(logger, serverConnector);
        closeService = new CloseService(logger, serverConnector);
    }
    @Override
    public void run() {
        try {
            while (isRunning.get()) {
                openServiceIteration();
                checkServiceIteration();
                closeServiceIteration();
                try {
                    Thread.sleep(10);
                } catch (InterruptedException ignored) {}
            }
        } catch (Throwable e) {
            isRunning.set(false);
            error = e;
            GeneralUtils.logExceptionStackTrace(logger, Stage.GENERAL, e);
        }
    }

    public Throwable getError() {
        return error;
    }

    public void stopServices() {
        isRunning.set(false);
    }

    public void setLogger(Logger logger) {
        openService.setLogger(logger);
        checkService.setLogger(logger);
        closeService.setLogger(logger);
        this.logger = logger;
    }

    public void setServerConnector(ServerConnector serverConnector) {
        openService.setServerConnector(serverConnector);
        checkService.setServerConnector(serverConnector);
        closeService.setServerConnector(serverConnector);
    }

    public void addTest(RunningTest runningTest) {
        runningTests.put(runningTest.getTestId(), runningTest);
        openService.addInput(runningTest.getTestId(), runningTest.prepareForOpen());
    }

    public void addCheckTask(CheckTask checkTask) {
        waitingCheckTasks.add(checkTask);
    }

    private void openServiceIteration() {
        openService.run();
        for (Pair<String, RunningSession> pair : openService.getSucceededTasks()) {
            runningTests.get(pair.getLeft()).openCompleted(pair.getRight());
            if (!stepsWaitingForOpen.containsKey(pair.getLeft())) {
                continue;
            }

            for (MatchWindowData matchWindowData : stepsWaitingForOpen.remove(pair.getLeft())) {
                matchWindowData.setRunningSession(pair.getRight());
            }
        }
        for (Pair<String, Throwable> pair : openService.getFailedTasks()) {
            runningTests.get(pair.getLeft()).openFailed(pair.getRight());
        }
    }

    private void checkServiceIteration() {
        List<CheckTask> tasksToRemove = new ArrayList<>();
        synchronized (waitingCheckTasks) {
            for (CheckTask checkTask : waitingCheckTasks) {
                if (!checkTask.isTestActive()) {
                    tasksToRemove.add(checkTask);
                    continue;
                }

                if (checkTask.isReady()) {
                    tasksToRemove.add(checkTask);
                    MatchWindowData matchWindowData = runningTests.get(checkTask.getTestId()).prepareForMatch(checkTask);
                    checkService.addInput(checkTask.getStepId(), matchWindowData);
                    inProgressCheckTasks.put(checkTask.getStepId(), checkTask);
                    if (matchWindowData.getRunningSession() == null) {
                        if (!stepsWaitingForOpen.containsKey(checkTask.getTestId())) {
                            stepsWaitingForOpen.put(checkTask.getTestId(), new ArrayList<MatchWindowData>());
                        }

                        stepsWaitingForOpen.get(checkTask.getTestId()).add(matchWindowData);
                    }
                }
            }
        }

        waitingCheckTasks.removeAll(tasksToRemove);

        checkService.run();
        for (Pair<String, MatchResult> pair : checkService.getSucceededTasks()) {
            CheckTask checkTask = inProgressCheckTasks.remove(pair.getLeft());
            if (!checkTask.isTestActive()) {
                continue;
            }

            checkTask.onComplete(pair.getRight());
        }
        for (Pair<String, Throwable> pair : checkService.getFailedTasks()) {
            CheckTask checkTask = inProgressCheckTasks.remove(pair.getLeft());
            checkTask.onFail(pair.getRight());
        }
    }

    private void closeServiceIteration() {
        synchronized (runningTests) {
            for (RunningTest runningTest : runningTests.values()) {
                if (runningTest.isTestReadyToClose()) {
                    if (!runningTest.getIsOpen()) {
                        // If the test isn't open and is ready to close, it means the open failed
                        openService.decrementConcurrency();
                        runningTest.closeFailed(new EyesException("Eyes never opened"));
                        continue;
                    }

                    SessionStopInfo sessionStopInfo = runningTest.prepareStopSession(runningTest.isTestAborted());
                    closeService.addInput(runningTest.getTestId(), sessionStopInfo);
                }
            }
        }

        closeService.run();
        for (Pair<String, TestResults> pair : closeService.getSucceededTasks()) {
            RunningTest runningTest = runningTests.get(pair.getLeft());
            runningTest.closeCompleted(pair.getRight());
            openService.decrementConcurrency();
        }

        for (Pair<String, Throwable> pair : closeService.getFailedTasks()) {
            RunningTest runningTest = runningTests.get(pair.getLeft());
            runningTest.closeFailed(pair.getRight());
            openService.decrementConcurrency();
        }
    }
}
