package com.applitools.eyes.config;

import com.applitools.eyes.*;
import com.applitools.eyes.selenium.BrowserType;
import com.applitools.eyes.selenium.StitchMode;
import com.applitools.eyes.visualgrid.model.*;
import com.applitools.utils.ArgumentGuard;
import com.applitools.utils.GeneralUtils;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.net.URI;
import java.util.*;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Configuration implements IConfiguration {
    private static final int DEFAULT_MATCH_TIMEOUT = 2000; // Milliseconds;
    private static final int DEFAULT_WAIT_BEFORE_SCREENSHOTS = 100;

    private String branchName = GeneralUtils.getEnvString("APPLITOOLS_BRANCH");

    private String parentBranchName = GeneralUtils.getEnvString("APPLITOOLS_PARENT_BRANCH");
    private String baselineBranchName = GeneralUtils.getEnvString("APPLITOOLS_BASELINE_BRANCH");
    private String agentId;
    private String environmentName;
    private Boolean saveDiffs;
    private SessionType sessionType;
    protected BatchInfo batch = new BatchInfo(null);
    protected String baselineEnvName;
    protected String appName;
    protected String testName;
    protected RectangleSize viewportSize;
    protected boolean ignoreDisplacements = false;
    protected ImageMatchSettings defaultMatchSettings = new ImageMatchSettings();
    private int matchTimeout = DEFAULT_MATCH_TIMEOUT;
    private String hostApp;
    private String hostOS;
    private String deviceInfo;
    private String hostingAppInfo;
    private String osInfo;
    // Used for automatic save of a test run.
    private boolean saveNewTests, saveFailedTests;
    private int stitchOverlap = 10;
    private Boolean isSendDom = true;
    private String apiKey = null;
    private String serverUrl = null;
    private AbstractProxySettings proxy = null;
    private FailureReports failureReports = FailureReports.ON_CLOSE;
    private AccessibilitySettings accessibilitySettings = null;
    private boolean enablePatterns;
    private boolean useDom;
    private Integer abortIdleTestTimeout;

    private Boolean forceFullPageScreenshot;
    private int waitBeforeScreenshots = DEFAULT_WAIT_BEFORE_SCREENSHOTS;
    private StitchMode stitchMode = StitchMode.SCROLL;
    private boolean hideScrollbars = true;
    private boolean hideCaret = true;
    private boolean isVisualGrid = false;
    private boolean disableBrowserFetching = false;
    @JsonIgnore
    private IDebugResourceWriter debugResourceWriter = new NullDebugResourceWriter();

    //Rendering Configuration
    private Boolean isRenderingConfig = false;

    private List<RenderBrowserInfo> browsersInfo = new ArrayList<>();

    private Set<Feature> features = new HashSet<>();

    private List<VisualGridOption> visualGridOptions = new ArrayList<>();

    private boolean isDefaultLayoutBreakpointsSet = false;
    private List<Integer> layoutBreakpoints = new ArrayList<>();

    public Configuration(Configuration other) {
        this.branchName = other.getBranchName();
        this.parentBranchName = other.getParentBranchName();
        this.baselineBranchName = other.getBaselineBranchName();
        this.agentId = other.getAgentId();
        this.environmentName = other.getEnvironmentName();
        this.saveDiffs = other.getSaveDiffs();
        this.sessionType = other.getSessionType();
        this.batch = other.getBatch();
        this.baselineEnvName = other.getBaselineEnvName();
        this.appName = other.getAppName();
        this.testName = other.getTestName();
        this.viewportSize = other.getViewportSize();
        this.defaultMatchSettings = new ImageMatchSettings(other.getDefaultMatchSettings());
        this.matchTimeout = other.getMatchTimeout();
        this.hostApp = other.getHostApp();
        this.hostOS = other.getHostOS();
        this.deviceInfo = other.getDeviceInfo();
        this.hostingAppInfo = other.getHostingAppInfo();
        this.osInfo = other.getOsInfo();
        this.saveNewTests = other.getSaveNewTests();
        this.saveFailedTests = other.getSaveFailedTests();
        this.stitchOverlap = other.getStitchOverlap();
        this.isSendDom = other.isSendDom();
        this.apiKey = other.getApiKey();
        this.useDom = other.getUseDom();
        this.abortIdleTestTimeout = other.getAbortIdleTestTimeout();
        this.enablePatterns = other.getEnablePatterns();
        URI serverUrl = other.getServerUrl();
        if (serverUrl != null) {
            this.serverUrl = serverUrl.toString();
        }
        this.failureReports = other.getFailureReports();
        this.proxy = other.getProxy();
        if (other.getMatchLevel() != null) {
            this.defaultMatchSettings.setMatchLevel(other.getMatchLevel());
        }
        this.ignoreDisplacements = other.getIgnoreDisplacements();
        this.accessibilitySettings = other.getAccessibilityValidation();
        this.forceFullPageScreenshot = other.getForceFullPageScreenshot();
        this.waitBeforeScreenshots = other.getWaitBeforeScreenshots();
        this.stitchMode = other.getStitchMode();
        this.hideScrollbars = other.getHideScrollbars();
        this.hideCaret = other.getHideCaret();
        this.isRenderingConfig = other.isRenderingConfig();
        this.browsersInfo.addAll(other.getBrowsersInfo());
        this.defaultMatchSettings = new ImageMatchSettings(other.getDefaultMatchSettings());
        this.isVisualGrid = other.isVisualGrid();
        this.features = new HashSet<>(other.getFeatures());
        this.visualGridOptions = other.getVisualGridOptions();
        this.disableBrowserFetching = other.isDisableBrowserFetching();
        this.debugResourceWriter = other.getDebugResourceWriter();
        this.isDefaultLayoutBreakpointsSet = other.isDefaultLayoutBreakpointsSet();
        this.layoutBreakpoints = other.getLayoutBreakpoints();
    }

    public Configuration() {
        defaultMatchSettings.setIgnoreCaret(true);
        agentId = null;// New tests are automatically saved by default.
        saveNewTests = true;
        saveFailedTests = false;

    }

    public Configuration(RectangleSize viewportSize) {
        this();
        ArrayList<RenderBrowserInfo> browsersInfo = new ArrayList<>();
        browsersInfo.add(new RenderBrowserInfo(viewportSize.getWidth(), viewportSize.getHeight(), BrowserType.CHROME, null));
        this.browsersInfo = browsersInfo;
    }

    public Configuration(String testName) {
        this.testName = testName;
    }

    public Configuration(String appName, String testName, RectangleSize viewportSize) {
        this();
        ArrayList<RenderBrowserInfo> browsersInfo = new ArrayList<>();
        if (viewportSize != null) {
            browsersInfo.add(new RenderBrowserInfo(viewportSize.getWidth(), viewportSize.getHeight(), BrowserType.CHROME, null));
        }
        this.browsersInfo = browsersInfo;
        this.testName = testName;
        this.viewportSize = viewportSize;
        this.setAppName(appName);
    }

    @Override
    public boolean getSaveNewTests() {
        return saveNewTests;
    }

    @Override
    public Configuration setSaveNewTests(boolean saveNewTests) {
        this.saveNewTests = saveNewTests;
        return this;
    }

    @Override
    public boolean getSaveFailedTests() {
        return saveFailedTests;
    }

    @Override
    public Configuration setSaveFailedTests(boolean saveFailedTests) {
        this.saveFailedTests = saveFailedTests;
        return this;
    }

    @Override
    public ImageMatchSettings getDefaultMatchSettings() {
        return defaultMatchSettings;
    }

    @Override
    public Configuration setDefaultMatchSettings(ImageMatchSettings defaultMatchSettings) {
        this.defaultMatchSettings = defaultMatchSettings;
        return this;
    }

    @Override
    public int getMatchTimeout() {
        return matchTimeout;
    }

    @Override
    public Configuration setMatchTimeout(int matchTimeout) {
        this.matchTimeout = matchTimeout;
        return this;
    }

    @Override
    public String getHostApp() {
        return hostApp;
    }

    @Override
    public Configuration setHostApp(String hostApp) {
        this.hostApp = hostApp;
        return this;
    }

    @Override
    public String getHostOS() {
        return hostOS;
    }

    @Override
    public Configuration setHostOS(String hostOS) {
        this.hostOS = hostOS;
        return this;
    }

    @Override
    public int getStitchOverlap() {
        return stitchOverlap;
    }

    @Override
    public Configuration setStitchOverlap(int stitchOverlap) {
        this.stitchOverlap = stitchOverlap;
        return this;
    }

    @Override
    public Configuration setBatch(BatchInfo batch) {
        this.batch = batch;
        return this;
    }

    @Override
    public BatchInfo getBatch() {
        return batch;
    }

    @Override
    public Configuration setBranchName(String branchName) {
        this.branchName = branchName;
        return this;
    }

    @Override
    public String getBranchName() {
        return branchName;
    }

    @Override
    public String getAgentId() {
        return agentId;
    }

    @Override
    public Configuration setAgentId(String agentId) {
        this.agentId = agentId;
        return this;
    }

    @Override
    public String getParentBranchName() {
        return parentBranchName;
    }

    @Override
    public Configuration setParentBranchName(String parentBranchName) {
        this.parentBranchName = parentBranchName;
        return this;
    }

    @Override
    public String getBaselineBranchName() {
        return baselineBranchName;
    }

    @Override
    public Configuration setBaselineBranchName(String baselineBranchName) {
        this.baselineBranchName = baselineBranchName;
        return this;
    }

    @Override
    public String getBaselineEnvName() {
        return baselineEnvName;
    }

    @Override
    public Configuration setBaselineEnvName(String baselineEnvName) {
        this.baselineEnvName = baselineEnvName;
        return this;
    }

    @Override
    public String getEnvironmentName() {
        return environmentName;
    }

    @Override
    public Configuration setEnvironmentName(String environmentName) {
        this.environmentName = environmentName;
        return this;
    }

    @Override
    public Boolean getSaveDiffs() {
        return saveDiffs;
    }

    @Override
    public Configuration setSaveDiffs(Boolean saveDiffs) {
        this.saveDiffs = saveDiffs;
        return this;
    }

    @Override
    public String getAppName() {
        return appName;
    }

    @Override
    public Configuration setAppName(String appName) {
        this.appName = appName;
        return this;
    }

    @Override
    public String getTestName() {
        return testName;
    }

    @Override
    public Configuration setTestName(String testName) {
        this.testName = testName;
        return this;
    }

    @Override
    public RectangleSize getViewportSize() {
        if (isRenderingConfig) {
            RenderBrowserInfo renderBrowserInfo = this.browsersInfo.get(0);
            return new RectangleSize(renderBrowserInfo.getWidth(), renderBrowserInfo.getHeight());
        }
        return viewportSize;
    }

    @Override
    public Configuration setViewportSize(RectangleSize viewportSize) {
        this.viewportSize = viewportSize;
        return this;
    }

    @Override
    public SessionType getSessionType() {
        return sessionType;
    }

    @Override
    public Configuration setSessionType(SessionType sessionType) {
        this.sessionType = sessionType;
        return this;
    }

    /**
     * @deprecated
     */
    @Override
    public Configuration setFailureReports(FailureReports failureReports) {
        this.failureReports = failureReports;
        return this;
    }

    /**
     * @deprecated
     */
    @Override
    public FailureReports getFailureReports() {
        return failureReports;
    }

    public String toString() {
        return super.toString() +
                "\n\tbatch = " + batch +
                "\n\tbranchName = " + branchName +
                "\n\tparentBranchName = " + parentBranchName +
                "\n\tagentId = " + agentId +
                "\n\tbaselineEnvName = " + baselineEnvName +
                "\n\tenvironmentName = " + environmentName +
                "\n\tsaveDiffs = " + saveDiffs +
                "\n\tappName = " + appName +
                "\n\ttestName = " + testName +
                "\n\tviewportSize = " + viewportSize +
                "\n\tsessionType = " + sessionType +
                "\n\tforceFullPageScreenshot = " + forceFullPageScreenshot +
                "\n\twaitBeforeScreenshots = " + waitBeforeScreenshots +
                "\n\tstitchMode = " + stitchMode +
                "\n\thideScrollbars = " + hideScrollbars +
                "\n\thideCaret = " + hideCaret;
    }

    @Override
    public Boolean isSendDom() {
        return isSendDom;
    }

    @Override
    public Configuration setSendDom(boolean sendDom) {
        isSendDom = sendDom;
        return this;
    }

    /**
     * @return Whether to ignore or the blinking caret or not when comparing images.
     */
    @Override
    public boolean getIgnoreCaret() {
        Boolean ignoreCaret = getDefaultMatchSettings().getIgnoreCaret();
        return ignoreCaret == null || ignoreCaret;
    }

    /**
     * Sets the ignore blinking caret value.
     * @param value The ignore value.
     */
    @Override
    public Configuration setIgnoreCaret(boolean value) {
        defaultMatchSettings.setIgnoreCaret(value);
        return this;
    }

    @Override
    public String getApiKey() {
        return apiKey;
    }

    @Override
    public Configuration setApiKey(String apiKey) {
        this.apiKey = apiKey;
        return this;
    }

    @Override
    public URI getServerUrl() {
        if (this.serverUrl != null) {
            return URI.create(serverUrl);
        }
        return null;
    }

    @Override
    public Configuration setServerUrl(String serverUrl) {
        this.serverUrl = serverUrl;
        return this;
    }

    @Override
    public AbstractProxySettings getProxy() {
        return proxy;
    }

    @Override
    public Configuration setProxy(AbstractProxySettings proxy) {
        this.proxy = proxy;
        return this;
    }

    @Override
    public MatchLevel getMatchLevel() {
        return this.defaultMatchSettings.getMatchLevel();
    }

    @Override
    public boolean getIgnoreDisplacements() {
        return this.ignoreDisplacements;
    }

    @Override
    public Configuration setMatchLevel(MatchLevel matchLevel) {
        this.defaultMatchSettings.setMatchLevel(matchLevel);
        return this;
    }

    @Override
    public Configuration setIgnoreDisplacements(boolean isIgnoreDisplacements) {
        this.defaultMatchSettings.setIgnoreDisplacements(isIgnoreDisplacements);
        this.ignoreDisplacements = isIgnoreDisplacements;
        return this;
    }

    @Override
    public AccessibilitySettings getAccessibilityValidation() {
        return this.accessibilitySettings != null ? this.accessibilitySettings : getDefaultMatchSettings().getAccessibilitySettings();
    }

    @Override
    public Configuration setAccessibilityValidation(AccessibilitySettings accessibilitySettings) {
        if (accessibilitySettings == null) {
            this.defaultMatchSettings.setAccessibilitySettings(null);
            this.accessibilitySettings = null;
            return this;
        }

        if (accessibilitySettings.getLevel() == null || accessibilitySettings.getGuidelinesVersion() == null) {
            throw new IllegalArgumentException("AccessibilitySettings should have the following properties: ‘level,version’");
        }

        this.defaultMatchSettings.setAccessibilitySettings(accessibilitySettings);
        this.accessibilitySettings = accessibilitySettings;
        return this;
    }

    @Override
    public Configuration setUseDom(boolean useDom) {
        this.defaultMatchSettings.setUseDom(useDom);
        this.useDom = useDom;
        return this;
    }

    @Override
    public boolean getUseDom() {
        return useDom;
    }

    @Override
    public Configuration setEnablePatterns(boolean enablePatterns) {
        this.defaultMatchSettings.setEnablePatterns(enablePatterns);
        this.enablePatterns = enablePatterns;
        return this;
    }

    @Override
    public boolean getEnablePatterns() {
        return enablePatterns;
    }

    public Boolean getForceFullPageScreenshot() {
        return forceFullPageScreenshot;
    }

    public int getWaitBeforeScreenshots() {
        return waitBeforeScreenshots;
    }

    public Configuration setWaitBeforeScreenshots(int waitBeforeScreenshots) {
        if (waitBeforeScreenshots <= 0) {
            this.waitBeforeScreenshots = DEFAULT_WAIT_BEFORE_SCREENSHOTS;
        } else {
            this.waitBeforeScreenshots = waitBeforeScreenshots;
        }
        return this;
    }

    public StitchMode getStitchMode() {
        return stitchMode;
    }

    public Configuration setStitchMode(StitchMode stitchMode) {
        this.stitchMode = stitchMode;
        return this;
    }

    public boolean getHideScrollbars() {
        return hideScrollbars;
    }

    public Configuration setHideScrollbars(boolean hideScrollbars) {
        this.hideScrollbars = hideScrollbars;
        return this;
    }

    public boolean getHideCaret() {
        return hideCaret;
    }

    public Configuration setHideCaret(boolean hideCaret) {
        this.hideCaret = hideCaret;
        return this;
    }

    public Configuration addBrowsers(IRenderingBrowserInfo... browserInfos) {
        for (IRenderingBrowserInfo browserInfo : browserInfos) {
            addBrowser(browserInfo);
        }
        return this;
    }

    private void addBrowser(IRenderingBrowserInfo browserInfo) {
        if (browserInfo instanceof DesktopBrowserInfo) {
            addBrowser((DesktopBrowserInfo) browserInfo);
        } else if(browserInfo instanceof ChromeEmulationInfo) {
            addBrowser((ChromeEmulationInfo) browserInfo);
        } else if(browserInfo instanceof IosDeviceInfo) {
            addBrowser((IosDeviceInfo) browserInfo);
        }
    }

    public Configuration addBrowser(RenderBrowserInfo renderBrowserInfo) {
        this.browsersInfo.add(renderBrowserInfo);
        return this;
    }

    public Configuration addBrowser(DesktopBrowserInfo desktopBrowserInfo) {
        this.browsersInfo.add(desktopBrowserInfo.getRenderBrowserInfo());
        return this;
    }

    public Configuration addBrowser(ChromeEmulationInfo chromeEmulationInfo) {
        RenderBrowserInfo renderBrowserInfo = new RenderBrowserInfo(chromeEmulationInfo);
        this.browsersInfo.add(renderBrowserInfo);
        return this;
    }

    public Configuration addBrowser(IosDeviceInfo iosDeviceInfo) {
        RenderBrowserInfo renderBrowserInfo = new RenderBrowserInfo(iosDeviceInfo);
        this.browsersInfo.add(renderBrowserInfo);
        return this;
    }

    public Configuration addBrowser(int width, int height, BrowserType browserType, String baselineEnvName) {
        RenderBrowserInfo browserInfo = new RenderBrowserInfo(width, height, browserType, baselineEnvName);
        addBrowser(browserInfo);
        return this;
    }

    public Configuration addBrowser(int width, int height, BrowserType browserType) {
        return addBrowser(width, height, browserType, baselineEnvName);
    }

    public Configuration addDeviceEmulation(DeviceName deviceName, ScreenOrientation orientation) {
        EmulationBaseInfo emulationInfo = new ChromeEmulationInfo(deviceName, orientation);
        RenderBrowserInfo browserInfo = new RenderBrowserInfo(emulationInfo, baselineEnvName);
        this.browsersInfo.add(browserInfo);
        return this;
    }

    public Configuration addDeviceEmulation(DeviceName deviceName) {
        EmulationBaseInfo emulationInfo = new ChromeEmulationInfo(deviceName, ScreenOrientation.PORTRAIT);
        RenderBrowserInfo browserInfo = new RenderBrowserInfo(emulationInfo, baselineEnvName);
        this.browsersInfo.add(browserInfo);
        return this;
    }

    public Configuration addDeviceEmulation(DeviceName deviceName, String baselineEnvName) {
        EmulationBaseInfo emulationInfo = new ChromeEmulationInfo(deviceName, ScreenOrientation.PORTRAIT);
        RenderBrowserInfo browserInfo = new RenderBrowserInfo(emulationInfo, baselineEnvName);
        this.browsersInfo.add(browserInfo);
        return this;
    }

    public Configuration addDeviceEmulation(DeviceName deviceName, ScreenOrientation orientation, String baselineEnvName) {
        EmulationBaseInfo emulationInfo = new ChromeEmulationInfo(deviceName, orientation);
        RenderBrowserInfo browserInfo = new RenderBrowserInfo(emulationInfo, baselineEnvName);
        this.browsersInfo.add(browserInfo);
        return this;
    }

    public List<RenderBrowserInfo> getBrowsersInfo() {
        if (browsersInfo != null && !browsersInfo.isEmpty()) {
            return browsersInfo;
        }

        if (this.viewportSize != null) {
            RenderBrowserInfo renderBrowserInfo = new RenderBrowserInfo(this.viewportSize.getWidth(), this.viewportSize.getHeight(), BrowserType.CHROME, baselineEnvName);
            return Collections.singletonList(renderBrowserInfo);
        }
        return browsersInfo;
    }

    public Configuration setBrowsersInfo(List<RenderBrowserInfo> browsersInfo) {
        this.browsersInfo = browsersInfo;
        return this;
    }

    public Boolean isForceFullPageScreenshot() {
        return forceFullPageScreenshot;
    }

    public Configuration setForceFullPageScreenshot(boolean forceFullPageScreenshot) {
        this.forceFullPageScreenshot = forceFullPageScreenshot;
        return this;
    }

    public boolean isRenderingConfig() {
        return isRenderingConfig;
    }

    public Configuration setRenderingConfig(boolean renderingConfig) {
        isRenderingConfig = renderingConfig;
        return this;
    }

    public Configuration setIsVisualGrid(boolean isVisualGrid) {
        this.isVisualGrid = isVisualGrid;
        return this;
    }

    public boolean isVisualGrid() {
        return isVisualGrid;
    }

    /**
     * Sets features to for the Eyes test.
     * Overrides existing features.
     */
    public Configuration setFeatures(Feature feature, Feature... features) {
        this.features.clear();
        this.features.add(feature);
        this.features.addAll(Arrays.asList(features));
        this.features.remove(null);
        return this;
    }

    public List<Feature> getFeatures() {
        return new ArrayList<>(features);
    }

    public boolean isFeatureActivated(Feature feature) {
        return features.contains(feature);
    }

    public Configuration setVisualGridOptions(VisualGridOption option, VisualGridOption... options) {
        this.visualGridOptions.clear();
        this.visualGridOptions.add(option);
        this.visualGridOptions.addAll(Arrays.asList(options));
        this.visualGridOptions.remove(null);
        return this;
    }

    public List<VisualGridOption> getVisualGridOptions() {
        return visualGridOptions;
    }

    public String getDeviceInfo() {
        return deviceInfo;
    }

    public Configuration setDeviceInfo(String deviceInfo) {
        this.deviceInfo = deviceInfo;
        return this;
    }

    public String getHostingAppInfo() {
        return hostingAppInfo;
    }

    public Configuration setHostingAppInfo(String hostingAppInfo) {
        this.hostingAppInfo = hostingAppInfo;
        return this;
    }

    public String getOsInfo() {
        return osInfo;
    }

    public Configuration setOsInfo(String osInfo) {
        this.osInfo = osInfo;
        return this;
    }

    public boolean isDisableBrowserFetching() {
        return disableBrowserFetching;
    }

    public Configuration setDisableBrowserFetching(boolean disableBrowserFetching) {
        this.disableBrowserFetching = disableBrowserFetching;
        return this;
    }

    public IDebugResourceWriter getDebugResourceWriter() {
        return debugResourceWriter;
    }

    public Configuration setDebugResourceWriter(IDebugResourceWriter debugResourceWriter) {
        this.debugResourceWriter = debugResourceWriter;
        return this;
    }

    public Integer getAbortIdleTestTimeout() {
        return abortIdleTestTimeout;
    }

    public Configuration setAbortIdleTestTimeout(Integer abortIdleTestTimeout) {
        this.abortIdleTestTimeout = abortIdleTestTimeout;
        return this;
    }

    public Configuration setLayoutBreakpoints(boolean shouldSet) {
        this.isDefaultLayoutBreakpointsSet = shouldSet;
        layoutBreakpoints.clear();
        return this;
    }

    public boolean isDefaultLayoutBreakpointsSet() {
        return isDefaultLayoutBreakpointsSet;
    }

    public Configuration setLayoutBreakpoints(int... breakpoints) {
        isDefaultLayoutBreakpointsSet = false;
        layoutBreakpoints.clear();
        if (breakpoints == null || breakpoints.length == 0) {
            return this;
        }

        for (int breakpoint : breakpoints) {
            ArgumentGuard.greaterThanZero(breakpoint, "breakpoint");
            layoutBreakpoints.add(breakpoint);
        }

        Collections.sort(layoutBreakpoints);
        return this;
    }

    public List<Integer> getLayoutBreakpoints() {
        return layoutBreakpoints;
    }
}
