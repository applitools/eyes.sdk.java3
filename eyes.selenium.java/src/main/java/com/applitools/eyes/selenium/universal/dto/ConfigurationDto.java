package com.applitools.eyes.selenium.universal.dto;

import java.util.List;
import java.util.Set;

/**
 * configuration
 */
public class ConfigurationDto {
  private String branchName;
  private String parentBranchName;
  private String baselineBranchName;
  private String agentId;
  private String environmentName;
  private Boolean saveDiffs;
  private String sessionType;
  private BatchDto batch;
  private String baselineEnvName;
  private String appName;
  private String testName;
  private RectangleSizeDto viewportSize;
  private Boolean ignoreDisplacements;
  private ImageMatchSettingsDto defaultMatchSettings;

  private Integer matchTimeout;
  private String hostApp;
  private String hostOS;
  private String deviceInfo;
  private String hostingAppInfo;
  private String osInfo;
  private Boolean saveNewTests;
  private Boolean saveFailedTests;
  private Integer stitchOverlap;
  private Boolean isSendDom;
  private String apiKey;
  private String serverUrl;
  private AbstractProxySettingsDto proxy;

  private String failureReports;
  private AccessibilitySettingsDto accessibilitySettings;
  private Boolean enablePatterns;
  private Boolean useDom;
  private Integer abortIdleTestTimeout;
  private Boolean forceFullPageScreenshot;
  private Integer waitBeforeScreenshots;
  private String stitchMode;
  private Boolean hideScrollbars;
  private Boolean hideCaret;
  private Boolean isVisualGrid;
  private Boolean disableBrowserFetching;
  private Boolean useCookies;
  private Boolean captureStatusBar;
  private Boolean isRenderingConfig;
  private List<RenderBrowserInfoDto> browsersInfo;
  private Set<String> features;
  private List<VisualGridOptionDto> visualGridOptions;
  private Boolean isDefaultLayoutBreakpointsSet;
  private List<Integer> layoutBreakpoints;

  public Set<String> getFeatures() {
    return features;
  }

  public void setFeatures(Set<String> features) {
    this.features = features;
  }

  public List<Integer> getLayoutBreakpoints() {
    return layoutBreakpoints;
  }

  public void setLayoutBreakpoints(List<Integer> layoutBreakpoints) {
    this.layoutBreakpoints = layoutBreakpoints;
  }

  public Boolean getDefaultLayoutBreakpointsSet() {
    return isDefaultLayoutBreakpointsSet;
  }

  public void setDefaultLayoutBreakpointsSet(Boolean defaultLayoutBreakpointsSet) {
    isDefaultLayoutBreakpointsSet = defaultLayoutBreakpointsSet;
  }

  public List<VisualGridOptionDto> getVisualGridOptions() {
    return visualGridOptions;
  }

  public void setVisualGridOptions(List<VisualGridOptionDto> visualGridOptions) {
    this.visualGridOptions = visualGridOptions;
  }

  public String getFailureReports() {
    return failureReports;
  }

  public void setFailureReports(String failureReports) {
    this.failureReports = failureReports;
  }

  public String getAppName() {
    return appName;
  }

  public void setAppName(String appName) {
    this.appName = appName;
  }

  public String getTestName() {
    return testName;
  }

  public void setTestName(String testName) {
    this.testName = testName;
  }

  public String getApiKey() {
    return apiKey;
  }

  public void setApiKey(String apiKey) {
    this.apiKey = apiKey;
  }

  public String getSessionType() {
    return sessionType;
  }

  public void setSessionType(String sessionType) {
    this.sessionType = sessionType;
  }

  public String getBranchName() {
    return branchName;
  }

  public void setBranchName(String branchName) {
    this.branchName = branchName;
  }

  public String getParentBranchName() {
    return parentBranchName;
  }

  public void setParentBranchName(String parentBranchName) {
    this.parentBranchName = parentBranchName;
  }

  public String getBaselineBranchName() {
    return baselineBranchName;
  }

  public void setBaselineBranchName(String baselineBranchName) {
    this.baselineBranchName = baselineBranchName;
  }

  public String getAgentId() {
    return agentId;
  }

  public void setAgentId(String agentId) {
    this.agentId = agentId;
  }

  public String getEnvironmentName() {
    return environmentName;
  }

  public void setEnvironmentName(String environmentName) {
    this.environmentName = environmentName;
  }

  public Boolean getSaveDiffs() {
    return saveDiffs;
  }

