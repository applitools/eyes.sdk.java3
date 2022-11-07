package com.applitools.eyes.universal.dto;

import java.util.List;
import java.util.Map;

import com.applitools.eyes.universal.mapper.CoreCodedRegionReferenceMapper;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * configuration
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties({
        "appName",
        "testName",
        "apiKey",
        "sessionType",
        "branchName",
        "parentBranchName",
        "baselineBranchName",
        "agentId",
        "environmentName",
        "saveDiffs",
        "batch",
        "baselineEnvName",
        "viewportSize",
        "matchTimeout",
        "hostApp",
        "hostOs",
        "deviceInfo",
        "saveNewTests",
        "saveFailedTests",
        "stitchOverlap",
        "sendDom",
        "serverUrl",
        "proxy",
        "forceFullPageScreenshot",
        "stitchMode",
        "hideScrollBars",
        "hideCaret",
        "disableBrowserFetching",
        "browsersInfo",
        "debugScreenshots",
        "disabled",
        "connectionTimeout",
        "removeSession",
        "displayName",
        "properties",
        "hostAppInfo",
        "compareWithParentBranch",
        "ignoreBaseline",
        "dontCloseBatches",
        "scrollRootElement",
        "cut",
        "rotation",
        "scaleRatio",
        "layoutBreakpoints",
        "visualGridOptions",
        "waitBeforeCapture",
        "autProxy",
        "browsersInfo",
        "defaultMatchSettings",
})
public class ConfigurationDto {
  /**
   * the open settings.
   */
  @JsonProperty("open")
  private OpenSettingsDto open;

  /**
   * the screenshot settings (part of check settings).
   */
  @JsonProperty("screenshot")
  private CheckSettingsDto screenshot;

  /**
   * the check settings.
   */
  @JsonProperty("check")
  private CheckSettingsDto check;

  /**
   * the close settings.
   */
  @JsonProperty("close")
  private CloseSettingsDto close;

  public void setOpen(OpenSettingsDto open) {
    this.open = open;
  }

  public void setScreenshot(CheckSettingsDto screenshot) {
    this.screenshot = screenshot;
  }

  public void setCheck(CheckSettingsDto check) {
    this.check = check;
  }

  public void setClose(CloseSettingsDto close) {
    this.close = close;
  }

  public String getAppName() {
    return open.getAppName();
  }

  public void setAppName(String appName) {
    this.open.setAppName(appName);
  }

  public String getTestName() {
    return open.getTestName();
  }

  public void setTestName(String testName) {
    this.open.setTestName(testName);
  }

  public String getApiKey() {
    return open.getApiKey();
  }

  public void setApiKey(String apiKey) {
    this.open.setApiKey(apiKey);
  }

  public String getSessionType() {
    return open.getSessionType();
  }

  public void setSessionType(String sessionType) {
    this.open.setSessionType(sessionType);
  }

  public String getBranchName() {
    return open.getBranchName();
  }

  public void setBranchName(String branchName) {
    this.open.setBranchName(branchName);
  }

  public String getParentBranchName() {
    return open.getParentBranchName();
  }

  public void setParentBranchName(String parentBranchName) {
    this.open.setParentBranchName(parentBranchName);
  }

  public String getBaselineBranchName() {
    return open.getBaselineBranchName();
  }

  public void setBaselineBranchName(String baselineBranchName) {
    this.open.setBaselineBranchName(baselineBranchName);
  }

  public String getAgentId() {
    return open.getAgentId();
  }

  public void setAgentId(String agentId) {
    this.open.setAgentId(agentId);
  }

  public String getEnvironmentName() {
    return open.getEnvironmentName();
  }

  public void setEnvironmentName(String environmentName) {
    this.open.setEnvironmentName(environmentName);
  }

  public Boolean getSaveDiffs() {
    return open.getSaveDiffs();
  }

  public void setSaveDiffs(Boolean saveDiffs) {
    this.open.setSaveDiffs(saveDiffs);
  }

  public BatchDto getBatch() {
    return open.getBatch();
  }

  public void setBatch(BatchDto batch) {
    this.open.setBatch(batch);
  }

  public String getBaselineEnvName() {
    return open.getBaselineEnvName();
  }

  public void setBaselineEnvName(String baselineEnvName) {
    this.open.setBaselineEnvName(baselineEnvName);
  }

  public RectangleSizeDto getViewportSize() {
    return open.getEnvironment().getViewportSize();
  }

  public void setViewportSize(RectangleSizeDto viewportSize) {
    this.open.getEnvironment().setViewportSize(viewportSize);
  }

