package com.applitools.eyes.config;

import com.applitools.eyes.*;
import com.applitools.utils.GeneralUtils;

import java.net.URI;

public class Configuration implements IConfigurationSetter, IConfigurationGetter {
    private static final int DEFAULT_MATCH_TIMEOUT = 2000; // Milliseconds;

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
    // Used for automatic save of a test run.
    private boolean saveNewTests, saveFailedTests;
    private int stitchOverlap = 10;
    private Boolean isSendDom = true;
    private String apiKey = null;
    private String serverUrl = null;
    private AbstractProxySettings proxy = null;
    private FailureReports failureReports = FailureReports.ON_CLOSE;
    private AccessibilityLevel accessibilityValidation = null;
    private boolean enablePatterns;
    private boolean useDom;

    public Configuration(IConfigurationGetter other) {
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
        this.saveNewTests = other.getSaveNewTests();
        this.saveFailedTests = other.getSaveFailedTests();
        this.stitchOverlap = other.getStitchOverlap();
        this.isSendDom = other.isSendDom();
        this.apiKey = other.getApiKey();
        this.useDom = other.getUseDom();
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
        this.accessibilityValidation = other.getAccessibilityValidation();
    }

    public Configuration() {
        defaultMatchSettings.setIgnoreCaret(true);
        agentId = null;// New tests are automatically saved by default.
        saveNewTests = true;
        saveFailedTests = false;

    }

    @Override
    public boolean getSaveNewTests() {
        return saveNewTests;
    }

    @Override
    public IConfigurationSetter setSaveNewTests(boolean saveNewTests) {
        this.saveNewTests = saveNewTests;
        return this;
    }

    @Override
    public boolean getSaveFailedTests() {
        return saveFailedTests;
    }

    @Override
    public IConfigurationSetter setSaveFailedTests(boolean saveFailedTests) {
        this.saveFailedTests = saveFailedTests;
        return this;
    }


    @Override
    public ImageMatchSettings getDefaultMatchSettings() {
        return defaultMatchSettings;
    }

    @Override
    public IConfigurationSetter setDefaultMatchSettings(ImageMatchSettings defaultMatchSettings) {
        this.defaultMatchSettings = defaultMatchSettings;
        return this;
    }

    @Override
    public int getMatchTimeout() {
        return matchTimeout;
    }

    @Override
    public IConfigurationSetter setMatchTimeout(int matchTimeout) {
        this.matchTimeout = matchTimeout;
        return this;
    }

    @Override
    public String getHostApp() {
        return hostApp;
    }

    @Override
    public IConfigurationSetter setHostApp(String hostApp) {
        this.hostApp = hostApp;
        return this;
    }

    @Override
    public String getHostOS() {
        return hostOS;
    }

    @Override
    public IConfigurationSetter setHostOS(String hostOS) {
        this.hostOS = hostOS;
        return this;
    }

    @Override
    public int getStitchOverlap() {
        return stitchOverlap;
    }


    @Override
    public IConfigurationSetter setStitchOverlap(int stitchOverlap) {
        this.stitchOverlap = stitchOverlap;
        return this;
    }

    @Override
    public IConfigurationSetter setBatch(BatchInfo batch) {
        this.batch = batch;
        return this;
    }

    @Override
    public BatchInfo getBatch() {
        return batch;
    }

