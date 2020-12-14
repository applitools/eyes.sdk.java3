package com.applitools.eyes.visualgrid.services;

import com.applitools.ICheckSettings;
import com.applitools.ICheckSettingsInternal;
import com.applitools.eyes.*;
import com.applitools.eyes.config.Configuration;
import com.applitools.eyes.exceptions.TestFailedException;
import com.applitools.eyes.visualgrid.model.RenderBrowserInfo;
import com.applitools.eyes.visualgrid.model.RenderStatusResults;
import com.applitools.eyes.visualgrid.model.VGRegion;
import com.applitools.eyes.visualgrid.model.VisualGridSelector;
import com.applitools.utils.GeneralUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

public class VisualGridTask implements Callable<TestResultContainer> {


    private final Logger logger;

    private boolean isSent;
    private String source;

    public enum TaskType {OPEN, CHECK, CLOSE, ABORT}

    private Configuration configuration;
    private TestResults testResults;

    private IEyesConnector eyesConnector;
    private TaskType type;

    private RenderStatusResults renderResult;
    private final List<VGTaskListener> listeners = new ArrayList<>();
    private ICheckSettingsInternal checkSettings;

    private final RunningTest runningTest;
    private Throwable exception;

    private final List<VisualGridSelector[]> regionSelectors;

    private boolean wasRenderTaskCreated = false;

    interface VGTaskListener {

        void onTaskComplete(VisualGridTask visualGridTask);

        void onTaskFailed(Throwable e, VisualGridTask visualGridTask);

        void onRenderComplete();

    }

    /******** BEGIN - PUBLIC FOR TESTING PURPOSES ONLY ********/
    public VisualGridTask(TaskType taskType, Logger logger, RunningTest runningTest) {
        this.logger = logger;
        this.type = taskType;
        this.runningTest = runningTest;
        this.regionSelectors = null;
    }

    /******** END - PUBLIC FOR TESTING PURPOSES ONLY ********/

    public VisualGridTask(Configuration configuration, TestResults testResults, IEyesConnector eyesConnector, TaskType type, VGTaskListener runningTestListener,
                          ICheckSettings checkSettings, RunningTest runningTest, List<VisualGridSelector[]> regionSelectors, String source) {
        this.configuration = configuration;
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
                    eyesConnector.open(configuration, runningTest.getAppName(), runningTest.getTestName());
                    logger.verbose("Eyes Open Done.");
                    break;

                case CHECK:
                    logger.verbose("VisualGridTask.run check task");

                    String imageLocation = renderResult.getImageLocation();
                    String domLocation = renderResult.getDomLocation();

                    List<VGRegion> vgRegions = renderResult.getSelectorRegions();
                    List<IRegion> regions = new ArrayList<>();
                    if (vgRegions != null) {
                        for (VGRegion reg : vgRegions) {
                            if (reg.getError() != null) {
                                logger.log(String.format("Warning: region error: %s", reg.getError()));
                            } else {
                                regions.add(reg);
                            }
                        }
                    }
                    if (imageLocation == null) {
                        logger.verbose("CHECKING IMAGE WITH NULL LOCATION - ");
                        logger.verbose(renderResult.toString());
                    }

                    eyesConnector.matchWindow(imageLocation, domLocation, (ICheckSettings) checkSettings, regions,
                            this.regionSelectors, renderResult.getImagePositionInActiveFrame(), renderResult.getRenderId(), source, renderResult.getVisualViewport());
                    logger.verbose("match done");
                    break;

                case CLOSE:
                    logger.verbose("VisualGridTask.run close task");
                    try {
                        testResults = eyesConnector.close(true);
                    } catch (Throwable e) {
                        GeneralUtils.logExceptionStackTrace(logger, e);
                        if (e instanceof TestFailedException) {
                            TestFailedException diffException = (TestFailedException) e;
                            testResults = diffException.getTestResults();
                        }
                        this.exception = e;

                    }
                    logger.verbose("Eyes Close Done.");
                    break;

                case ABORT:
                    logger.verbose("VisualGridTask.run abort task");
                    if (runningTest.isTestOpen()) {
                        testResults = eyesConnector.abortIfNotClosed();
                    }
                    logger.verbose("Closing a not opened test");
            }

            TestResultContainer testResultContainer = new TestResultContainer(testResults, runningTest.getBrowserInfo(), this.exception);
            notifySuccessAllListeners();
            return testResultContainer;
        } catch (Throwable e) {
            GeneralUtils.logExceptionStackTrace(logger, e);
            this.exception = new Error(e);
            notifyFailureAllListeners(new Error(e));
        }
        return null;
    }

    private void notifySuccessAllListeners() {
        for (VGTaskListener listener : listeners) {
            listener.onTaskComplete(this);
        }
    }

    private void notifyFailureAllListeners(Error e) {
        for (VGTaskListener listener : listeners) {
            listener.onTaskFailed(e, this);
        }
    }

    private void notifyRenderCompleteAllListeners() {
        for (VGTaskListener listener : listeners) {
            listener.onRenderComplete();
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

    public void addListener(VGTaskListener listener) {
        this.listeners.add(listener);
    }

    public void setRenderError(String renderId, String error) {
        logger.verbose("enter - renderId: " + renderId);
        for (VGTaskListener listener : listeners) {
            exception = new InstantiationError("Render Failed for " + this.getBrowserInfo() + " (renderId: " + renderId + ") with reason: " + error);
            listener.onTaskFailed(exception, this);
        }
        logger.verbose("exit - renderId: " + renderId);
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
        if (type == TaskType.CLOSE) {
            type = TaskType.ABORT;
        }
        runningTest.abort(true, exception);
    }

    @Override
    public String toString() {
        return "VisualGridTask - Type: " + type + " ; Browser Info: " + getBrowserInfo();
    }

    public RunningSession getSession() {
        return this.eyesConnector.getSession();
    }

    public boolean wasRenderTaskCreated() {
        return wasRenderTaskCreated;
    }

    public void setRenderTaskCreated() {
        wasRenderTaskCreated = true;
    }

    public boolean isReadyForRender() {
        return runningTest.isCheckTaskReadyForRender(this);
    }

    public String getRenderer() {
        return eyesConnector.getRenderer();
    }
}
