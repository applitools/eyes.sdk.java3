package com.applitools.eyes.visualgrid.services;


import com.applitools.ICheckSettings;
import com.applitools.connectivity.ServerConnector;
import com.applitools.eyes.*;
import com.applitools.eyes.config.Configuration;
import com.applitools.eyes.fluent.ICheckSettingsInternal;
import com.applitools.eyes.logging.Stage;
import com.applitools.eyes.logging.TraceLevel;
import com.applitools.eyes.visualgrid.model.*;
import com.applitools.utils.ClassVersionGetter;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;

public class VisualGridRunningTest extends RunningTest {
    // The maximum number of steps which can run in parallel
    static final int PARALLEL_STEPS_LIMIT = 1;

    private final Configuration configuration;
    final List<CheckTask> checkTasks = new ArrayList<>();
    private JobInfo jobInfo;

    public VisualGridRunningTest(RenderBrowserInfo browserInfo, Logger logger, Configuration configuration) {
        super(browserInfo, logger);
        logger.log(getTestId(), Stage.GENERAL, Pair.of("browserInfo", browserInfo));
        this.configuration = configuration;
    }

    public VisualGridRunningTest(Configuration configuration, RenderBrowserInfo browserInfo,
                                 List<PropertyData> properties, Logger logger, ServerConnector serverConnector) {
        this(browserInfo, logger, configuration);
        this.setServerConnector(serverConnector);
        if (properties != null) {
            for (PropertyData property : properties) {
                this.addProperty(property);
            }
        }
    }

    private void removeAllCheckTasks() {
        checkTasks.clear();
    }

    boolean isCheckTaskReadyForRender(CheckTask checkTask) {
        if (!getIsOpen()) {
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

    @Override
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
                    logger.log(TraceLevel.Error, getTestId(), Stage.CHECK, Pair.of("regionError", reg.getError()));
                } else {
                    regions.add(reg);
                }
            }
        }

        ICheckSettingsInternal checkSettingsInternal = (ICheckSettingsInternal) checkTask.getCheckSettings();
        if (checkSettingsInternal.getStitchContent() == null) {
            checkTask.getCheckSettings().fully();
        }

        logger.log(TraceLevel.Info, getTestId(), Stage.CHECK,
                Pair.of("configuration", getConfiguration()),
                Pair.of("checkSettings", checkSettingsInternal));
        ImageMatchSettings imageMatchSettings = MatchWindowTask.createImageMatchSettings(checkSettingsInternal, this);
        String tag = checkSettingsInternal.getName();
        AppOutput appOutput = new AppOutput(tag, null, domLocation, imageLocation, renderResult.getImagePositionInActiveFrame(), visualViewport);
        MatchWindowTask.collectRegions(imageMatchSettings, renderResult.getImagePositionInActiveFrame(), regions, checkTask.getRegionSelectors());
        MatchWindowTask.collectRegions(imageMatchSettings, checkSettingsInternal);
        return prepareForMatch(new ArrayList<Trigger>(), appOutput, tag, false, imageMatchSettings, renderId, checkTask.getSource());
    }

    @Override
    public CheckTask issueCheck(ICheckSettings checkSettings, List<VisualGridSelector[]> regionSelectors, String source) {
        CheckTask checkTask = new CheckTask(this, checkSettings, regionSelectors, source);
        checkTasks.add(checkTask);
        return checkTask;
    }

    @Override
    public void checkCompleted(CheckTask checkTask, MatchResult matchResult) {
        validateResult(matchResult);
        checkTasks.remove(checkTask);
    }

    @Override
    public void issueAbort(Throwable error, boolean forceAbort) {
        super.issueAbort(error, forceAbort);
        if (isTestAborted()) {
            removeAllCheckTasks();
        }
    }

    /**
     * @return true if the only task left is CLOSE task
     */
    public boolean isTestReadyToClose() {
        return super.isTestReadyToClose() && checkTasks.isEmpty();
    }

    @Override
    protected String getBaseAgentId() {
        return "eyes.selenium.visualgrid.java/" + ClassVersionGetter.CURRENT_VERSION;
    }

    public String tryCaptureDom() {
        return null;
    }

    protected Object getAppEnvironment() {
        return getJobInfo().getEyesEnvironment();
    }

    public JobInfo getJobInfo() {
        if (jobInfo != null) {
            return jobInfo;
        }

        SyncTaskListener<JobInfo[]> listener = new SyncTaskListener<>(logger, String.format("getJobInfo %s", browserInfo));
        RenderInfo renderInfo = new RenderInfo(browserInfo.getWidth(), browserInfo.getHeight(), null, null,
                null, browserInfo.getEmulationInfo(), browserInfo.getIosDeviceInfo());
        RenderRequest renderRequest = new RenderRequest(renderInfo, browserInfo.getPlatform(), browserInfo.getBrowserType());
        renderRequest.setTestId(getTestId());
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

    @Override
    protected Configuration getConfigurationInstance() {
        return configuration;
    }
}