  public void setSaveDiffs(Boolean saveDiffs) {
    this.saveDiffs = saveDiffs;
  }

  public BatchDto getBatch() {
    return batch;
  }

  public void setBatch(BatchDto batch) {
    this.batch = batch;
  }

  public String getBaselineEnvName() {
    return baselineEnvName;
  }

  public void setBaselineEnvName(String baselineEnvName) {
    this.baselineEnvName = baselineEnvName;
  }

  public RectangleSizeDto getViewportSize() {
    return viewportSize;
  }

  public void setViewportSize(RectangleSizeDto viewportSize) {
    this.viewportSize = viewportSize;
  }

  public Boolean getIgnoreDisplacements() {
    return ignoreDisplacements;
  }

  public void setIgnoreDisplacements(Boolean ignoreDisplacements) {
    this.ignoreDisplacements = ignoreDisplacements;
  }

  public ImageMatchSettingsDto getDefaultMatchSettings() {
    return defaultMatchSettings;
  }

  public void setDefaultMatchSettings(ImageMatchSettingsDto defaultMatchSettings) {
    this.defaultMatchSettings = defaultMatchSettings;
  }

  public Integer getMatchTimeout() {
    return matchTimeout;
  }

  public void setMatchTimeout(Integer matchTimeout) {
    this.matchTimeout = matchTimeout;
  }

  public String getHostApp() {
    return hostApp;
  }

  public void setHostApp(String hostApp) {
    this.hostApp = hostApp;
  }

  public String getHostOS() {
    return hostOS;
  }

  public void setHostOS(String hostOS) {
    this.hostOS = hostOS;
  }

  public String getDeviceInfo() {
    return deviceInfo;
  }

  public void setDeviceInfo(String deviceInfo) {
    this.deviceInfo = deviceInfo;
  }

  public String getHostingAppInfo() {
    return hostingAppInfo;
  }

  public void setHostingAppInfo(String hostingAppInfo) {
    this.hostingAppInfo = hostingAppInfo;
  }

  public String getOsInfo() {
    return osInfo;
  }

  public void setOsInfo(String osInfo) {
    this.osInfo = osInfo;
  }

  public Boolean getSaveNewTests() {
    return saveNewTests;
  }

  public void setSaveNewTests(Boolean saveNewTests) {
    this.saveNewTests = saveNewTests;
  }

  public Boolean getSaveFailedTests() {
    return saveFailedTests;
  }

  public void setSaveFailedTests(Boolean saveFailedTests) {
    this.saveFailedTests = saveFailedTests;
  }

  public Integer getStitchOverlap() {
    return stitchOverlap;
  }

  public void setStitchOverlap(Integer stitchOverlap) {
    this.stitchOverlap = stitchOverlap;
  }

  public Boolean getSendDom() {
    return isSendDom;
  }

  public void setSendDom(Boolean sendDom) {
    isSendDom = sendDom;
  }

  public String getServerUrl() {
    return serverUrl;
  }

  public void setServerUrl(String serverUrl) {
    this.serverUrl = serverUrl;
  }

  public AbstractProxySettingsDto getProxy() {
    return proxy;
  }

  public void setProxy(AbstractProxySettingsDto proxy) {
    this.proxy = proxy;
  }

  public AccessibilitySettingsDto getAccessibilitySettings() {
    return accessibilitySettings;
  }

  public void setAccessibilitySettings(AccessibilitySettingsDto accessibilitySettings) {
    this.accessibilitySettings = accessibilitySettings;
  }

  public Boolean getEnablePatterns() {
    return enablePatterns;
  }

  public void setEnablePatterns(Boolean enablePatterns) {
    this.enablePatterns = enablePatterns;
  }

  public Boolean getUseDom() {
    return useDom;
  }

  public void setUseDom(Boolean useDom) {
    this.useDom = useDom;
  }

  public Integer getAbortIdleTestTimeout() {
    return abortIdleTestTimeout;
  }

  public void setAbortIdleTestTimeout(Integer abortIdleTestTimeout) {
    this.abortIdleTestTimeout = abortIdleTestTimeout;
  }

  public Boolean getForceFullPageScreenshot() {
    return forceFullPageScreenshot;
  }

  public void setForceFullPageScreenshot(Boolean forceFullPageScreenshot) {
    this.forceFullPageScreenshot = forceFullPageScreenshot;
  }

  public Integer getWaitBeforeScreenshots() {
    return waitBeforeScreenshots;
  }

  public void setWaitBeforeScreenshots(Integer waitBeforeScreenshots) {
    this.waitBeforeScreenshots = waitBeforeScreenshots;
  }

