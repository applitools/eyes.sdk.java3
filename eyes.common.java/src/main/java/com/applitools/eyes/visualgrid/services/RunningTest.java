package com.applitools.eyes.visualgrid.services;


import com.applitools.ICheckSettings;
import com.applitools.eyes.IBatchCloser;
import com.applitools.eyes.Logger;
import com.applitools.eyes.selenium.Configuration;
import com.applitools.eyes.selenium.ISeleniumConfigurationProvider;
import com.applitools.eyes.visualgrid.model.RenderBrowserInfo;
import com.applitools.eyes.visualgrid.model.RenderingTask;
import com.applitools.eyes.TestResultContainer;
import com.applitools.eyes.visualgrid.model.VisualGridSelector;

import java.util.*;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.atomic.AtomicBoolean;

public class RunningTest {
    private final List<VisualGridTask> visualGridTaskList = Collections.synchronizedList(new ArrayList<VisualGridTask>());
    private IEyesConnector eyes;
    private RenderBrowserInfo browserInfo;
    private AtomicBoolean isTestOpen = new AtomicBoolean(false);
    private AtomicBoolean isTestClose = new AtomicBoolean(false);
    private AtomicBoolean isTestInExceptionMode = new AtomicBoolean(false);
    private RunningTestListener listener;
    private ISeleniumConfigurationProvider configurationProvider;
    private HashMap<VisualGridTask, FutureTask<TestResultContainer>> taskToFutureMapping = new HashMap<>();
    private Logger logger;
    private AtomicBoolean isCloseTaskIssued = new AtomicBoolean(false);
    private VisualGridTask closeTask;
    private VisualGridTask openTask;
    private String appName;
    private String testName;
    private Throwable error;

    public RunningTest(IEyesConnector eyes, ISeleniumConfigurationProvider configuration, RenderBrowserInfo browserInfo, Logger logger, RunningTestListener listener) {
        this.eyes = eyes;
        this.browserInfo = browserInfo;
        this.configurationProvider = configuration;
        this.listener = listener;
        this.logger = logger;
        this.appName = configurationProvider.get().getAppName();
        this.testName = configurationProvider.get().getTestName();
    }

    public Future<TestResultContainer> abort(boolean forceAbort, Throwable e) {
        removeAllCheckTasks();
        if (isOpenTaskIssued()) {
            openTask.setException(e);
        }
        if (forceAbort && closeTask != null && closeTask.getType() == VisualGridTask.TaskType.CLOSE) {
            closeTask.setExceptionAndAbort(e);
        }
        if (closeTask == null) {
            VisualGridTask abortTask = new VisualGridTask(new Configuration(configurationProvider.get()), null,
                    eyes, VisualGridTask.TaskType.ABORT, taskListener, null, this, null, null);
            visualGridTaskList.add(abortTask);
            this.closeTask = abortTask;
            FutureTask<TestResultContainer> futureTask = new FutureTask<>(abortTask);
            taskToFutureMapping.put(abortTask, futureTask);
            this.isCloseTaskIssued.set(true);
        }
        return taskToFutureMapping.get(closeTask);
    }

    public IBatchCloser getBatchCloser() {
        return (IBatchCloser) this.eyes;
    }

    private void removeAllCheckTasks() {
        Iterator<VisualGridTask> iterator = visualGridTaskList.iterator();
        while (iterator.hasNext()) {
            VisualGridTask next = iterator.next();
            if (next.getType() == VisualGridTask.TaskType.CHECK) iterator.remove();

        }
    }

    public boolean isCloseTaskIssued() {
        return closeTask != null;
    }

    public interface RunningTestListener {

        void onTaskComplete(VisualGridTask visualGridTask, RunningTest test);

        void onRenderComplete();

    }

