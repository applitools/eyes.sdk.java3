package com.applitools.eyes;

import com.applitools.ICheckSettings;
import com.applitools.eyes.config.Configuration;
import com.applitools.eyes.fluent.ICheckSettingsInternal;
import com.applitools.eyes.logging.Stage;
import com.applitools.eyes.selenium.ClassicRunner;
import com.applitools.eyes.visualgrid.model.RenderBrowserInfo;
import com.applitools.eyes.visualgrid.model.VisualGridSelector;
import com.applitools.eyes.visualgrid.services.CheckTask;
import com.applitools.utils.GeneralUtils;

import java.util.List;

public abstract class RunningTest extends EyesBase implements IBatchCloser {
    protected final RenderBrowserInfo browserInfo;
    protected Throwable error = null;

    private Boolean isAbortIssued = null;
    private boolean inOpenProcess = false;
    private boolean startedCloseProcess = false;

    protected RunningTest(ClassicRunner runner) {
        super(runner);
        browserInfo = null;
    }

    protected RunningTest(RenderBrowserInfo browserInfo, Logger logger) {
        this.browserInfo = browserInfo;
        this.logger = logger;
    }

    public boolean isCloseTaskIssued() {
        return isAbortIssued != null;
    }

    public TestResultContainer getTestResultContainer() {
        return testResultContainer;
    }

    @Override
    public SessionStartInfo prepareForOpen() {
        inOpenProcess = true;
        return super.prepareForOpen();
    }

    @Override
    public void openCompleted(RunningSession result) {
        inOpenProcess = false;
        super.openCompleted(result);
    }

    public void openFailed(Throwable e) {
        inOpenProcess = false;
        setTestInExceptionMode(e);
    }

    public abstract MatchWindowData prepareForMatch(CheckTask checkTask);

    public abstract CheckTask issueCheck(ICheckSettings checkSettings, List<VisualGridSelector[]> regionSelectors, String source);

    public abstract void checkCompleted(CheckTask checkTask, MatchResult matchResult);

    public void issueClose() {
        if (isCloseTaskIssued()) {
            return;
        }

        isAbortIssued = false;
    }

    public void issueAbort(Throwable error, boolean forceAbort) {
        if (isCloseTaskIssued() && !forceAbort) {
            return;
        }

        isAbortIssued = true;
        if (this.error == null) {
            this.error = error;
        }
    }

    public void closeCompleted(TestResults testResults) {
        if (!isTestAborted()) {
            try {
                logSessionResultsAndThrowException(true, testResults);
            } catch (Throwable e) {
                GeneralUtils.logExceptionStackTrace(logger, Stage.CLOSE, e, getTestId());
                error = e;
            }
        }

        testResultContainer = new TestResultContainer(testResults, browserInfo, error);
    }

    public void closeFailed(Throwable t) {
        if (error == null) {
            error = t;
        }

        testResultContainer = new TestResultContainer(null, browserInfo, error);
    }

    @Override
    public SessionStopInfo prepareStopSession(boolean isAborted) {
        startedCloseProcess = true;
        return super.prepareStopSession(isAborted);
    }

    /**
     * @return true if the only task left is CLOSE task
     */
    public boolean isTestReadyToClose() {
        return !inOpenProcess && isAbortIssued != null && !startedCloseProcess;
    }

    public boolean isTestAborted() {
        return isAbortIssued != null && isAbortIssued;
    }

    public void setTestInExceptionMode(Throwable e) {
        if (isTestAborted()) {
            return;
        }
        issueAbort(e, true);
    }

    protected RectangleSize getViewportSize() {
        return RectangleSize.EMPTY;
    }

    protected Configuration setViewportSize(RectangleSize size) {
        return getConfigurationInstance();
    }

    protected String getInferredEnvironment() {
        return null;
    }

    protected EyesScreenshot getScreenshot(Region targetRegion, ICheckSettingsInternal checkSettingsInternal) {
        return null;
    }

    protected String getTitle() {
        return null;
    }

    public void closeBatch(String batchId) {
        getServerConnector().closeBatch(batchId);
    }

    public RenderBrowserInfo getBrowserInfo() {
        return browserInfo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        RunningTest that = (RunningTest) o;
        return getTestId().equals(that.getTestId());
    }

    @Override
    public String toString() {
        return "RunningTest{" +
                "browserInfo=" + browserInfo +
                ", testId='" + getTestId() + '\'' +
                '}';
    }
}
