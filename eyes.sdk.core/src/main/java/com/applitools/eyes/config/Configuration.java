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
    private static final int DEFAULT_WAIT_BEFORE_SCREENSHOTS = 100;
    private static final int DEFAULT_MATCH_TIMEOUT = 2000; // Milliseconds;

    private String branchName = GeneralUtils.getEnvString("APPLITOOLS_BRANCH");

    private String parentBranchName = GeneralUtils.getEnvString("APPLITOOLS_PARENT_BRANCH");
    private String baselineBranchName = GeneralUtils.getEnvString("APPLITOOLS_BASELINE_BRANCH");
    private String agentId;
    private String environmentName;
    private Boolean saveDiffs;
    private SessionType sessionType;
    protected BatchInfo batch;
    protected String baselineEnvName;
    protected String appName;
    protected String testName;
    protected RectangleSize viewportSize;
    protected Boolean ignoreDisplacements;
    protected ImageMatchSettings defaultMatchSettings = new ImageMatchSettings();
    private Integer matchTimeout = DEFAULT_MATCH_TIMEOUT;
    private String hostApp;
    private String hostOS;
    private String deviceInfo;
    private String hostingAppInfo;
    private String osInfo;
    // Used for automatic save of a test run.
    private Boolean saveNewTests;
    private Boolean saveFailedTests;
    private Integer stitchOverlap;
    private Boolean isSendDom;
    private String apiKey = GeneralUtils.getEnvString("APPLITOOLS_API_KEY");
    private String serverUrl = "https://eyes.applitools.com/";
    private AbstractProxySettings proxy;
    private AutProxySettings autProxy;
    private FailureReports failureReports;
    private AccessibilitySettings accessibilitySettings;
    private Boolean enablePatterns;
    private Boolean useDom;
    private Integer abortIdleTestTimeout;

    private Boolean forceFullPageScreenshot;
    private Integer waitBeforeScreenshots;
    private StitchMode stitchMode;
    private Boolean hideScrollbars;
    private Boolean hideCaret;
    private Boolean isVisualGrid;
    private Boolean disableBrowserFetching;
    private Boolean useCookies;
    private Boolean captureStatusBar;
    @JsonIgnore
    private IDebugResourceWriter debugResourceWriter = new NullDebugResourceWriter(); // FIXME do we need that true

    //Rendering Configuration
    private Boolean isRenderingConfig;

    private List<RenderBrowserInfo> browsersInfo = new ArrayList<>();

    private Set<Feature> features = new HashSet<>();

    private List<VisualGridOption> visualGridOptions = new ArrayList<>();

    private Boolean isDefaultLayoutBreakpointsSet;
    private List<Integer> layoutBreakpoints =  new ArrayList<>();
    private Boolean saveDebugScreenshots;
    private String debugScreenshotsPath;
    private String debugScreenshotsPrefix;
    private Boolean isDisabled = false;
    private final List<PropertyData> properties = new ArrayList<>();
    private CutProvider cutProvider;
    private Integer rotation;
    private Double scaleRatio;
    private Boolean useCeilForViewportSize;
    private Integer waitBeforeCapture;
    private WebDriverProxySettings webdriverProxySettings;
    private ContentInset contentInset;

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
        this.autProxy = other.getAutProxy();
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
        if (other.getBrowsersInfo() != null) {
            this.browsersInfo = new ArrayList<>(other.getBrowsersInfo());
        }
        this.defaultMatchSettings = new ImageMatchSettings(other.getDefaultMatchSettings());
        this.isVisualGrid = other.isVisualGrid();
        this.features = new HashSet<>(other.getFeatures());
        this.visualGridOptions = other.getVisualGridOptions();
        this.disableBrowserFetching = other.isDisableBrowserFetching();
        this.useCookies = other.isUseCookies();
        this.debugResourceWriter = other.getDebugResourceWriter();
        this.isDefaultLayoutBreakpointsSet = other.isDefaultLayoutBreakpointsSet();
        this.layoutBreakpoints = other.getLayoutBreakpoints();
        this.captureStatusBar = other.isCaptureStatusBar();
        this.useCeilForViewportSize = other.getUseCeilForViewportSize();
        this.waitBeforeCapture = other.getWaitBeforeCapture();
        this.webdriverProxySettings = other.getWebDriverProxy();
        this.contentInset = other.getContentInset();
    }

    public Configuration() {
    }

    public Configuration(RectangleSize viewportSize) {
        this();
        browsersInfo.add(new RenderBrowserInfo(viewportSize.getWidth(), viewportSize.getHeight(), BrowserType.CHROME, null));
    }

    public Configuration(String testName) {
        this.testName = testName;
    }

    public Configuration(String appName, String testName, RectangleSize viewportSize) {
        this();
        if (viewportSize != null) {
            browsersInfo.add(new RenderBrowserInfo(viewportSize.getWidth(), viewportSize.getHeight(), BrowserType.CHROME, null));
        }
        this.testName = testName;
        this.viewportSize = viewportSize;
        this.appName = appName;

    }

    @Override
    public Boolean getSaveNewTests() {
        return saveNewTests;
    }

    @Override
    public Configuration setSaveNewTests(Boolean saveNewTests) {
        this.saveNewTests = saveNewTests;
        return this;
    }

    @Override
    public Boolean getSaveFailedTests() {
        return saveFailedTests;
    }

    @Override
    public Configuration setSaveFailedTests(Boolean saveFailedTests) {
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
    public Integer getMatchTimeout() {
        return matchTimeout;
    }

    @Override
    public Configuration setMatchTimeout(Integer matchTimeout) {
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
    public Integer getStitchOverlap() {
        return stitchOverlap;
    }

    @Override
    public Configuration setStitchOverlap(Integer stitchOverlap) {
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
    public Configuration setSendDom(Boolean sendDom) {
        isSendDom = sendDom;
        return this;
    }

    /**
     * @return Whether to ignore or the blinking caret or not when comparing images.
     */
    @Override
    public Boolean getIgnoreCaret() {
        Boolean ignoreCaret = null;
        if (getDefaultMatchSettings() != null) ignoreCaret = getDefaultMatchSettings().getIgnoreCaret();
        return ignoreCaret == null || ignoreCaret;
    }

    /**
     * Sets the ignore blinking caret value.
     * @param value The ignore value.
     */
    @Override
    public Configuration setIgnoreCaret(Boolean value) {
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
    public Boolean getIgnoreDisplacements() {
        return this.ignoreDisplacements;
    }

    @Override
    public Configuration setMatchLevel(MatchLevel matchLevel) {
        this.defaultMatchSettings.setMatchLevel(matchLevel);
        return this;
    }

    @Override
    public Configuration setIgnoreDisplacements(Boolean isIgnoreDisplacements) {
        this.defaultMatchSettings.setIgnoreDisplacements(isIgnoreDisplacements);
        this.ignoreDisplacements = isIgnoreDisplacements;
        return this;
    }

    @Override
    public AccessibilitySettings getAccessibilityValidation() {
        return accessibilitySettings;
        // TODO
        //return this.accessibilitySettings != null ? this.accessibilitySettings : getDefaultMatchSettings().getAccessibilitySettings();
    }

    @Override
    public Configuration setAccessibilityValidation(AccessibilitySettings accessibilitySettings) {
        if (accessibilitySettings == null) {
            return this;
        }

        // TODO do we need such validation here?
        if (accessibilitySettings.getLevel() == null || accessibilitySettings.getGuidelinesVersion() == null) {
            throw new IllegalArgumentException("AccessibilitySettings should have the following properties: ‘level,version’");
        }

        this.defaultMatchSettings.setAccessibilitySettings(accessibilitySettings);
        this.accessibilitySettings = accessibilitySettings;
        return this;
    }

    @Override
    public Configuration setUseDom(Boolean useDom) {
        this.defaultMatchSettings.setUseDom(useDom);
        this.useDom = useDom;
        return this;
    }

    @Override
    public Boolean getUseDom() {
        return useDom;
    }

    @Override
    public Configuration setEnablePatterns(Boolean enablePatterns) {
        this.defaultMatchSettings.setEnablePatterns(enablePatterns);
        this.enablePatterns = enablePatterns;
        return this;
    }

    @Override
    public Boolean getEnablePatterns() {
        return enablePatterns;
    }

    public Boolean getForceFullPageScreenshot() {
        return forceFullPageScreenshot;
    }

    public Integer getWaitBeforeScreenshots() {
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

    public Boolean getHideScrollbars() {
        return hideScrollbars;
    }

    public Configuration setHideScrollbars(boolean hideScrollbars) {
        this.hideScrollbars = hideScrollbars;
        return this;
    }

    public Boolean getHideCaret() {
        return hideCaret;
    }

    public Configuration setHideCaret(boolean hideCaret) {
        this.hideCaret = hideCaret;
        return this;
    }

    public Configuration addBrowsers(IRenderingBrowserInfo... browserInfos) {
        if (browserInfos == null) {
            return this;
        }
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

    public Configuration addBrowser(int width, int height, com.applitools.eyes.visualgrid.BrowserType browserType, String baselineEnvName) {
        BrowserType browserType1 = BrowserType.fromName(browserType.getName());
        return addBrowser(width, height, browserType1, baselineEnvName);
    }

    public Configuration addBrowser(int width, int height, com.applitools.eyes.visualgrid.BrowserType browserType) {
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

        return null;
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

    public Boolean isRenderingConfig() {
        return isRenderingConfig;
    }

    public Configuration setRenderingConfig(Boolean renderingConfig) {
        isRenderingConfig = renderingConfig;
        return this;
    }

    public Configuration setIsVisualGrid(Boolean isVisualGrid) {
        this.isVisualGrid = isVisualGrid;
        return this;
    }

    public Boolean isVisualGrid() {
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

    public Boolean isDisableBrowserFetching() {
        return disableBrowserFetching;
    }

    public Configuration setDisableBrowserFetching(Boolean disableBrowserFetching) {
        this.disableBrowserFetching = disableBrowserFetching;
        return this;
    }

    public Boolean isUseCookies() {
        return useCookies;
    }

    public Configuration setUseCookies(Boolean useCookies) {
        this.useCookies = useCookies;
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

    public Configuration setLayoutBreakpoints(Boolean shouldSet) {
        this.isDefaultLayoutBreakpointsSet = shouldSet;
        layoutBreakpoints.clear();
        return this;
    }

    public Boolean isDefaultLayoutBreakpointsSet() {
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

    public Boolean isCaptureStatusBar() {
        return captureStatusBar;
    }

    public void setCaptureStatusBar(Boolean captureStatusBar) {
        this.captureStatusBar = captureStatusBar;
    }

    public Configuration setSaveDebugScreenshots(Boolean saveDebugScreenshots) {
        this.saveDebugScreenshots = saveDebugScreenshots;
        return this;
    }

    public Boolean getSaveDebugScreenshots() {
        return saveDebugScreenshots;
    }

    public Configuration setDebugScreenshotsPath(String debugScreenshotsPath) {
        this.debugScreenshotsPath = debugScreenshotsPath;
        return this;
    }

    public String getDebugScreenshotsPath() {
        return debugScreenshotsPath;
    }

    public Configuration setDebugScreenshotsPrefix(String debugScreenshotsPrefix) {
        this.debugScreenshotsPrefix = debugScreenshotsPrefix;
        return this;
    }

    public String getDebugScreenshotsPrefix() {
        return debugScreenshotsPrefix;
    }

    public Configuration setIsDisabled(Boolean isDisabled) {
        this.isDisabled = isDisabled;
        return this;
    }

    public Boolean getIsDisabled() {
        return isDisabled;
    }

    public void addProperty(String name, String value) {
        PropertyData pd = new PropertyData(name, value);
        properties.add(pd);
    }

    public void clearProperties() {
        properties.clear();
    }

    public List<PropertyData> getProperties() {
        return properties;
    }

    public Configuration setCutProvider(CutProvider cutProvider) {
        this.cutProvider = cutProvider;
        return this;
    }

    public CutProvider getCutProvider() {
        return cutProvider;
    }

    public Configuration setRotation(Integer rotation) {
        this.rotation = rotation;
        return this;
    }

    public Integer getRotation() {
        return rotation;
    }

    public Configuration setScaleRatio(Double scaleRatio) {
        this.scaleRatio = scaleRatio;
        return this;
    }

    public Double getScaleRatio() {
        return scaleRatio;
    }

    public Configuration addMobileDevice(IosDeviceInfo iosDeviceInfo) {
        return addBrowser(iosDeviceInfo);
    }

    public Configuration addMobileDevices(IosDeviceInfo iosDeviceInfo, IosDeviceInfo... iosDeviceInfos) {
        addMobileDevice(iosDeviceInfo);
        if (iosDeviceInfos == null) {
            return this;
        }

        for (IosDeviceInfo info : iosDeviceInfos) {
            addMobileDevice(info);
        }

        return this;
    }

    public Configuration addMobileDevice(AndroidDeviceInfo androidDeviceInfo) {
        RenderBrowserInfo renderBrowserInfo = new RenderBrowserInfo(androidDeviceInfo);
        this.browsersInfo.add(renderBrowserInfo);
        return this;
    }

    public Configuration addMobileDevices(AndroidDeviceInfo androidDeviceInfo, AndroidDeviceInfo... androidDeviceInfos) {
        addMobileDevice(androidDeviceInfo);
        if (androidDeviceInfos == null) {
            return this;
        }

        for (AndroidDeviceInfo info : androidDeviceInfos) {
            addMobileDevice(info);
        }

        return this;
    }


    public Configuration setUseCeilForViewportSize(Boolean useCeilForViewportSize) {
        this.useCeilForViewportSize = useCeilForViewportSize;
        return this;
    }

    public Boolean getUseCeilForViewportSize() {
        return useCeilForViewportSize;
    }

    /**
     * @return wait before capture
     */
    public Integer getWaitBeforeCapture() {
        return waitBeforeCapture;
    }

    /**
     * @param milliSec
     *          time to wait before each screenshot
     */
    public void setWaitBeforeCapture(Integer milliSec) {
        this.waitBeforeCapture = milliSec;
    }

    public WebDriverProxySettings getWebDriverProxy() {
        return webdriverProxySettings;
    }

    public Configuration setWebDriverProxy(WebDriverProxySettings proxySettings) {
        this.webdriverProxySettings = proxySettings;
        return this;
    }

    public Configuration setAutProxy(AutProxySettings autProxy) {
        this.autProxy = autProxy;
        return this;
    }

    public AutProxySettings getAutProxy() {
        return autProxy;
    }

    public ContentInset getContentInset() {
        return contentInset;
    }

    public Configuration setContentInset(ContentInset contentInset) {
        this.contentInset = contentInset;
        return this;
    }
}
