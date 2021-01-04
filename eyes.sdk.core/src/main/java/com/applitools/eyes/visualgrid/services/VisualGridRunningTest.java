package com.applitools.eyes.visualgrid.services;


import com.applitools.connectivity.ServerConnector;
import com.applitools.eyes.*;
import com.applitools.eyes.capture.ScreenshotProvider;
import com.applitools.eyes.config.Configuration;
import com.applitools.eyes.fluent.ICheckSettingsInternal;
import com.applitools.eyes.locators.BaseOcrRegion;
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
    private JobInfo jobInfo;

    private final boolean isWeb;

    VisualGridRunningTest(Logger logger, boolean isWeb, String eyesId, RenderBrowserInfo browserInfo, Configuration configuration) {
        super(browserInfo, logger);
        setTestId(String.format("%s/%s", eyesId, getTestId()));
        logger.log(getTestId(), Stage.GENERAL, Pair.of("browserInfo", browserInfo));
        this.configuration = configuration;
        this.isWeb = isWeb;
    }

    public VisualGridRunningTest(Logger logger, boolean isWeb, String eyesId, Configuration configuration, RenderBrowserInfo browserInfo,
                                 List<PropertyData> properties, ServerConnector serverConnector, String agentRunId) {
        this(logger, isWeb, eyesId, browserInfo, configuration);
            this.agentRunId = agentRunId;
        this.setServerConnector(serverConnector);
        if (properties != null) {
            for (PropertyData property : properties) {
                this.addProperty(property);
            }
        }
    }

    public boolean isCheckTaskReady(CheckTask checkTask) {
        if (!super.isCheckTaskReady(checkTask) || !getIsOpen()) {
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
        String renderId = renderResult.getRenderId();
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
        if (isWeb) {
            MatchWindowTask.collectRegions(imageMatchSettings, renderResult.getImagePositionInActiveFrame(), regions, checkTask.getRegionSelectors());
            MatchWindowTask.collectRegions(imageMatchSettings, checkSettingsInternal);
        }
        return prepareForMatch(checkSettingsInternal, new ArrayList<Trigger>(), checkTask.getAppOutput(), checkSettingsInternal.getName(), false, imageMatchSettings, renderId, checkTask.getSource());
    }

    @Override
    protected String getBaseAgentId() {
        String moduleName = isWeb ? "selenium" : "appium";
        return String.format("eyes.%s.visualgrid.java/%s", moduleName, ClassVersionGetter.CURRENT_VERSION);
    }

    @Override
    protected void getAppOutputForOcr(BaseOcrRegion ocrRegion) {}

    @Override
    protected ScreenshotProvider getScreenshotProvider() {
        return null;
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
                null, browserInfo.getEmulationInfo(), browserInfo.getIosDeviceInfo(), browserInfo.getAndroidDeviceInfo());
        String platformType = isWeb ? "web" : "native";
        RenderRequest renderRequest = new RenderRequest(renderInfo, browserInfo.getPlatform(), platformType, isWeb ? browserInfo.getBrowserType() : null);
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