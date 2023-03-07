package com.applitools.eyes.universal.dto;

import java.util.List;
import java.util.Map;

import com.applitools.eyes.StitchOverlap;
import com.applitools.eyes.universal.mapper.CoreCodedRegionReferenceMapper;
import com.fasterxml.jackson.annotation.JsonIgnore;
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

  @JsonIgnore
  public String getAppName() {
    return open.getAppName();
  }

  @JsonIgnore
  public void setAppName(String appName) {
    this.open.setAppName(appName);
  }

  @JsonIgnore
  public String getTestName() {
    return open.getTestName();
  }

  @JsonIgnore
  public void setTestName(String testName) {
    this.open.setTestName(testName);
  }

  @JsonIgnore
  public String getApiKey() {
    return open.getApiKey();
  }

  @JsonIgnore
  public void setApiKey(String apiKey) {
    this.open.setApiKey(apiKey);
  }

  @JsonIgnore
  public String getSessionType() {
    return open.getSessionType();
  }

  @JsonIgnore
  public void setSessionType(String sessionType) {
    this.open.setSessionType(sessionType);
  }

  @JsonIgnore
  public String getBranchName() {
    return open.getBranchName();
  }

  @JsonIgnore
  public void setBranchName(String branchName) {
    this.open.setBranchName(branchName);
  }

  @JsonIgnore
  public String getParentBranchName() {
    return open.getParentBranchName();
  }

  @JsonIgnore
  public void setParentBranchName(String parentBranchName) {
    this.open.setParentBranchName(parentBranchName);
  }

  @JsonIgnore
  public String getBaselineBranchName() {
    return open.getBaselineBranchName();
  }

  @JsonIgnore
  public void setBaselineBranchName(String baselineBranchName) {
    this.open.setBaselineBranchName(baselineBranchName);
  }

  @JsonIgnore
  public String getAgentId() {
    return open.getAgentId();
  }

  @JsonIgnore
  public void setAgentId(String agentId) {
    this.open.setAgentId(agentId);
  }

  @JsonIgnore
  public String getEnvironmentName() {
    return open.getEnvironmentName();
  }

  @JsonIgnore
  public void setEnvironmentName(String environmentName) {
    this.open.setEnvironmentName(environmentName);
  }

  @JsonIgnore
  public Boolean getSaveDiffs() {
    return open.getSaveDiffs();
  }

  @JsonIgnore
  public void setSaveDiffs(Boolean saveDiffs) {
    this.open.setSaveDiffs(saveDiffs);
  }

  @JsonIgnore
  public BatchDto getBatch() {
    return open.getBatch();
  }

  @JsonIgnore
  public void setBatch(BatchDto batch) {
    this.open.setBatch(batch);
  }

  @JsonIgnore
  public String getBaselineEnvName() {
    return open.getBaselineEnvName();
  }

  @JsonIgnore
  public void setBaselineEnvName(String baselineEnvName) {
    this.open.setBaselineEnvName(baselineEnvName);
  }

  @JsonIgnore
  public RectangleSizeDto getViewportSize() {
    return open.getEnvironment().getViewportSize();
  }

  @JsonIgnore
  public void setViewportSize(RectangleSizeDto viewportSize) {
    this.open.getEnvironment().setViewportSize(viewportSize);
  }

  @JsonIgnore
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

  @JsonIgnore
  public Integer getMatchTimeout() {
    return this.check.getRetryTimeout();
  }

  @JsonIgnore
  public void setMatchTimeout(Integer matchTimeout) {
    this.check.setRetryTimeout(matchTimeout);
  }

  @JsonIgnore
  public String getHostApp() {
    return open.getEnvironment().getHostingApp();
  }

  @JsonIgnore
  public void setHostApp(String hostApp) {
    this.open.getEnvironment().setHostingApp(hostApp);
  }

  @JsonIgnore
  public String getHostOS() {
    return open.getEnvironment().getOs();
  }

  @JsonIgnore
  public void setHostOS(String hostOS) {
    this.open.getEnvironment().setOs(hostOS);
  }

  @JsonIgnore
  public String getDeviceInfo() {
    return open.getEnvironment().getDeviceName();
  }

  @JsonIgnore
  public void setDeviceInfo(String deviceInfo) {
    this.open.getEnvironment().setDeviceName(deviceInfo);
  }

  @JsonIgnore
  public Boolean getSaveNewTests() {
    return close.getUpdateBaselineIfNew();
  }

  @JsonIgnore
  public void setSaveNewTests(Boolean saveNewTests) {
    this.close.setUpdateBaselineIfNew(saveNewTests);
  }

  @JsonIgnore
  public Boolean getSaveFailedTests() {
    return close.getUpdateBaselineIfDifferent();
  }

  @JsonIgnore
  public void setSaveFailedTests(Boolean saveFailedTests) {
    this.close.setUpdateBaselineIfDifferent(saveFailedTests);
  }

  @JsonIgnore
  public StitchOverlap getStitchOverlap() {
    return screenshot.getOverlap();
  }

  @JsonIgnore
  public void setStitchOverlap(StitchOverlap stitchOverlap) {
    this.screenshot.setOverlap(stitchOverlap);
  }

  @JsonIgnore
  public Boolean getSendDom() {
    return check.getSendDom();
  }

  @JsonIgnore
  public void setSendDom(Boolean sendDom) {
    this.check.setSendDom(sendDom);
  }

  @JsonIgnore
  public String getServerUrl() {
    return open.getServerUrl();
  }

  @JsonIgnore
  public void setServerUrl(String serverUrl) {
    this.open.setServerUrl(serverUrl);
  }

  @JsonIgnore
  public ProxyDto getProxy() {
    return open.getProxy();
  }

  @JsonIgnore
  public void setProxy(ProxyDto proxy) {
    this.open.setProxy(proxy);
  }

  @JsonIgnore
  public Boolean getForceFullPageScreenshot() {
    return screenshot.getFully();
  }

  @JsonIgnore
  public void setForceFullPageScreenshot(Boolean forceFullPageScreenshot) {
    this.screenshot.setFully(forceFullPageScreenshot);
  }

  @JsonIgnore
  public String getStitchMode() {
    return screenshot.getStitchMode();
  }

  @JsonIgnore
  public void setStitchMode(String stitchMode) {
    this.screenshot.setStitchMode(stitchMode);
  }

  @JsonIgnore
  public Boolean getHideScrollBars() {
    return screenshot.getHideScrollbars();
  }

  @JsonIgnore
  public void setHideScrollBars(Boolean hideScrollBars) {
    this.screenshot.setHideScrollbars(hideScrollBars);
  }

  @JsonIgnore
  public Boolean getHideCaret() {
    return screenshot.getHideCaret();
  }

  @JsonIgnore
  public void setHideCaret(Boolean hideCaret) {
    this.screenshot.setHideCaret(hideCaret);
  }

  @JsonIgnore
  public Boolean getDisableBrowserFetching() {
    return check.getDisableBrowserFetching();
  }

  @JsonIgnore
  public void setDisableBrowserFetching(Boolean disableBrowserFetching) {
    this.check.setDisableBrowserFetching(disableBrowserFetching);
  }

  @JsonIgnore
  public List<IBrowsersInfo> getBrowsersInfo() {
    return check.getRenderers();
  }

  @JsonIgnore
  public void setBrowsersInfo(List<IBrowsersInfo> browsersInfo) {
    this.check.setRenderers(browsersInfo);
  }

  @JsonIgnore
  public DebugScreenshotHandlerDto getDebugScreenshots() {
    return screenshot.getDebugImages();
  }

  @JsonIgnore
  public void setDebugScreenshots(DebugScreenshotHandlerDto debugScreenshots) {
    this.screenshot.setDebugImages(debugScreenshots);
  }

  @JsonIgnore
  public Boolean getDisabled() {
    return open.getDisabled();
  }

  @JsonIgnore
  public void setDisabled(Boolean disabled) {
    this.open.setDisabled(disabled);
  }

  @JsonIgnore
  public Integer getConnectionTimeout() {
    return open.getConnectionTimeout();
  }

  @JsonIgnore
  public void setConnectionTimeout(Integer connectionTimeout) {
    this.open.setConnectionTimeout(connectionTimeout);
  }

  @JsonIgnore
  public Boolean getRemoveSession() {
    return open.getRemoveSession();
  }

  @JsonIgnore
  public void setRemoveSession(Boolean removeSession) {
    this.open.setRemoveSession(removeSession);
  }

  @JsonIgnore
  public String getDisplayName() {
    return open.getDisplayName();
  }

  @JsonIgnore
  public void setDisplayName(String displayName) {
    this.open.setDisplayName(displayName);
  }

  @JsonIgnore
  public List<CustomPropertyDto> getProperties() {
    return open.getProperties();
  }

  @JsonIgnore
  public void setProperties(List<CustomPropertyDto> properties) {
    this.open.setProperties(properties);
  }

  @JsonIgnore
  public String getHostAppInfo() {
    return open.getEnvironment().getHostingAppInfo();
  }

  @JsonIgnore
  public void setHostAppInfo(String hostAppInfo) {
    this.open.getEnvironment().setHostingAppInfo(hostAppInfo);
  }

  @JsonIgnore
  public String getHostOSInfo() {
    return open.getEnvironment().getOsInfo();
  }

  @JsonIgnore
  public void setHostOSInfo(String hostOSInfo) {
    this.open.getEnvironment().setOsInfo(hostOSInfo);
  }

  @JsonIgnore
  public Boolean getCompareWithParentBranch() {
    return open.getCompareWithParentBranch();
  }

  @JsonIgnore
  public void setCompareWithParentBranch(Boolean compareWithParentBranch) {
    this.open.setCompareWithParentBranch(compareWithParentBranch);
  }

  @JsonIgnore
  public Boolean getIgnoreBaseline() {
    return open.getIgnoreBaseline();
  }

  @JsonIgnore
  public void setIgnoreBaseline(Boolean ignoreBaseline) {
    this.open.setIgnoreBaseline(ignoreBaseline);
  }

  @JsonIgnore
  public Boolean getDontCloseBatches() {
    return open.getKeepBatchOpen();
  }

  @JsonIgnore
  public void setDontCloseBatches(Boolean dontCloseBatches) {
    this.open.setKeepBatchOpen(dontCloseBatches);
  }

  @JsonIgnore
  public TRegion getScrollRootElement() {
    return screenshot.getScrollRootElement();
  }

  @JsonIgnore
  public void setScrollRootElement(TRegion scrollRootElement) {
    this.screenshot.setScrollRootElement(scrollRootElement);
  }

  @JsonIgnore
  public ICut getCut() {
    if (screenshot.getNormalization() == null) {
      return null;
    }
    return screenshot.getNormalization().getCut();
  }

  @JsonIgnore
  public void setCut(ICut cut) {
    if (cut == null) {
      return;
    }
    if (screenshot.getNormalization() == null) {
      screenshot.setNormalization(new NormalizationDto());
    }
    this.screenshot.getNormalization().setCut(cut);
  }

  @JsonIgnore
  public Integer getRotation() {
    if (screenshot.getNormalization() == null) {
      return null;
    }
    return screenshot.getNormalization().getRotation();
  }

  @JsonIgnore
  public void setRotation(Integer rotation) {
    if (rotation == null) {
      return;
    }
    if (screenshot.getNormalization() == null) {
      screenshot.setNormalization(new NormalizationDto());
    }
    this.screenshot.getNormalization().setRotation(rotation);
  }

  @JsonIgnore
  public Double getScaleRatio() {
    if (screenshot.getNormalization() == null) {
      return null;
    }
    return screenshot.getNormalization().getScaleRatio();
  }

  @JsonIgnore
  public void setScaleRatio(Double scaleRatio) {
    if (scaleRatio == null) {
      return;
    }
    if (screenshot.getNormalization() == null) {
      screenshot.setNormalization(new NormalizationDto());
    }
    this.screenshot.getNormalization().setScaleRatio(scaleRatio);
  }

  @JsonIgnore
  public Object getLayoutBreakpoints() {
    return check.getLayoutBreakpoints();
  }

  @JsonIgnore
  public void setLayoutBreakpoints(Object layoutBreakpoints) {
    this.check.setLayoutBreakpoints(layoutBreakpoints);
  }

  @JsonIgnore
  public Map<String, Object> getVisualGridOptions() {
    return check.getUfgOptions();
  }

  @JsonIgnore
  public void setVisualGridOptions(Map<String, Object> visualGridOptions) {
    this.check.setUfgOptions(visualGridOptions);
  }

  @JsonIgnore
  public Integer getWaitBeforeCapture() {
    return screenshot.getWaitBeforeCapture();
  }

  @JsonIgnore
  public void setWaitBeforeCapture(Integer waitBeforeCapture) {
    this.screenshot.setWaitBeforeCapture(waitBeforeCapture);
  }

  @JsonIgnore
  public AutProxyDto getAutProxy() {
    return check.getAutProxy();
  }

  @JsonIgnore
  public void setAutProxy(AutProxyDto autProxy) {
    this.check.setAutProxy(autProxy);
  }
}