  public void setDefaultMatchSettings(MatchSettingsDto defaultMatchSettings) {
    if (defaultMatchSettings == null)
      return;

    this.check.setAccessibilitySettings(defaultMatchSettings.getAccessibilitySettings());
    this.check.setMatchLevel(defaultMatchSettings.getMatchLevel());
    this.check.setSendDom(defaultMatchSettings.getSendDom());
    this.check.setUseDom(defaultMatchSettings.getUseDom());
//    defaultMatchSettings.getExact();
    this.check.setEnablePatterns(defaultMatchSettings.getEnablePatterns());
    this.check.setIgnoreCaret(defaultMatchSettings.getIgnoreCaret());
    this.check.setIgnoreDisplacements(defaultMatchSettings.getIgnoreDisplacements());

    this.check.setAccessibilityRegions(defaultMatchSettings.getAccessibilityRegions());
    this.check.setFloatingRegions(defaultMatchSettings.getFloatingRegions());
    this.check.setContentRegions(CoreCodedRegionReferenceMapper.toCodedRegionReferenceList(
            defaultMatchSettings.getContentRegions()
    ));
    this.check.setLayoutRegions(CoreCodedRegionReferenceMapper.toCodedRegionReferenceList(
            defaultMatchSettings.getLayoutRegions()
    ));
    this.check.setIgnoreRegions(CoreCodedRegionReferenceMapper.toCodedRegionReferenceList(
            defaultMatchSettings.getIgnoreRegions()
    ));
    this.check.setStrictRegions(CoreCodedRegionReferenceMapper.toCodedRegionReferenceList(
            defaultMatchSettings.getIgnoreRegions()
    ));
  }

  public Integer getMatchTimeout() {
    return this.check.getRetryTimeout();
  }

  public void setMatchTimeout(Integer matchTimeout) {
    this.check.setRetryTimeout(matchTimeout);
  }

  public String getHostApp() {
    return open.getEnvironment().getHostingApp();
  }

  public void setHostApp(String hostApp) {
    this.open.getEnvironment().setHostingApp(hostApp);
  }

  public String getHostOS() {
    return open.getEnvironment().getOs();
  }

  public void setHostOS(String hostOS) {
    this.open.getEnvironment().setOs(hostOS);
  }

  public String getDeviceInfo() {
    return open.getEnvironment().getDeviceName();
  }

  public void setDeviceInfo(String deviceInfo) {
    this.open.getEnvironment().setDeviceName(deviceInfo);
  }

  public Boolean getSaveNewTests() {
    return close.getUpdateBaselineIfNew();
  }

  public void setSaveNewTests(Boolean saveNewTests) {
    this.close.setUpdateBaselineIfNew(saveNewTests);
  }

  public Boolean getSaveFailedTests() {
    return close.getUpdateBaselineIfDifferent();
  }

  public void setSaveFailedTests(Boolean saveFailedTests) {
    this.close.setUpdateBaselineIfDifferent(saveFailedTests);
  }

  public Integer getStitchOverlap() {
    return screenshot.getOverlap();
  }

  public void setStitchOverlap(Integer stitchOverlap) {
    this.screenshot.setOverlap(stitchOverlap);
  }

  public Boolean getSendDom() {
    return check.getSendDom();
  }

  public void setSendDom(Boolean sendDom) {
    this.check.setSendDom(sendDom);
  }

  public String getServerUrl() {
    return open.getServerUrl();
  }

  public void setServerUrl(String serverUrl) {
    this.open.setServerUrl(serverUrl);
  }

  public ProxyDto getProxy() {
    return open.getProxy();
  }

  public void setProxy(ProxyDto proxy) {
    this.open.setProxy(proxy);
  }

  public Boolean getForceFullPageScreenshot() {
    return screenshot.getFully();
  }

  public void setForceFullPageScreenshot(Boolean forceFullPageScreenshot) {
    this.screenshot.setFully(forceFullPageScreenshot);
  }

//  public Integer getWaitBeforeScreenshots() {
//    return waitBeforeScreenshots;
//  }

//  public void setWaitBeforeScreenshots(Integer waitBeforeScreenshots) {
//    this.waitBeforeScreenshots = waitBeforeScreenshots;
//  }

  public String getStitchMode() {
    return screenshot.getStitchMode();
  }

  public void setStitchMode(String stitchMode) {
    this.screenshot.setStitchMode(stitchMode);
  }

  public Boolean getHideScrollBars() {
    return screenshot.getHideScrollbars();
  }

  public void setHideScrollBars(Boolean hideScrollBars) {
    this.screenshot.setHideScrollbars(hideScrollBars);
  }

  public Boolean getHideCaret() {
    return screenshot.getHideCaret();
  }

  public void setHideCaret(Boolean hideCaret) {
    this.screenshot.setHideCaret(hideCaret);
  }


  public Boolean getDisableBrowserFetching() {
    return check.getDisableBrowserFetching();
  }

  public void setDisableBrowserFetching(Boolean disableBrowserFetching) {
    this.check.setDisableBrowserFetching(disableBrowserFetching);
  }

  public List<IBrowsersInfo> getBrowsersInfo() {
    return check.getRenderers();
  }

  public void setBrowsersInfo(List<IBrowsersInfo> browsersInfo) {
    this.check.setRenderers(browsersInfo);
  }

//  public Object getLogs() {
//    return logs;
//  }

//  public void setLogs(Object logs) {
//    this.logs = logs;
//  }