    private VisualGridTask.TaskListener taskListener = new VisualGridTask.TaskListener() {
        @Override
        public void onTaskComplete(VisualGridTask visualGridTask) {
            RunningTest runningTest = RunningTest.this;
            logger.verbose("locking runningTest.visualGridTaskList");
            synchronized (runningTest.visualGridTaskList) {
                runningTest.visualGridTaskList.remove(visualGridTask);
            }
            logger.verbose("releasing runningTest.visualGridTaskList");
            switch (visualGridTask.getType()) {
                case OPEN:
                    runningTest.setTestOpen(true);
                    break;
                case CLOSE:
                case ABORT:
                    RunningTest.this.isTestClose.set(true);
                    break;
            }
            if (runningTest.listener != null) {
                RunningTest.this.listener.onTaskComplete(visualGridTask, RunningTest.this);
            }
        }

        @Override
        public void onTaskFailed(Throwable e, VisualGridTask visualGridTask) {
            setTestInExceptionMode(e);
            listener.onTaskComplete(visualGridTask, RunningTest.this);
        }

        @Override
        public void onRenderComplete(RenderingTask renderingTask, Throwable error) {
            logger.verbose("enter");
            listener.onRenderComplete();
            logger.verbose("exit");
        }

    };


    public boolean isTestOpen() {
        return isTestOpen.get();
    }

    private void setTestOpen(boolean testOpen) {
        isTestOpen.set(testOpen);
    }

    public List<VisualGridTask> getVisualGridTaskList() {
        return visualGridTaskList;
    }

    public ScoreTask getScoreTaskObjectByType(VisualGridTask.TaskType taskType) {
        if (!this.isTestOpen.get() && taskType == VisualGridTask.TaskType.CHECK) return null;
        int score = 0;
        VisualGridTask chosenVisualGridTask;
        synchronized (this.visualGridTaskList) {
            for (VisualGridTask visualGridTask : this.visualGridTaskList) {
                if (visualGridTask.isTaskReadyToCheck() && visualGridTask.getType() == VisualGridTask.TaskType.CHECK) {
                    score++;
                }
            }

            if (this.visualGridTaskList.isEmpty())
                return null;

            chosenVisualGridTask = this.visualGridTaskList.get(0);
            if (chosenVisualGridTask.getType() != taskType || chosenVisualGridTask.isSent() || (taskType == VisualGridTask.TaskType.OPEN && !chosenVisualGridTask.isTaskReadyToCheck()))
                return null;
        }
        return new ScoreTask(chosenVisualGridTask, score);
    }

    public synchronized FutureTask<TestResultContainer> getNextCloseTask() {
//        logger.verbose("enter");
        if (!visualGridTaskList.isEmpty() && isCloseTaskIssued.get()) {
            VisualGridTask visualGridTask = visualGridTaskList.get(0);
            VisualGridTask.TaskType type = visualGridTask.getType();
            if (type != VisualGridTask.TaskType.CLOSE && type != VisualGridTask.TaskType.ABORT) return null;
//            logger.verbose("locking visualGridTaskList");
            synchronized (visualGridTaskList) {
//                logger.verbose("removing visualGridTask " + visualGridTask.toString() + " and exiting");
                visualGridTaskList.remove(visualGridTask);
//                logger.verbose("tasks in visualGridTaskList: " + visualGridTaskList.size());
            }
//            logger.verbose("releasing visualGridTaskList");
            return taskToFutureMapping.get(visualGridTask);
        }
//        logger.verbose("exit with null");
        return null;
    }

    public RenderBrowserInfo getBrowserInfo() {
        return browserInfo;
    }

    public VisualGridTask open() {
        logger.verbose("adding Open visualGridTask...");
        VisualGridTask visualGridTask = new VisualGridTask(new Configuration(configurationProvider.get()), null,
                eyes, VisualGridTask.TaskType.OPEN, taskListener, null, this, null, null);
        openTask = visualGridTask;
        FutureTask<TestResultContainer> futureTask = new FutureTask<>(visualGridTask);
        this.taskToFutureMapping.put(visualGridTask, futureTask);
        logger.verbose("locking visualGridTaskList");
        synchronized (this.visualGridTaskList) {
            this.visualGridTaskList.add(visualGridTask);
            logger.verbose("Open visualGridTask was added: " + visualGridTask.toString());
            logger.verbose("tasks in visualGridTaskList: " + visualGridTaskList.size());
        }
        logger.verbose("releasing visualGridTaskList");
        return visualGridTask;
    }

