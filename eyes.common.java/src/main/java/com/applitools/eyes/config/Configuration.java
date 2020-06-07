package com.applitools.eyes.config;

import com.applitools.eyes.*;
import com.applitools.utils.GeneralUtils;

import java.net.URI;

public class Configuration {
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
    private AccessibilitySettings accessibilitySettings = null;
    private boolean enablePatterns;
    private boolean useDom;

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
        this.accessibilitySettings = other.getAccessibilityValidation();
    }

    public Configuration() {
        defaultMatchSettings.setIgnoreCaret(true);
        agentId = null;// New tests are automatically saved by default.
        saveNewTests = true;
        saveFailedTests = false;

    }

    public boolean getSaveNewTests() {
        return saveNewTests;
    }

    public Configuration setSaveNewTests(boolean saveNewTests) {
        this.saveNewTests = saveNewTests;
        return this;
    }

    public boolean getSaveFailedTests() {
        return saveFailedTests;
    }

    public Configuration setSaveFailedTests(boolean saveFailedTests) {
        this.saveFailedTests = saveFailedTests;
        return this;
    }

    public ImageMatchSettings getDefaultMatchSettings() {
        return defaultMatchSettings;
    }

    public Configuration setDefaultMatchSettings(ImageMatchSettings defaultMatchSettings) {
        this.defaultMatchSettings = defaultMatchSettings;
        return this;
    }

    public int getMatchTimeout() {
        return matchTimeout;
    }

    public Configuration setMatchTimeout(int matchTimeout) {
        this.matchTimeout = matchTimeout;
        return this;
    }

    public String getHostApp() {
        return hostApp;
    }

    public Configuration setHostApp(String hostApp) {
        this.hostApp = hostApp;
        return this;
    }

    public String getHostOS() {
        return hostOS;
    }

    public Configuration setHostOS(String hostOS) {
        this.hostOS = hostOS;
        return this;
    }

    public int getStitchOverlap() {
        return stitchOverlap;
    }

    public Configuration setStitchOverlap(int stitchOverlap) {
        this.stitchOverlap = stitchOverlap;
        return this;
    }

    public Configuration setBatch(BatchInfo batch) {
        this.batch = batch;
        return this;
    }

    public BatchInfo getBatch() {
        return batch;
    }

    public Configuration setBranchName(String branchName) {
        this.branchName = branchName;
        return this;
    }

    public String getBranchName() {
        return branchName;
    }

    public String getAgentId() {
        return agentId;
    }

    public Configuration setAgentId(String agentId) {
        this.agentId = agentId;
        return this;
    }

    public String getParentBranchName() {
        return parentBranchName;
    }

    public Configuration setParentBranchName(String parentBranchName) {
        this.parentBranchName = parentBranchName;
        return this;
    }

    public String getBaselineBranchName() {
        return baselineBranchName;
    }

    public Configuration setBaselineBranchName(String baselineBranchName) {
        this.baselineBranchName = baselineBranchName;
        return this;
    }

    public String getBaselineEnvName() {
        return baselineEnvName;
    }

    public Configuration setBaselineEnvName(String baselineEnvName) {
        this.baselineEnvName = baselineEnvName;
        return this;
    }

    public String getEnvironmentName() {
        return environmentName;
    }

    public Configuration setEnvironmentName(String environmentName) {
        this.environmentName = environmentName;
        return this;
    }

    public Boolean getSaveDiffs() {
        return saveDiffs;
    }

    public Configuration setSaveDiffs(Boolean saveDiffs) {
        this.saveDiffs = saveDiffs;
        return this;
    }

    public String getAppName() {
        return appName;
    }

    public Configuration setAppName(String appName) {
        this.appName = appName;
        return this;
    }

    public String getTestName() {
        return testName;
    }

    public Configuration setTestName(String testName) {
        this.testName = testName;
        return this;
    }

    public RectangleSize getViewportSize() {
        return viewportSize;
    }

    public Configuration setViewportSize(RectangleSize viewportSize) {
        this.viewportSize = viewportSize;
        return this;
    }

    public SessionType getSessionType() {
        return sessionType;
    }

    public Configuration setSessionType(SessionType sessionType) {
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

    public Configuration setFailureReports(FailureReports failureReports) {
        this.failureReports = failureReports;
        return this;
    }

    /**
     * @return the failure reports setting.
     */

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
                "\n\tsessionType = " + sessionType;
    }

    public Boolean isSendDom() {
        return isSendDom;
    }

    public Configuration setSendDom(boolean sendDom) {
        isSendDom = sendDom;
        return this;
    }

    /**
     * @return Whether to ignore or the blinking caret or not when comparing images.
     */

    public boolean getIgnoreCaret() {
        Boolean ignoreCaret = getDefaultMatchSettings().getIgnoreCaret();
        return ignoreCaret == null ? true : ignoreCaret;
    }

    /**
     * Sets the ignore blinking caret value.
     * @param value The ignore value.
     */

    public Configuration setIgnoreCaret(boolean value) {
        defaultMatchSettings.setIgnoreCaret(value);
        return this;
    }

    public String getApiKey() {
        return apiKey;
    }

    public Configuration setApiKey(String apiKey) {
        this.apiKey = apiKey;
        return this;
    }

    public URI getServerUrl() {
        if (this.serverUrl != null) {
            return URI.create(serverUrl);
        }
        return null;
    }

    public Configuration setServerUrl(String serverUrl) {
        this.serverUrl = serverUrl;
        return this;
    }

    public AbstractProxySettings getProxy() {
        return proxy;
    }

    public Configuration setProxy(AbstractProxySettings proxy) {
        this.proxy = proxy;
        return this;
    }

    public MatchLevel getMatchLevel() {
        return this.defaultMatchSettings.getMatchLevel();
    }

    public boolean getIgnoreDisplacements() {
        return this.ignoreDisplacements;
    }

    public Configuration setMatchLevel(MatchLevel matchLevel) {
        this.defaultMatchSettings.setMatchLevel(matchLevel);
        return this;
    }

    public Configuration setIgnoreDisplacements(boolean isIgnoreDisplacements) {
        this.defaultMatchSettings.setIgnoreDisplacements(isIgnoreDisplacements);
        this.ignoreDisplacements = isIgnoreDisplacements;
        return this;
    }

    public AccessibilitySettings getAccessibilityValidation() {
        return this.accessibilitySettings != null ? this.accessibilitySettings : getDefaultMatchSettings().getAccessibilitySettings();
    }

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

    public Configuration setUseDom(boolean useDom) {
        this.defaultMatchSettings.setUseDom(useDom);
        this.useDom = useDom;
        return this;
    }

    public boolean getUseDom() {
        return useDom;
    }

    public Configuration setEnablePatterns(boolean enablePatterns) {
        this.defaultMatchSettings.setEnablePatterns(enablePatterns);
        this.enablePatterns = enablePatterns;
        return this;
    }

    public boolean getEnablePatterns() {
        return enablePatterns;
    }

}
