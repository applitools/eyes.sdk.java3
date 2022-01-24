package com.applitools.eyes.selenium.universal.dto;

import java.util.List;
import java.util.Map;

import com.applitools.universal.dto.AccessibilitySettingsDto;
import com.applitools.universal.dto.ExactMatchSettingsDto;
import com.applitools.universal.dto.RegionDto;
import com.applitools.universal.dto.VisualGridOptionDto;

/**
 * check settings dto
 */
public class CheckSettingsDto {
  // CheckSettings
  private String name;
  private Boolean disableBrowserFetching;
  private List<Integer> layoutBreakpoints;
  private List<VisualGridOptionDto> visualGridOptions;
  private Map<String, String> hooks;
  private String renderId;
  private String variationGroupId;
  private Integer timeout;

  // MatchSettings
  private ExactMatchSettingsDto exact;
  private String matchLevel;
  private Boolean sendDom;
  private Boolean useDom;
  private Boolean enablePatterns;
  private Boolean ignoreCaret;
  private Boolean ignoreDisplacements;
  private AccessibilitySettingsDto accessibilitySettings;
  private List<SimpleRegionDto> ignoreRegions;
  private List<SimpleRegionDto> layoutRegions;
  private List<SimpleRegionDto> strictRegions;
  private List<SimpleRegionDto> contentRegions;
  private List<FloatingRegionDto> floatingRegions;





  private RegionDto targetRegion;


  // TODO accessibility regions
  // TODO region
  // TODO frames
  private ScrollRootElementDto scrollRootElement; // TODO scroll root element
  private Boolean fully; // TODO map stitchContent to fully

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public List<Integer> getLayoutBreakpoints() {
    return layoutBreakpoints;
  }

  public void setLayoutBreakpoints(List<Integer> layoutBreakpoints) {
    this.layoutBreakpoints = layoutBreakpoints;
  }

  public List<VisualGridOptionDto> getVisualGridOptions() {
    return visualGridOptions;
  }

  public void setVisualGridOptions(List<VisualGridOptionDto> visualGridOptions) {
    this.visualGridOptions = visualGridOptions;
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

  public RegionDto getTargetRegion() {
    return targetRegion;
  }

  public void setTargetRegion(RegionDto targetRegion) {
    this.targetRegion = targetRegion;
  }

  public List<SimpleRegionDto> getIgnoreRegions() {
    return ignoreRegions;
  }

  public void setIgnoreRegions(List<SimpleRegionDto> ignoreRegions) {
    this.ignoreRegions = ignoreRegions;
  }

  public List<SimpleRegionDto> getLayoutRegions() {
    return layoutRegions;
  }

  public void setLayoutRegions(List<SimpleRegionDto> layoutRegions) {
    this.layoutRegions = layoutRegions;
  }

  public List<SimpleRegionDto> getStrictRegions() {
    return strictRegions;
  }

  public void setStrictRegions(List<SimpleRegionDto> strictRegions) {
    this.strictRegions = strictRegions;
  }

  public List<SimpleRegionDto> getContentRegions() {
    return contentRegions;
  }

  public void setContentRegions(List<SimpleRegionDto> contentRegions) {
    this.contentRegions = contentRegions;
  }

  public List<FloatingRegionDto> getFloatingRegions() {
    return floatingRegions;
  }

  public void setFloatingRegions(List<FloatingRegionDto> floatingRegions) {
    this.floatingRegions = floatingRegions;
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

  public ScrollRootElementDto getScrollRootElement() {
    return scrollRootElement;
  }

  public void setScrollRootElement(ScrollRootElementDto scrollRootElement) {
    this.scrollRootElement = scrollRootElement;
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
}
