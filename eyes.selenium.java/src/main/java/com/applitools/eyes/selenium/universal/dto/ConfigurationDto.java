package com.applitools.eyes.selenium.universal.dto;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * configuration
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ConfigurationDto {
  // EyesBaseConfig
  private Object logs;
  private DebugScreenshotHandlerDto debugScreenshots;
  private String agentId;
  private String apiKey;
  private String serverUrl;
  private ProxyDto proxy;
  private Boolean isDisabled;
  private Integer connectionTimeout;
  private Boolean removeSession;
  private Object remoteEvents;

  // EyesOpenConfig
  private String appName;
  private String testName;
  private String displayName;
  private RectangleSizeDto viewportSize;
  private String sessionType;
  private List<CustomPropertyDto> properties;
  private BatchDto batch;
  private MatchSettingsDto defaultMatchSettings; // TODO defaultMatchSettings?: MatchSettings<Region>
  private String hostApp;
  private String hostOS;
  private String hostAppInfo;
  private String hostOSInfo;
  private String deviceInfo;
  private String baselineEnvName;
  private String environmentName;
  private String branchName;
  private String parentBranchName;
  private String baselineBranchName;
  private Boolean compareWithParentBranch;
  private Boolean ignoreBaseline;
  private Boolean saveFailedTests;
  private Boolean saveNewTests;
  private Boolean saveDiffs;
  private Boolean dontCloseBatches;

  // EyesCheckConfig
  private Boolean sendDom;
  private Integer matchTimeout;
  private Boolean forceFullPageScreenshot;

  // EyesClassicConfig<TElement, TSelector>
  private Integer waitBeforeScreenshots;
  private String stitchMode; // 'CSS' | 'Scroll'
  private Boolean hideScrollbars;
  private Boolean hideCaret;
  private Integer stitchOverlap;
  private IScrollRootElement scrollRootElement; // TElement | TSelector
  private ICut cut; // ImageCropRect | ImageCropRegion
  private Integer rotation; // ImageRotation -270 | -180 | -90 | 0 | 90 | 180 | 270
  private Double scaleRatio;

  // EyesUFGConfig
  private Integer concurrentSessions;
  private List<IBrowsersInfo> browsersInfo; // (DesktopBrowserRenderer | ChromeEmulationDeviceRenderer | IOSDeviceRenderer)[]
  private Map<String,Object> visualGridOptions; // Record<string, any>
  private Object layoutBreakpoints; // boolean | number[]
  private Boolean disableBrowserFetching;


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

  public MatchSettingsDto getDefaultMatchSettings() {
    return defaultMatchSettings;
  }

  public void setDefaultMatchSettings(MatchSettingsDto defaultMatchSettings) {
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
    return sendDom;
  }

  public void setSendDom(Boolean sendDom) {
    this.sendDom = sendDom;
  }

  public String getServerUrl() {
    return serverUrl;
  }

  public void setServerUrl(String serverUrl) {
    this.serverUrl = serverUrl;
  }

  public ProxyDto getProxy() {
    return proxy;
  }

  public void setProxy(ProxyDto proxy) {
    this.proxy = proxy;
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


  public Boolean getDisableBrowserFetching() {
    return disableBrowserFetching;
  }

  public void setDisableBrowserFetching(Boolean disableBrowserFetching) {
    this.disableBrowserFetching = disableBrowserFetching;
  }

  public List<IBrowsersInfo> getBrowsersInfo() {
    return browsersInfo;
  }

  public void setBrowsersInfo(List<IBrowsersInfo> browsersInfo) {
    this.browsersInfo = browsersInfo;
  }

  public Object getLogs() {
    return logs;
  }

  public void setLogs(Object logs) {
    this.logs = logs;
  }

  public DebugScreenshotHandlerDto getDebugScreenshots() {
    return debugScreenshots;
  }

  public void setDebugScreenshots(DebugScreenshotHandlerDto debugScreenshots) {
    this.debugScreenshots = debugScreenshots;
  }

  public Boolean getDisabled() {
    return isDisabled;
  }

  public void setDisabled(Boolean disabled) {
    isDisabled = disabled;
  }

  public Integer getConnectionTimeout() {
    return connectionTimeout;
  }

  public void setConnectionTimeout(Integer connectionTimeout) {
    this.connectionTimeout = connectionTimeout;
  }

  public Boolean getRemoveSession() {
    return removeSession;
  }

  public void setRemoveSession(Boolean removeSession) {
    this.removeSession = removeSession;
  }

  public Object getRemoteEvents() {
    return remoteEvents;
  }

  public void setRemoteEvents(Object remoteEvents) {
    this.remoteEvents = remoteEvents;
  }

  public String getDisplayName() {
    return displayName;
  }

  public void setDisplayName(String displayName) {
    this.displayName = displayName;
  }

  public List<CustomPropertyDto> getProperties() {
    return properties;
  }

  public void setProperties(List<CustomPropertyDto> properties) {
    this.properties = properties;
  }

  public String getHostAppInfo() {
    return hostAppInfo;
  }

  public void setHostAppInfo(String hostAppInfo) {
    this.hostAppInfo = hostAppInfo;
  }

  public String getHostOSInfo() {
    return hostOSInfo;
  }

  public void setHostOSInfo(String hostOSInfo) {
    this.hostOSInfo = hostOSInfo;
  }

  public Boolean getCompareWithParentBranch() {
    return compareWithParentBranch;
  }

  public void setCompareWithParentBranch(Boolean compareWithParentBranch) {
    this.compareWithParentBranch = compareWithParentBranch;
  }

  public Boolean getIgnoreBaseline() {
    return ignoreBaseline;
  }

  public void setIgnoreBaseline(Boolean ignoreBaseline) {
    this.ignoreBaseline = ignoreBaseline;
  }

  public Boolean getDontCloseBatches() {
    return dontCloseBatches;
  }

  public void setDontCloseBatches(Boolean dontCloseBatches) {
    this.dontCloseBatches = dontCloseBatches;
  }

  public IScrollRootElement getScrollRootElement() {
    return scrollRootElement;
  }

  public void setScrollRootElement(IScrollRootElement scrollRootElement) {
    this.scrollRootElement = scrollRootElement;
  }

  public ICut getCut() {
    return cut;
  }

  public void setCut(ICut cut) {
    this.cut = cut;
  }

  public Integer getRotation() {
    return rotation;
  }

  public void setRotation(Integer rotation) {
    this.rotation = rotation;
  }

  public Double getScaleRatio() {
    return scaleRatio;
  }

  public void setScaleRatio(Double scaleRatio) {
    this.scaleRatio = scaleRatio;
  }

  public Integer getConcurrentSessions() {
    return concurrentSessions;
  }

  public void setConcurrentSessions(Integer concurrentSessions) {
    this.concurrentSessions = concurrentSessions;
  }

  public Object getLayoutBreakpoints() {
    return layoutBreakpoints;
  }

  public void setLayoutBreakpoints(Object layoutBreakpoints) {
    this.layoutBreakpoints = layoutBreakpoints;
  }

  public Map<String, Object> getVisualGridOptions() {
    return visualGridOptions;
  }

  public void setVisualGridOptions(Map<String, Object> visualGridOptions) {
    this.visualGridOptions = visualGridOptions;
  }
}
