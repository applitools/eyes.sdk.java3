package com.applitools.eyes.selenium.universal.dto;

import java.util.List;
import java.util.Map;

import com.applitools.eyes.LazyLoadOptions;
import com.fasterxml.jackson.annotation.JsonInclude;


/**
 * check settings dto
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CheckSettingsDto {
  // CheckSettings
  private String name;
  private Boolean disableBrowserFetching;
  private Object layoutBreakpoints;
  private Map<String, Object> visualGridOptions;
  private Map<String, String> hooks;

  private String renderId;
  private String variationGroupId;
  private Integer timeout;
  private Integer waitBeforeCapture;

  // MatchSettings
  private ExactMatchSettingsDto exact;
  private String matchLevel;
  private Boolean sendDom;
  private Boolean useDom;
  private Boolean enablePatterns;
  private Boolean ignoreCaret;
  private Boolean ignoreDisplacements;
  private AccessibilitySettingsDto accessibilitySettings;

  private List<CodedRegionReference> ignoreRegions;
  private List<CodedRegionReference> layoutRegions;
  private List<CodedRegionReference> strictRegions;
  private List<CodedRegionReference> contentRegions;

  private Object floatingRegions;
  private Object accessibilityRegions;

  // ScreenshotSettings
  private TRegion region;
  private Object frames;
  private TRegion scrollRootElement;
  private Boolean fully;
  private String pageId;
  private LazyLoadOptions lazyLoad;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Object getLayoutBreakpoints() {
    return layoutBreakpoints;
  }

  public void setLayoutBreakpoints(Object layoutBreakpoints) {
    this.layoutBreakpoints = layoutBreakpoints;
  }

  public Map<String, String> getHooks() {
    return hooks;
  }

  public void setHooks(Map<String, String> hooks) {
    this.hooks = hooks;
  }

  public String getVariationGroupId() {
    return variationGroupId;
  }

  public void setVariationGroupId(String variationGroupId) {
    this.variationGroupId = variationGroupId;
  }

  public Integer getTimeout() {
    return timeout;
  }

  public void setTimeout(Integer timeout) {
    this.timeout = timeout;
  }

  public String getMatchLevel() {
    return matchLevel;
  }

  public void setMatchLevel(String matchLevel) {
    this.matchLevel = matchLevel;
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

  public Boolean getIgnoreDisplacements() {
    return ignoreDisplacements;
  }

  public void setIgnoreDisplacements(Boolean ignoreDisplacements) {
    this.ignoreDisplacements = ignoreDisplacements;
  }

  public Boolean getDisableBrowserFetching() {
    return disableBrowserFetching;
  }

  public void setDisableBrowserFetching(Boolean disableBrowserFetching) {
    this.disableBrowserFetching = disableBrowserFetching;
  }

  public String getRenderId() {
    return renderId;
  }

  public void setRenderId(String renderId) {
    this.renderId = renderId;
  }


  public Boolean getFully() {
    return fully;
  }

  public void setFully(Boolean fully) {
    this.fully = fully;
  }

  public ExactMatchSettingsDto getExact() {
    return exact;
  }

  public void setExact(ExactMatchSettingsDto exact) {
    this.exact = exact;
  }

  public AccessibilitySettingsDto getAccessibilitySettings() {
    return accessibilitySettings;
  }

  public void setAccessibilitySettings(AccessibilitySettingsDto accessibilitySettings) {
    this.accessibilitySettings = accessibilitySettings;
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

  public Object getFrames() {
    return frames;
  }

  public void setFrames(List<ContextReferenceDto> frames) {
    this.frames = frames;
  }


  public void setAccessibilityRegions(List<TAccessibilityRegion> accessibilityRegions) {
    this.accessibilityRegions = accessibilityRegions;
  }

  public TRegion getRegion() {
    return region;
  }

  public void setRegion(TRegion region) {
    this.region = region;
  }

  public TRegion getScrollRootElement() {
    return scrollRootElement;
  }

  public void setScrollRootElement(TRegion scrollRootElement) {
    this.scrollRootElement = scrollRootElement;
  }

  public Map<String, Object> getVisualGridOptions() {
    return visualGridOptions;
  }

  public void setVisualGridOptions(Map<String, Object> visualGridOptions) {
    this.visualGridOptions = visualGridOptions;
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

  public void setFrames(Object frames) {
    this.frames = frames;
  }

  public String getPageId() {
    return pageId;
  }

  public void setPageId(String pageId) {
    this.pageId = pageId;
  }

  public LazyLoadOptions getLazyLoad() {
    return lazyLoad;
  }

  public void setLazyLoad(LazyLoadOptions lazyLoad) {
    this.lazyLoad = lazyLoad;
  }

  public Integer getWaitBeforeCapture() {
    return waitBeforeCapture;
  }

  public void setWaitBeforeCapture(Integer waitBeforeCapture) {
    this.waitBeforeCapture = waitBeforeCapture;
  }
}