  public String getStitchMode() {
    return stitchMode;
  }

  public void setStitchMode(String stitchMode) {
    this.stitchMode = stitchMode;
  }

  public Boolean getHideScrollbars() {
    return hideScrollbars;
  }

  public void setHideScrollbars(Boolean hideScrollbars) {
    this.hideScrollbars = hideScrollbars;
  }

  public Boolean getHideCaret() {
    return hideCaret;
  }

  public void setHideCaret(Boolean hideCaret) {
    this.hideCaret = hideCaret;
  }

  public Boolean getVisualGrid() {
    return isVisualGrid;
  }

  public void setVisualGrid(Boolean visualGrid) {
    isVisualGrid = visualGrid;
  }

  public Boolean getDisableBrowserFetching() {
    return disableBrowserFetching;
  }

  public void setDisableBrowserFetching(Boolean disableBrowserFetching) {
    this.disableBrowserFetching = disableBrowserFetching;
  }

  public Boolean getUseCookies() {
    return useCookies;
  }

  public void setUseCookies(Boolean useCookies) {
    this.useCookies = useCookies;
  }

  public Boolean getCaptureStatusBar() {
    return captureStatusBar;
  }

  public void setCaptureStatusBar(Boolean captureStatusBar) {
    this.captureStatusBar = captureStatusBar;
  }

  public Boolean getRenderingConfig() {
    return isRenderingConfig;
  }

  public void setRenderingConfig(Boolean renderingConfig) {
    isRenderingConfig = renderingConfig;
  }

  public List<RenderBrowserInfoDto> getBrowsersInfo() {
    return browsersInfo;
  }

  public void setBrowsersInfo(List<RenderBrowserInfoDto> browsersInfo) {
    this.browsersInfo = browsersInfo;
  }

  @Override
  public String toString() {
    return "ConfigurationDto{" +
        "branchName='" + branchName + '\'' +
        ", parentBranchName='" + parentBranchName + '\'' +
        ", baselineBranchName='" + baselineBranchName + '\'' +
        ", agentId='" + agentId + '\'' +
        ", environmentName='" + environmentName + '\'' +
        ", saveDiffs=" + saveDiffs +
        ", sessionType='" + sessionType + '\'' +
        ", batch=" + batch +
        ", baselineEnvName='" + baselineEnvName + '\'' +
        ", appName='" + appName + '\'' +
        ", testName='" + testName + '\'' +
        ", viewportSize=" + viewportSize +
        ", ignoreDisplacements=" + ignoreDisplacements +
        ", defaultMatchSettings=" + defaultMatchSettings +
        ", matchTimeout=" + matchTimeout +
        ", hostApp='" + hostApp + '\'' +
        ", hostOS='" + hostOS + '\'' +
        ", deviceInfo='" + deviceInfo + '\'' +
        ", hostingAppInfo='" + hostingAppInfo + '\'' +
        ", osInfo='" + osInfo + '\'' +
        ", saveNewTests=" + saveNewTests +
        ", saveFailedTests=" + saveFailedTests +
        ", stitchOverlap=" + stitchOverlap +
        ", isSendDom=" + isSendDom +
        ", apiKey='" + apiKey + '\'' +
        ", serverUrl='" + serverUrl + '\'' +
        ", proxy=" + proxy +
        ", failureReports='" + failureReports + '\'' +
        ", accessibilitySettings=" + accessibilitySettings +
        ", enablePatterns=" + enablePatterns +
        ", useDom=" + useDom +
        ", abortIdleTestTimeout=" + abortIdleTestTimeout +
        ", forceFullPageScreenshot=" + forceFullPageScreenshot +
        ", waitBeforeScreenshots=" + waitBeforeScreenshots +
        ", stitchMode='" + stitchMode + '\'' +
        ", hideScrollbars=" + hideScrollbars +
        ", hideCaret=" + hideCaret +
        ", isVisualGrid=" + isVisualGrid +
        ", disableBrowserFetching=" + disableBrowserFetching +
        ", useCookies=" + useCookies +
        ", captureStatusBar=" + captureStatusBar +
        ", isRenderingConfig=" + isRenderingConfig +
        ", browsersInfo=" + browsersInfo +
        ", features=" + features +
        ", visualGridOptions=" + visualGridOptions +
        ", isDefaultLayoutBreakpointsSet=" + isDefaultLayoutBreakpointsSet +
        ", layoutBreakpoints=" + layoutBreakpoints +
        '}';
  }
}