    public FutureTask<TestResultContainer> close() {
        VisualGridTask lastVisualGridTask;
        if (!this.visualGridTaskList.isEmpty()) {
            logger.verbose("visual grid tasks list not empty");
            lastVisualGridTask = this.visualGridTaskList.get(visualGridTaskList.size() - 1);
            VisualGridTask.TaskType type = lastVisualGridTask.getType();
            if (type == VisualGridTask.TaskType.CLOSE || type == VisualGridTask.TaskType.ABORT) {
                closeTask = lastVisualGridTask;
                logger.verbose("returning last task future (type of task: " + type + ")");
                return taskToFutureMapping.get(lastVisualGridTask);
            }
        } else {
            if (closeTask != null) {
                logger.verbose("returning future of close task");
                return taskToFutureMapping.get(closeTask);
            }
        }

        logger.verbose("adding close visualGridTask...");
        VisualGridTask visualGridTask = new VisualGridTask(new Configuration(configurationProvider.get()), null,
                eyes, VisualGridTask.TaskType.CLOSE, taskListener, null, this, null, null);
        FutureTask<TestResultContainer> futureTask = new FutureTask<>(visualGridTask);
        closeTask = visualGridTask;
        isCloseTaskIssued.set(true);
        this.taskToFutureMapping.put(visualGridTask, futureTask);
        logger.verbose("locking visualGridTaskList");
        synchronized (visualGridTaskList) {
            this.visualGridTaskList.add(visualGridTask);
            logger.verbose("Close visualGridTask was added: " + visualGridTask.toString());
            logger.verbose("tasks in visualGridTaskList: " + visualGridTaskList.size());
            if (visualGridTaskList.size()>1){
                StringBuilder sb = new StringBuilder();
                for (VisualGridTask vgt: visualGridTaskList) {
                    sb.append(vgt.getType()).append(" ; ");
                }
                logger.verbose(sb.toString());
            }
        }
        logger.verbose("releasing visualGridTaskList");
        FutureTask<TestResultContainer> testResultContainerFutureTask = this.taskToFutureMapping.get(visualGridTask);
        return testResultContainerFutureTask;
    }

    public VisualGridTask check(ICheckSettings checkSettings, List<VisualGridSelector[]> regionSelectors, String source) {
        logger.verbose("adding check visualGridTask...");
        VisualGridTask visualGridTask = new VisualGridTask(new Configuration(configurationProvider.get()), null,
                eyes, VisualGridTask.TaskType.CHECK, taskListener, checkSettings, this, regionSelectors, source);
        logger.verbose("locking visualGridTaskList");
        synchronized (visualGridTaskList) {
            this.visualGridTaskList.add(visualGridTask);
            logger.verbose("Check VisualGridTask was added: " + visualGridTask.toString());
            logger.verbose("tasks in visualGridTaskList: " + visualGridTaskList.size());
        }
        logger.verbose("releasing visualGridTaskList");
        this.taskToFutureMapping.get(visualGridTask);
        return visualGridTask;
    }

    /**
     * @return true if the only task left is CLOSE task
     */
    public boolean isTestReadyToClose() {

        if (visualGridTaskList.size() != 1) return false;

        for (VisualGridTask visualGridTask : visualGridTaskList) {
            if (visualGridTask.getType() == VisualGridTask.TaskType.CLOSE || visualGridTask.getType() == VisualGridTask.TaskType.ABORT)
                return true;
        }

        return false;
    }

    public boolean isTestClose() {
        return isTestClose.get();
    }

    public IEyesConnector getEyes() {
        return eyes;
    }

    public void setTestInExceptionMode(Throwable e) {
        this.isTestInExceptionMode.set(true);
        this.error = e;

        if (closeTask != null) {
            logger.verbose("locking visualGridTaskList.");
            synchronized (visualGridTaskList) {
                removeAllCheckTasks();
                if (closeTask != null) {
                    if (!visualGridTaskList.contains(closeTask)) {
                        this.visualGridTaskList.add(closeTask);
                    }
                    closeTask.setExceptionAndAbort(e);
                }
            }
        }
        if (openTask != null) {
            openTask.setExceptionAndAbort(e);
        }
        logger.verbose("releasing visualGridTaskList.");
    }

    Logger getLogger() {
        return logger;
    }

    public boolean isOpenTaskIssued() {
        return openTask != null;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getTestName() {
        return testName;
    }

    public void setTestName(String testName) {
        this.testName = testName;
    }
}