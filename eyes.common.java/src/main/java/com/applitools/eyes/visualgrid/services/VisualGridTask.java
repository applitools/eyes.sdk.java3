package com.applitools.eyes.visualgrid.services;

import com.applitools.ICheckSettings;
import com.applitools.ICheckSettingsInternal;
import com.applitools.eyes.*;
import com.applitools.eyes.exceptions.DiffsFoundException;
import com.applitools.eyes.selenium.IConfigurationGetter;
import com.applitools.eyes.visualgrid.model.*;
import com.applitools.eyes.visualgrid.model.RenderBrowserInfo;
import com.applitools.utils.ArgumentGuard;
import com.applitools.utils.GeneralUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicBoolean;

public class VisualGridTask implements Callable<TestResultContainer>, CompletableTask {


    private final Logger logger;

    private boolean isSent;
    private String source;

    public enum TaskType {OPEN, CHECK, CLOSE, ABORT}

    private IConfigurationGetter configurationGetter;
    private TestResults testResults;

    private IEyesConnector eyesConnector;
    private TaskType type;

    private RenderStatusResults renderResult;
    private List<TaskListener> listeners = new ArrayList<>();
    private ICheckSettingsInternal checkSettings;

    private RunningTest runningTest;
    private Throwable exception;
    private RenderingTask renderingTask = null;
    private AtomicBoolean isTaskComplete = new AtomicBoolean(false);

    private final List<VisualGridSelector[]> regionSelectors;

    interface TaskListener {

        void onTaskComplete(VisualGridTask visualGridTask);

        void onTaskFailed(Throwable e, VisualGridTask visualGridTask);

        void onRenderComplete(RenderingTask renderTask, Throwable e);

    }

    /******** BEGIN - PUBLIC FOR TESTING PURPOSES ONLY ********/
    public VisualGridTask(TaskType taskType, Logger logger, RunningTest runningTest)
    {
        this.logger = logger;
        this.type = taskType;
        this.runningTest = runningTest;
        this.regionSelectors = null;
    }
    /******** END - PUBLIC FOR TESTING PURPOSES ONLY ********/

    public VisualGridTask(IConfigurationGetter seleniumConfigurationProvider, TestResults testResults, IEyesConnector eyesConnector, TaskType type, TaskListener runningTestListener,
                          ICheckSettings checkSettings, RunningTest runningTest, List<VisualGridSelector[]> regionSelectors, String source) {
        this.configurationGetter = seleniumConfigurationProvider;
        this.testResults = testResults;
        this.eyesConnector = eyesConnector;
        this.type = type;
        this.regionSelectors = regionSelectors;
        this.listeners.add(runningTestListener);
        this.logger = runningTest.getLogger();
        this.source = source;
        if (checkSettings != null) {
            this.checkSettings = (ICheckSettingsInternal) checkSettings;
            this.checkSettings = this.checkSettings.clone();
        }
        this.runningTest = runningTest;
    }

    public RenderBrowserInfo getBrowserInfo() {
        return runningTest.getBrowserInfo();
    }

    public TaskType getType() {
        return type;
    }

    boolean isSent() {
        return isSent;
    }

    void setIsSent() {
        this.isSent = true;
    }

