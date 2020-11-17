package com.applitools.eyes.visualgrid.services;


import com.applitools.ICheckSettings;
import com.applitools.eyes.*;
import com.applitools.eyes.capture.AppOutputWithScreenshot;
import com.applitools.eyes.config.Configuration;
import com.applitools.eyes.fluent.ICheckSettingsInternal;
import com.applitools.eyes.visualgrid.model.*;
import com.applitools.utils.ClassVersionGetter;
import com.applitools.utils.GeneralUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class RunningTest extends EyesBase implements IBatchCloser {
    // The maximum number of steps which can run in parallel
    static final int PARALLEL_STEPS_LIMIT = 1;

    final List<CheckTask> checkTasks = new ArrayList<>();
    private final RenderBrowserInfo browserInfo;
    private Throwable error = null;
    private Configuration configuration;
    private JobInfo jobInfo;
    private final Logger logger;

    private Boolean isAbortIssued = null;
    private boolean inOpenProcess = false;
    private boolean startedCloseProcess = false;
    private final String testId = UUID.randomUUID().toString();

    private TestResultContainer testResultContainer;

    /******** BEGIN - PUBLIC FOR TESTING PURPOSES ONLY ********/
    public RunningTest(RenderBrowserInfo browserInfo, Logger logger) {
        this.browserInfo = browserInfo;
        this.logger = logger;
    }

    public RunningTest(RenderBrowserInfo browserInfo, Logger logger, Configuration configuration) {
        this.browserInfo = browserInfo;
        this.configuration = configuration;
        this.logger = logger;
    }

    /******** END - PUBLIC FOR TESTING PURPOSES ONLY ********/

    public RunningTest(Configuration configuration, RenderBrowserInfo browserInfo,
                       List<PropertyData> properties, Logger logger) {
        this.browserInfo = browserInfo;
        this.configuration = configuration;
        this.logger = logger;
        if (properties != null) {
            for (PropertyData property : properties) {
                this.addProperty(property);
            }
        }
    }

    private void removeAllCheckTasks() {
        logger.verbose(String.format("Removing %s CHECK tasks from test", checkTasks.size()));
        checkTasks.clear();
    }

    public boolean isCloseTaskIssued() {
        return isAbortIssued != null;
    }

    boolean isCheckTaskReadyForRender(CheckTask checkTask) {
        if (!isOpen()) {
            return false;
        }

        if (!checkTasks.contains(checkTask)) {
            return false;
        }

        int notRenderedStepsCount = 0;
        for (CheckTask task : checkTasks) {
            if (task.equals(checkTask)) {
                break;
            }

            if (!task.isRenderFinished()) {
                notRenderedStepsCount++;
            }
        }

        return notRenderedStepsCount < PARALLEL_STEPS_LIMIT;
    }

    public RenderBrowserInfo getBrowserInfo() {
        return browserInfo;
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

    public CheckTask issueCheck(ICheckSettings checkSettings, List<VisualGridSelector[]> regionSelectors, String source) {
        logger.verbose(toString());
        CheckTask checkTask = new CheckTask(this, checkSettings, regionSelectors, source);
        checkTasks.add(checkTask);
        return checkTask;
    }

    public void checkCompleted(CheckTask checkTask, MatchResult matchResult) {
        validateResult(((ICheckSettingsInternal) checkTask.getCheckSettings()).getName(), matchResult);
        checkTasks.remove(checkTask);
    }

    public void issueClose() {
        if (isCloseTaskIssued()) {
            return;
        }

        logger.verbose(toString());
        isAbortIssued = false;
    }

    public void issueAbort(Throwable error, boolean forceAbort) {
        if (isCloseTaskIssued() && !forceAbort) {
            return;
        }

        logger.verbose(toString());
        isAbortIssued = true;
        removeAllCheckTasks();
        if (this.error == null) {
            this.error = error;
        }
    }

    public void closeCompleted(TestResults testResults) {
        if (!isTestAborted()) {
            try {
                logSessionResultsAndThrowException(logger, true, testResults);
            } catch (Throwable e) {
                GeneralUtils.logExceptionStackTrace(logger, e);
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

    public MatchWindowData prepareForMatch(CheckTask checkTask) {
        RenderStatusResults renderResult = checkTask.getRenderStatusResults();
        String imageLocation = renderResult.getImageLocation();
        String domLocation = renderResult.getDomLocation();
        String renderId = renderResult.getRenderId();
        RectangleSize visualViewport = renderResult.getVisualViewport();

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
        Location location = null;
        List<VisualGridSelector[]> regionSelectors = checkTask.getRegionSelectors();
        if (regionSelectors.size() > 0) {
            VisualGridSelector[] targetSelector = regionSelectors.get(regionSelectors.size() - 1);
            if (targetSelector.length > 0 && "target".equals(targetSelector[0].getCategory())) {
                location = regions.get(regions.size() - 1).getLocation();
            }
        }

        ICheckSettingsInternal checkSettingsInternal = (ICheckSettingsInternal) checkTask.getCheckSettings();
        if (checkSettingsInternal.getStitchContent() == null) {
            checkTask.getCheckSettings().fully();
        }

        MatchWindowTask matchWindowTask = new MatchWindowTask(this.logger, getServerConnector(), this.runningSession, getConfigurationInstance().getMatchTimeout(), this);
        ImageMatchSettings imageMatchSettings = MatchWindowTask.createImageMatchSettings(checkSettingsInternal, this);
        String tag = checkSettingsInternal.getName();
        AppOutput appOutput = new AppOutput(tag, null, domLocation, imageLocation, visualViewport);
        AppOutputWithScreenshot appOutputWithScreenshot = new AppOutputWithScreenshot(appOutput, null, location);
        return matchWindowTask.prepareForMatch(appOutputWithScreenshot, tag, checkSettingsInternal, imageMatchSettings, regions, regionSelectors, this, renderId, checkTask.getSource());
    }

    /**
     * @return true if the only task left is CLOSE task
     */
    public boolean isTestReadyToClose() {
        return !inOpenProcess && checkTasks.isEmpty() && isAbortIssued != null && !startedCloseProcess;
    }

    public boolean isTestAborted() {
        return isAbortIssued != null && isAbortIssued;
    }

    public boolean isTestCompleted() {
        return testResultContainer != null;
    }

    public void setTestInExceptionMode(Throwable e) {
        GeneralUtils.logExceptionStackTrace(logger, e);
        if (isTestAborted()) {
            return;
        }
        issueAbort(e, true);
        logger.verbose("releasing visualGridTaskList.");
    }

    public String getAppName() {
        return configuration.getAppName();
    }

    public String getTestName() {
        return configuration.getTestName();
    }

    public String getTestId() {
        return testId;
    }

    protected String getBaseAgentId() {
        return "eyes.selenium.visualgrid.java/" + ClassVersionGetter.CURRENT_VERSION;
    }

    protected RectangleSize getViewportSize() {
        return RectangleSize.EMPTY;
    }

    protected Configuration setViewportSize(RectangleSize size) {
        logger.log("WARNING setViewportSize() was called in Visual-Grid context");
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

    protected String getAUTSessionId() {
        return null;
    }

    @Override
    protected Configuration getConfigurationInstance() {
        return configuration;
    }

    protected void openLogger() {
        // do nothing.
    }

    protected void closeLogger() {
        // do nothing.
    }

    public String tryCaptureDom() {
        return null;
    }

    protected Object getAppEnvironment() {
        return getJobInfo().getEyesEnvironment();
    }

    public RunningSession getSession() {
        return this.runningSession;
    }

    public JobInfo getJobInfo() {
        if (jobInfo != null) {
            return jobInfo;
        }

        SyncTaskListener<JobInfo[]> listener = new SyncTaskListener<>(logger, String.format("getJobInfo %s", browserInfo));
        RenderInfo renderInfo = new RenderInfo(browserInfo.getWidth(), browserInfo.getHeight(), null, null,
                null, browserInfo.getEmulationInfo(), browserInfo.getIosDeviceInfo());
        RenderRequest renderRequest = new RenderRequest(renderInfo, browserInfo.getPlatform(), browserInfo.getBrowserType());
        getServerConnector().getJobInfo(listener, new RenderRequest[]{renderRequest});
        JobInfo[] jobInfos = listener.get();
        if (jobInfos == null) {
            throw new EyesException("Failed getting job info");
        }
        jobInfo = jobInfos[0];
        return jobInfo;
    }

    public String getRenderer() {
        return getJobInfo().getRenderer();
    }

    protected String getBaselineEnvName() {
        String baselineEnvName = this.browserInfo.getBaselineEnvName();
        if (baselineEnvName != null) {
            return baselineEnvName;
        }
        return getConfigurationInstance().getBaselineEnvName();
    }

    public void closeBatch(String batchId) {
        getServerConnector().closeBatch(batchId);
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
        return testId.equals(that.testId);
    }

    @Override
    public String toString() {
        return "RunningTest{" +
                "browserInfo=" + browserInfo +
                ", testId='" + testId + '\'' +
                '}';
    }
}