    @Override
    public IConfigurationSetter setBranchName(String branchName) {
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
    public IConfigurationSetter setAgentId(String agentId) {
        this.agentId = agentId;
        return this;
    }

    @Override
    public String getParentBranchName() {
        return parentBranchName;
    }

    @Override
    public IConfigurationSetter setParentBranchName(String parentBranchName) {
        this.parentBranchName = parentBranchName;
        return this;
    }

    @Override
    public String getBaselineBranchName() {
        return baselineBranchName;
    }

    @Override
    public IConfigurationSetter setBaselineBranchName(String baselineBranchName) {
        this.baselineBranchName = baselineBranchName;
        return this;
    }

    @Override
    public String getBaselineEnvName() {
        return baselineEnvName;
    }

    @Override
    public IConfigurationSetter setBaselineEnvName(String baselineEnvName) {
        this.baselineEnvName = baselineEnvName;
        return this;
    }

    @Override
    public String getEnvironmentName() {
        return environmentName;
    }

    @Override
    public IConfigurationSetter setEnvironmentName(String environmentName) {
        this.environmentName = environmentName;
        return this;
    }

    @Override
    public Boolean getSaveDiffs() {
        return saveDiffs;
    }

    @Override
    public IConfigurationSetter setSaveDiffs(Boolean saveDiffs) {
        this.saveDiffs = saveDiffs;
        return this;
    }

    @Override
    public String getAppName() {
        return appName;
    }

    @Override
    public IConfigurationSetter setAppName(String appName) {
        this.appName = appName;
        return this;
    }

    @Override
    public String getTestName() {
        return testName;
    }

    @Override
    public IConfigurationSetter setTestName(String testName) {
        this.testName = testName;
        return this;
    }

    @Override
    public RectangleSize getViewportSize() {
        return viewportSize;
    }

    @Override
    public IConfigurationSetter setViewportSize(RectangleSize viewportSize) {
        this.viewportSize = viewportSize;
        return this;
    }

    @Override
    public SessionType getSessionType() {
        return sessionType;
    }

    @Override
    public IConfigurationSetter setSessionType(SessionType sessionType) {
        this.sessionType = sessionType;
        return this;
    }

    public Configuration cloneConfig() {
        return new Configuration(this);
    }

    /**
     * @param failureReports The failure reports setting.
     * @see FailureReports
     */
    @Override
    public IConfigurationSetter setFailureReports(FailureReports failureReports) {
        this.failureReports = failureReports;
        return this;
    }

    /**
     * @return the failure reports setting.
     */
    @Override
    public FailureReports getFailureReports() {
        return failureReports;
    }

    @Override
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
                "\n\tsessionType = " + sessionType;
    }

    @Override
    public Boolean isSendDom() {
        return isSendDom;
    }

    @Override
    public IConfigurationSetter setSendDom(boolean sendDom) {
        isSendDom = sendDom;
        return this;
    }

    /**
     * @return Whether to ignore or the blinking caret or not when comparing images.
     */
    @Override
    public boolean getIgnoreCaret() {
        Boolean ignoreCaret = getDefaultMatchSettings().getIgnoreCaret();
        return ignoreCaret == null ? true : ignoreCaret;
    }

    /**
     * Sets the ignore blinking caret value.
     * @param value The ignore value.
     */
    @Override
    public IConfigurationSetter setIgnoreCaret(boolean value) {
        defaultMatchSettings.setIgnoreCaret(value);
        return this;
    }


    @Override
    public String getApiKey() {
        return apiKey;
    }

    @Override
    public IConfigurationSetter setApiKey(String apiKey) {
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
    public IConfigurationSetter setServerUrl(String serverUrl) {
        this.serverUrl = serverUrl;
        return this;
    }

    @Override
    public AbstractProxySettings getProxy() {
        return proxy;
    }

    @Override
    public IConfigurationSetter setProxy(AbstractProxySettings proxy) {
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
    public IConfigurationSetter setMatchLevel(MatchLevel matchLevel) {
        this.defaultMatchSettings.setMatchLevel(matchLevel);
        return this;
    }

    @Override
    public IConfigurationSetter setIgnoreDisplacements(boolean isIgnoreDisplacements) {
        this.ignoreDisplacements = isIgnoreDisplacements;
        return this;
    }

    @Override
    public AccessibilityLevel getAccessibilityValidation() {
        return this.accessibilityValidation != null ? this.accessibilityValidation : getDefaultMatchSettings().getAccessibilityLevel();
    }

    @Override
    public IConfigurationSetter setAccessibilityValidation(AccessibilityLevel accessibilityValidation) {
        this.accessibilityValidation = accessibilityValidation;
        return this;
    }

    @Override
    public IConfigurationSetter setUseDom(boolean useDom) {
        this.useDom = useDom;
        return this;
    }

    @Override
    public boolean getUseDom() {
        return useDom;
    }

    @Override
    public IConfigurationSetter setEnablePatterns(boolean enablePatterns) {
        this.enablePatterns = enablePatterns;
        return this;
    }

    @Override
    public boolean getEnablePatterns() {
        return enablePatterns;
    }

}
