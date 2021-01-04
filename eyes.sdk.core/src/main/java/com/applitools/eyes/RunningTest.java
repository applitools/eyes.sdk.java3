package com.applitools.eyes;

import com.applitools.ICheckSettings;
import com.applitools.eyes.config.Configuration;
import com.applitools.eyes.fluent.ICheckSettingsInternal;
import com.applitools.eyes.logging.Stage;
import com.applitools.eyes.logging.Type;
import com.applitools.eyes.selenium.ClassicRunner;
import com.applitools.eyes.visualgrid.model.RenderBrowserInfo;
import com.applitools.eyes.visualgrid.model.VisualGridSelector;
import com.applitools.eyes.visualgrid.services.CheckTask;
import com.applitools.utils.GeneralUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;

public abstract class RunningTest extends EyesBase implements IBatchCloser {
    protected final RenderBrowserInfo browserInfo;
    protected Throwable error = null;

    private Boolean isAbortIssued = null;
    protected boolean inOpenProcess = false;
    private boolean startedCloseProcess = false;

    protected final List<CheckTask> checkTasks = new ArrayList<>();

    protected RunningTest(EyesRunner runner) {
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

    private void removeAllCheckTasks() {
        checkTasks.clear();
    }

    @Override
    public SessionStartInfo prepareForOpen() {
        isAbortIssued = null;
        inOpenProcess = true;
        startedCloseProcess = false;
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

    public boolean isCheckTaskReady(CheckTask checkTask) {
        return checkTasks.contains(checkTask);
    }

    public MatchWindowData prepareForMatch(CheckTask checkTask) {
        ICheckSettingsInternal checkSettingsInternal = (ICheckSettingsInternal) checkTask.getCheckSettings();
        return this.prepareForMatch(checkSettingsInternal, Arrays.asList(getUserInputs()), checkTask.getAppOutput(),
                checkSettingsInternal.getName(), false, checkTask.getImageMatchSettings(), null, checkTask.getSource());
    }

    public CheckTask issueCheck(ICheckSettings checkSettings, List<VisualGridSelector[]> regionSelectors, String source) {
        CheckTask checkTask = new CheckTask(this, checkSettings, regionSelectors, source);
        checkTasks.add(checkTask);
        return checkTask;
    }

    public void checkCompleted(CheckTask checkTask, MatchResult matchResult) {
        validateResult(matchResult);
        checkTasks.remove(checkTask);
    }

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

        removeAllCheckTasks();
    }

    public void closeCompleted(TestResults testResults) {
        startedCloseProcess = true;
        runningSession = null;
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
        startedCloseProcess = true;
        runningSession = null;
        if (error == null) {
            error = t;
        }

        testResultContainer = new TestResultContainer(null, browserInfo, error);
    }

    @Override
    public TestResults close(boolean throwEx) {
        logger.log(getTestId(), Stage.CLOSE, Type.CALLED, Pair.of("throwEx", throwEx));
        if (isAsync) {
            issueClose();
            return waitForEyesToFinish(throwEx);
        }

        TestResults results;
        try {
            results = stopSession(false);
            closeCompleted(results);
        } catch (Throwable e) {
            GeneralUtils.logExceptionStackTrace(logger, Stage.CLOSE, e, getTestId());
            closeFailed(e);
            throw e;
        }

        if (error != null && throwEx) {
            throw new Error(error);
        }

        ((ClassicRunner) runner).aggregateResult(testResultContainer);
        return results;
    }

    @Override
    public TestResults abortIfNotClosed() {
        logger.log(getTestId(), Stage.CLOSE, Type.CALLED);
        if (isAsync) {
            issueAbort(new EyesException("eyes.close wasn't called. Aborted the test"), false);
            return waitForEyesToFinish(false);
        }

        return super.abortIfNotClosed();
    }

    public TestResults waitForEyesToFinish(boolean throwException) {
        AsyncClassicRunner asyncClassicRunner = (AsyncClassicRunner) runner;
        while (!isCompleted() && asyncClassicRunner.getError() == null) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException ignored) {}
        }

        if (asyncClassicRunner.getError() != null) {
            throw new EyesException("Execution crashed", asyncClassicRunner.getError());
        }

        if (testResultContainer.getException() != null && throwException) {
            throw new Error(testResultContainer.getException());
        }

        return testResultContainer.getTestResults();
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
        return !inOpenProcess && isAbortIssued != null && !startedCloseProcess && checkTasks.isEmpty();
    }

    public boolean isTestAborted() {
        return isAbortIssued != null && isAbortIssued;
    }

    public void setTestInExceptionMode(Throwable e) {
        GeneralUtils.logExceptionStackTrace(logger, Stage.GENERAL, e, getTestId());
        if (isTestAborted()) {
            return;
        }
        issueAbort(e, true);
    }

    public boolean isNextCheckTask(CheckTask checkTask) {
        return !checkTasks.isEmpty() && checkTasks.get(0).equals(checkTask);
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

    public int getNumberOfStepsLeft() {
        return checkTasks.size();
    }

    public Map<String, RunningTest> getAllRunningTests() {
        Map<String, RunningTest> allTests = new HashMap<>();
        allTests.put(getTestId(), this);
        return allTests;
    }

    public List<TestResultContainer> getAllTestResults() {
        if (!isCompleted()) {
            return null;
        }

        return Collections.singletonList(testResultContainer);
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
    public int hashCode() {
        return Objects.hash(getTestId());
    }

    @Override
    public String toString() {
        return "RunningTest{" +
                "browserInfo=" + browserInfo +
                ", testId='" + getTestId() + '\'' +
                '}';
    }
}