    @Override
    public TestResultContainer call() {
        try {
            testResults = null;
            switch (type) {
                case OPEN:
                    logger.verbose("VisualGridTask.run opening task");
                    if (renderResult != null) {
                        String userAgent = renderResult.getUserAgent();
                        RectangleSize deviceSize = renderResult.getDeviceSize();
                        eyesConnector.setUserAgent(userAgent);
                        eyesConnector.setDeviceSize(deviceSize);
                    } else {
                        // We are in exception mode - trying to do eyes.open() without first render
                        RenderBrowserInfo browserInfo = runningTest.getBrowserInfo();
                        //eyesConnector.setUserAgent(craftUserAgent(browserInfo));
                        eyesConnector.setDeviceSize(browserInfo.getViewportSize());
                    }
                    eyesConnector.open(configurationGetter, runningTest.getAppName(), runningTest.getTestName());
                    logger.verbose("Eyes Open Done.");
                    break;

                case CHECK:
                    logger.verbose("VisualGridTask.run check task");

                    String imageLocation = renderResult.getImageLocation();
                    String domLocation = renderResult.getDomLocation();

                    List<VGRegion> vgRegions = renderResult.getSelectorRegions();
                    List<IRegion> regions = new ArrayList<>();
                    if (vgRegions != null)
                    {
                        for(VGRegion reg : vgRegions)
                        {
                            if (reg.getError() != null)
                            {
                                logger.log(String.format("Warning: region error: %s", reg.getError()));
                            }
                            else
                            {
                                regions.add(reg);
                            }
                        }
                    }
                    if (imageLocation == null) {
                        logger.verbose("CHECKING IMAGE WITH NULL LOCATION - ");
                        logger.verbose(renderResult.toString());
                    }
                    Location location = null;
                    if (regionSelectors.size() > 0) {
                        VisualGridSelector[] targetSelector = regionSelectors.get(regionSelectors.size() - 1);
                        if (targetSelector.length > 0 && "target".equals(targetSelector[0].getCategory())) {
                            location = regions.get(regions.size() - 1).getLocation();
                        }
                    }

                    eyesConnector.matchWindow(imageLocation, domLocation, (ICheckSettings) checkSettings, regions, this.regionSelectors, location, renderResult.getRenderId(), source);
                    logger.verbose("match done");
                    break;

                case CLOSE:
                    logger.verbose("VisualGridTask.run close task");
                    try {
                        testResults = eyesConnector.close(true);
                    } catch (Throwable e) {
                        GeneralUtils.logExceptionStackTrace(logger, e);
                        if (e instanceof DiffsFoundException) {
                            DiffsFoundException diffException = (DiffsFoundException) e;
                            testResults = diffException.getTestResults();
                        }
                        this.exception = e;

                    }
                    logger.verbose("Eyes Close Done.");
                    break;

                case ABORT:
                    logger.verbose("VisualGridTask.run abort task");
                    testResults = eyesConnector.abortIfNotClosed();
                    logger.verbose("Closing a not opened test");
            }

            TestResultContainer testResultContainer = new TestResultContainer(testResults, runningTest.getBrowserInfo(), this.exception);
            notifySuccessAllListeners();
            this.isTaskComplete.set(true);
            return testResultContainer;
        } catch (Throwable e) {
            GeneralUtils.logExceptionStackTrace(logger, e);
            this.exception = new Error(e);
            notifyFailureAllListeners(new Error(e));
        }
        return null;
    }

    public static String toPascalCase(String str) {
        ArgumentGuard.notNullOrEmpty(str, "str");
        return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
    }

    private void notifySuccessAllListeners() {
        for (TaskListener listener : listeners) {
            listener.onTaskComplete(this);
        }
    }

    private void notifyFailureAllListeners(Error e) {
        for (TaskListener listener : listeners) {
            listener.onTaskFailed(e, this);
        }
    }

    private void notifyRenderCompleteAllListeners() {
        for (TaskListener listener : listeners) {
            listener.onRenderComplete(this.renderingTask, exception);
        }
    }

    public IEyesConnector getEyesConnector() {
        return eyesConnector;
    }

    public void setRenderResult(RenderStatusResults renderResult) {
        logger.verbose("enter");
        this.renderResult = renderResult;
        notifyRenderCompleteAllListeners();
        logger.verbose("exit");
    }

    public boolean isTaskReadyToCheck() {
        return this.renderResult != null || this.exception != null;
    }

    public RunningTest getRunningTest() {
        return runningTest;
    }

    public boolean getIsTaskComplete() {
        return isTaskComplete.get();
    }

    public void addListener(TaskListener listener) {
        this.listeners.add(listener);
    }

    public void setRenderError(String renderId, String error) {
        logger.verbose("enter - renderId: " + renderId);
        for (TaskListener listener : listeners) {
            exception = new Error("Render Failed for " + this.getBrowserInfo() + " (renderId: " + renderId + ") with reason: " + error);
            listener.onTaskFailed(exception, this);
        }
        logger.verbose("exit - renderId: " + renderId);
        renderResult = new RenderStatusResults();
        renderResult.setDeviceSize(configurationGetter.getViewportSize());
    }

    public Throwable getException() {
        return exception;
    }

    public void setException(Throwable exception) {
        this.exception = exception;
    }

    public void setExceptionAndAbort(Throwable exception) {
        logger.verbose("aborting task with exception");
        this.exception = exception;
        if(type == TaskType.CLOSE){
            type = TaskType.ABORT;
        }
        runningTest.abort(true, exception);
    }

    @Override
    public String toString() {
        return "VisualGridTask - Type: " + type + " ; Browser Info: " + getBrowserInfo();
    }

    public void setRenderingTask(RenderingTask renderingTask) {
        this.renderingTask = renderingTask;
    }

    public RunningSession getSession() {
        return this.eyesConnector.getSession();
    }

}

