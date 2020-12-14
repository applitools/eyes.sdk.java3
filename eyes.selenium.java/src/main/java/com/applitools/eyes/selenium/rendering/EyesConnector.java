package com.applitools.eyes.selenium.rendering;

import com.applitools.ICheckSettings;
import com.applitools.eyes.*;
import com.applitools.eyes.config.Configuration;
import com.applitools.eyes.fluent.ICheckSettingsInternal;
import com.applitools.eyes.visualgrid.model.*;
import com.applitools.eyes.visualgrid.services.IEyesConnector;
import com.applitools.utils.ClassVersionGetter;

import java.net.URI;
import java.util.List;
import java.util.concurrent.Future;

class EyesConnector extends EyesBase implements IEyesConnector, IBatchCloser {
    private final RenderBrowserInfo browserInfo;
    private Configuration configuration;
    private String appName;
    private String testName;
    private JobInfo jobInfo;

    public EyesConnector(Configuration configuration, List<PropertyData> properties, RenderBrowserInfo browserInfo) {
        this.configuration = configuration;
        this.browserInfo = browserInfo;
        if (properties != null) {
            for (PropertyData property : properties) {
                this.addProperty(property);
            }
        }
    }

    /**
     * Starts a new test without setting the viewport size of the AUT.
     */
    public void open(Configuration config, String appName, String testName) {
        this.configuration = config;
        this.appName = appName;
        this.testName = testName;
        logger.verbose("opening EyesConnector with viewport size: " + browserInfo.getViewportSize());
        openBase();
    }

    public Future<?> getResource(URI url, String userAgent, String refererUrl, TaskListener<RGridResource> listener) {
        return getServerConnector().downloadResource(url, userAgent, refererUrl, listener);
    }

    @Override
    public Future<?> renderPutResource(String renderId, RGridResource resource, TaskListener<Void> listener) {
        return getServerConnector().renderPutResource(renderId, resource, listener);
    }

    @Override
    public List<RunningRender> render(RenderRequest... renderRequests) {
        SyncTaskListener<List<RunningRender>> listener = new SyncTaskListener<>(logger, "render");
        getServerConnector().render(listener, renderRequests);
        return listener.get();
    }

    public List<RenderStatusResults> renderStatusById(String... renderIds) {
        final SyncTaskListener<List<RenderStatusResults>> listener = new SyncTaskListener<>(logger, "renderStatusById");
        getServerConnector().renderStatusById(listener, renderIds);
        return listener.get();
    }

    public MatchResult matchWindow(String resultImageURL, String domLocation, ICheckSettings checkSettings,
                                   List<? extends IRegion> regions, List<VisualGridSelector[]> regionSelectors, Location location,
                                   String renderId, String source, RectangleSize virtualViewport) {
        ICheckSettingsInternal checkSettingsInternal = (ICheckSettingsInternal) checkSettings;
        if (checkSettingsInternal.getStitchContent() == null) {
            checkSettings.fully();
        }

        MatchWindowTask matchWindowTask = new MatchWindowTask(this.logger, getServerConnector(), this.runningSession, getConfigurationInstance().getMatchTimeout(), this);
        ImageMatchSettings imageMatchSettings = MatchWindowTask.createImageMatchSettings(checkSettingsInternal, this);
        String tag = checkSettingsInternal.getName();
        AppOutput appOutput = new AppOutput(tag, null, domLocation, resultImageURL, location, virtualViewport);
        return matchWindowTask.performMatch(appOutput, tag, checkSettingsInternal, imageMatchSettings, regions, regionSelectors, this, renderId, source);
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

    public void setRenderInfo(RenderingInfo renderInfo) {
        getServerConnector().setRenderingInfo(renderInfo);
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

    public Configuration setApiKey(String apiKey) {
        return super.setApiKey(apiKey);
    }

    public Configuration setServerUrl(URI serverUrl) {
        return super.setServerUrl(serverUrl);
    }

    public void setBranchName(String branchName) {
        getConfigurationInstance().setBranchName(branchName);
    }

    public void setParentBranchName(String parentBranchName) {
        getConfigurationInstance().setParentBranchName(parentBranchName);
    }

    protected Object getAppEnvironment() {
        return getJobInfo().getEyesEnvironment();
    }

    public RunningSession getSession() {
        return this.runningSession;
    }

    @Override
    public void checkResourceStatus(TaskListener<Boolean[]> listener, String renderId, HashObject... hashes) {
        getServerConnector().checkResourceStatus(listener, renderId, hashes);
    }

    @Override
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

    @Override
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

    protected String getAppName() {
        return this.appName;
    }

    protected String getTestName() {
        return this.testName;
    }

    public void closeBatch(String batchId) {
        getServerConnector().closeBatch(batchId);
    }
}