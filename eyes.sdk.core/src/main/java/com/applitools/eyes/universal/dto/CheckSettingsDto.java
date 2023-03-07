package com.applitools.eyes.universal.dto;

import java.util.List;
import java.util.Map;

import com.applitools.eyes.DensityMetrics;
import com.applitools.eyes.LazyLoadOptions;
import com.applitools.eyes.StitchOverlap;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;


/**
 * check settings dto
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CheckSettingsDto {

  // screenshot
  private TRegion region;
  private Object frames;
  private Object webview;
  private Boolean fully;
  private TRegion scrollRootElement;
  private String stitchMode; // "CSS" | "Scroll"
  private Boolean hideScrollbars; // this is not camel case
  private Boolean hideCaret;
  private StitchOverlap overlap;
  private Integer waitBeforeCapture;
  private LazyLoadOptions lazyLoad;
  private Boolean ignoreDisplacements;
  private NormalizationDto normalization;
  private DebugScreenshotHandlerDto debugImages;

  // check
  private String name;
  private String pageId;
  private List<CodedRegionReference> ignoreRegions;
  private List<CodedRegionReference> layoutRegions;
  private List<CodedRegionReference> strictRegions;
  private List<CodedRegionReference> contentRegions;
  private Object floatingRegions;
  private Object accessibilityRegions;
  private AccessibilitySettingsDto accessibilitySettings;
  private String matchLevel;
  private Integer retryTimeout;
  private Boolean sendDom;
  private Boolean useDom;
  private Boolean enablePatterns;
  private Boolean ignoreCaret;
  private Map<String, Object> ufgOptions;
  private Object layoutBreakpoints;
  private Boolean disableBrowserFetching;
  private AutProxyDto autProxy;
  private Map<String, String> hooks;
  private List<IBrowsersInfo> renderers;
  private String userCommandId;
  private DensityMetrics densityMetrics;
  @JsonIgnore
  private String type;

  public CheckSettingsDto() {

  }

  public TRegion getRegion() {
    return region;
  }

  public void setRegion(TRegion region) {
    this.region = region;
  }

  public Object getFrames() {
    return frames;
  }

  public void setFrames(Object frames) {
    this.frames = frames;
  }

  public Boolean getFully() {
    return fully;
  }

  public void setFully(Boolean fully) {
    this.fully = fully;
  }

  public TRegion getScrollRootElement() {
    return scrollRootElement;
  }

  public void setScrollRootElement(TRegion scrollRootElement) {
    this.scrollRootElement = scrollRootElement;
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

  public StitchOverlap getOverlap() {
    return overlap;
  }

  public void setOverlap(StitchOverlap overlap) {
    this.overlap = overlap;
  }

  public Integer getWaitBeforeCapture() {
    return waitBeforeCapture;
  }

  public void setWaitBeforeCapture(Integer waitBeforeCapture) {
    this.waitBeforeCapture = waitBeforeCapture;
  }

  public LazyLoadOptions getLazyLoad() {
    return lazyLoad;
  }

  public void setLazyLoad(LazyLoadOptions lazyLoad) {
    this.lazyLoad = lazyLoad;
  }

  public Boolean getIgnoreDisplacements() {
    return ignoreDisplacements;
  }

  public void setIgnoreDisplacements(Boolean ignoreDisplacements) {
    this.ignoreDisplacements = ignoreDisplacements;
  }

  public NormalizationDto getNormalization() {
    return normalization;
  }

  public void setNormalization(NormalizationDto normalization) {
    this.normalization = normalization;
  }

  public DebugScreenshotHandlerDto getDebugImages() {
    return debugImages;
  }

  public void setDebugImages(DebugScreenshotHandlerDto debugImages) {
    this.debugImages = debugImages;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getPageId() {
    return pageId;
  }

  public void setPageId(String pageId) {
    this.pageId = pageId;
  }

  public List<CodedRegionReference> getIgnoreRegions() {
    return ignoreRegions;
  }

  public void setIgnoreRegions(List<CodedRegionReference> ignoreRegions) {
    this.ignoreRegions = ignoreRegions;
  }

  public List<CodedRegionReference> getLayoutRegions() {
    return layoutRegions;
  }

  public void setLayoutRegions(List<CodedRegionReference> layoutRegions) {
    this.layoutRegions = layoutRegions;
  }

  public List<CodedRegionReference> getStrictRegions() {
    return strictRegions;
  }

  public void setStrictRegions(List<CodedRegionReference> strictRegions) {
    this.strictRegions = strictRegions;
  }

  public List<CodedRegionReference> getContentRegions() {
    return contentRegions;
  }

  public void setContentRegions(List<CodedRegionReference> contentRegions) {
    this.contentRegions = contentRegions;
  }

  public Object getFloatingRegions() {
    return floatingRegions;
  }

  public void setFloatingRegions(Object floatingRegions) {
    this.floatingRegions = floatingRegions;
  }

  public Object getAccessibilityRegions() {
    return accessibilityRegions;
  }

  public void setAccessibilityRegions(Object accessibilityRegions) {
    this.accessibilityRegions = accessibilityRegions;
  }

  public AccessibilitySettingsDto getAccessibilitySettings() {
    return accessibilitySettings;
  }

  public void setAccessibilitySettings(AccessibilitySettingsDto accessibilitySettings) {
    this.accessibilitySettings = accessibilitySettings;
  }

  public String getMatchLevel() {
    return matchLevel;
  }

  public void setMatchLevel(String matchLevel) {
    this.matchLevel = matchLevel;
  }

  public Integer getRetryTimeout() {
    return retryTimeout;
  }

  public void setRetryTimeout(Integer retryTimeout) {
    this.retryTimeout = retryTimeout;
  }

  public Boolean getSendDom() {
    return sendDom;
  }

  public void setSendDom(Boolean sendDom) {
    this.sendDom = sendDom;
  }

  public Boolean getUseDom() {
    return useDom;
  }

  public void setUseDom(Boolean useDom) {
    this.useDom = useDom;
  }

  public Boolean getEnablePatterns() {
    return enablePatterns;
  }

  public void setEnablePatterns(Boolean enablePatterns) {
    this.enablePatterns = enablePatterns;
  }

  public Boolean getIgnoreCaret() {
    return ignoreCaret;
  }

  public void setIgnoreCaret(Boolean ignoreCaret) {
    this.ignoreCaret = ignoreCaret;
  }

  public Map<String, Object> getUfgOptions() {
    return ufgOptions;
  }

  public void setUfgOptions(Map<String, Object> ufgOptions) {
    this.ufgOptions = ufgOptions;
  }

  public Object getLayoutBreakpoints() {
    return layoutBreakpoints;
  }

  public void setLayoutBreakpoints(Object layoutBreakpoints) {
    this.layoutBreakpoints = layoutBreakpoints;
  }

  public Boolean getDisableBrowserFetching() {
    return disableBrowserFetching;
  }

  public void setDisableBrowserFetching(Boolean disableBrowserFetching) {
    this.disableBrowserFetching = disableBrowserFetching;
  }

  public AutProxyDto getAutProxy() {
    return autProxy;
  }

  public void setAutProxy(AutProxyDto autProxy) {
    this.autProxy = autProxy;
  }

  public Map<String, String> getHooks() {
    return hooks;
  }

  public void setHooks(Map<String, String> hooks) {
    this.hooks = hooks;
  }

  public List<IBrowsersInfo> getRenderers() {
    return renderers;
  }

  public void setRenderers(List<IBrowsersInfo> renderers) {
    this.renderers = renderers;
  }

  public void setUserCommandId(String userCommandId) {
    this.userCommandId = userCommandId;
  }

  public String getUserCommandId() {
    return userCommandId;
  }

  public Object getWebview() { return webview; }

  public void setWebview(Object webview) { this.webview = webview; }

  public DensityMetrics getDensityMetrics() {
    return densityMetrics;
  }

  public void setDensityMetrics(DensityMetrics densityMetrics) {
    this.densityMetrics = densityMetrics;
  }

  @JsonIgnore
  public void setType(String type) {
    this.type = type;
  }

  @JsonIgnore
  public String getType() {
    return type;
  }
}