  public DebugScreenshotHandlerDto getDebugScreenshots() {
    return screenshot.getDebugImages();
  }

  public void setDebugScreenshots(DebugScreenshotHandlerDto debugScreenshots) {
    this.screenshot.setDebugImages(debugScreenshots);
  }

  public Boolean getDisabled() {
    return open.getDisabled();
  }

  public void setDisabled(Boolean disabled) {
    this.open.setDisabled(disabled);
  }

  public Integer getConnectionTimeout() {
    return open.getConnectionTimeout();
  }

  public void setConnectionTimeout(Integer connectionTimeout) {
    this.open.setConnectionTimeout(connectionTimeout);
  }

  public Boolean getRemoveSession() {
    return open.getRemoveSession();
  }

  public void setRemoveSession(Boolean removeSession) {
    this.open.setRemoveSession(removeSession);
  }

//  public Object getRemoteEvents() {
//    return remoteEvents;
//  }
//
//  public void setRemoteEvents(Object remoteEvents) {
//    this.remoteEvents = remoteEvents;
//  }

  public String getDisplayName() {
    return open.getDisplayName();
  }

  public void setDisplayName(String displayName) {
    this.open.setDisplayName(displayName);
  }

  public List<CustomPropertyDto> getProperties() {
    return open.getProperties();
  }

  public void setProperties(List<CustomPropertyDto> properties) {
    this.open.setProperties(properties);
  }

  public String getHostAppInfo() {
    return open.getEnvironment().getHostingAppInfo();
  }

  public void setHostAppInfo(String hostAppInfo) {
    this.open.getEnvironment().setHostingAppInfo(hostAppInfo);
  }

  public String getHostOSInfo() {
    return open.getEnvironment().getOsInfo();
  }

  public void setHostOSInfo(String hostOSInfo) {
    this.open.getEnvironment().setOsInfo(hostOSInfo);
  }

  public Boolean getCompareWithParentBranch() {
    return open.getCompareWithParentBranch();
  }

  public void setCompareWithParentBranch(Boolean compareWithParentBranch) {
    this.open.setCompareWithParentBranch(compareWithParentBranch);
  }

  public Boolean getIgnoreBaseline() {
    return open.getIgnoreBaseline();
  }

  public void setIgnoreBaseline(Boolean ignoreBaseline) {
    this.open.setIgnoreBaseline(ignoreBaseline);
  }

  public Boolean getDontCloseBatches() {
    return open.getKeepBatchOpen();
  }

  public void setDontCloseBatches(Boolean dontCloseBatches) {
    this.open.setKeepBatchOpen(dontCloseBatches);
  }

  public TRegion getScrollRootElement() {
    return screenshot.getScrollRootElement();
  }

  public void setScrollRootElement(TRegion scrollRootElement) {
    this.screenshot.setScrollRootElement(scrollRootElement);
  }

  public ICut getCut() {
    return screenshot.getNormalization().getCut();
  }

  public void setCut(ICut cut) {
    this.screenshot.getNormalization().setCut(cut);
  }

  public Integer getRotation() {
    return screenshot.getNormalization().getRotation();
  }

  public void setRotation(Integer rotation) {
    this.screenshot.getNormalization().setRotation(rotation);
  }

  public Double getScaleRatio() {
    return screenshot.getNormalization().getScaleRatio();
  }

  public void setScaleRatio(Double scaleRatio) {
    this.screenshot.getNormalization().setScaleRatio(scaleRatio);
  }

//  public Integer getConcurrentSessions() {
//    return concurrentSessions;
//  }

//  public void setConcurrentSessions(Integer concurrentSessions) {
//    this.concurrentSessions = concurrentSessions;
//  }

  public Object getLayoutBreakpoints() {
    return check.getLayoutBreakpoints();
  }

  public void setLayoutBreakpoints(Object layoutBreakpoints) {
    this.check.setLayoutBreakpoints(layoutBreakpoints);
  }

  public Map<String, Object> getVisualGridOptions() {
    return check.getUfgOptions();
  }

  public void setVisualGridOptions(Map<String, Object> visualGridOptions) {
    this.check.setUfgOptions(visualGridOptions);
  }

//  public Boolean getUseCeilForViewportSize() {
//    return useCeilForViewportSize;
//  }

//  public void setUseCeilForViewportSize(Boolean useCeilForViewportSize) {
//    this.useCeilForViewportSize = useCeilForViewportSize;
//  }

  public Integer getWaitBeforeCapture() {
    return screenshot.getWaitBeforeCapture();
  }

  public void setWaitBeforeCapture(Integer waitBeforeCapture) {
    this.screenshot.setWaitBeforeCapture(waitBeforeCapture);
  }

  public AutProxyDto getAutProxy() {
    return check.getAutProxy();
  }

  public void setAutProxy(AutProxyDto autProxy) {
    this.check.setAutProxy(autProxy);
  }
}
