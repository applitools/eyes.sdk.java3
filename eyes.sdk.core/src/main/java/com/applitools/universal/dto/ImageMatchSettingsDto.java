package com.applitools.universal.dto;

import java.util.List;

/**
 * image match settings
 */
public class ImageMatchSettingsDto {
  private String matchLevel;
  private ExactMatchSettingsDto exact;
  private Boolean ignoreCaret;
  private List<RegionDto> ignoreRegions;
  private List<RegionDto> layoutRegions;
  private List<RegionDto> strictRegions;
  private List<RegionDto> contentRegions;
  private List<FloatingMatchSettingsDto> floatingMatchSettings;
  private Boolean useDom;
  private Boolean enablePatterns;
  private Boolean ignoreDisplacements;
  private List<AccessibilityRegionByRectangleDto> accessibility;
  private AccessibilitySettingsDto accessibilitySettings;

  public String getMatchLevel() {
    return matchLevel;
  }

  public void setMatchLevel(String matchLevel) {
    this.matchLevel = matchLevel;
  }

  public ExactMatchSettingsDto getExact() {
    return exact;
  }

  public void setExact(ExactMatchSettingsDto exact) {
    this.exact = exact;
  }

  public Boolean getIgnoreCaret() {
    return ignoreCaret;
  }

  public void setIgnoreCaret(Boolean ignoreCaret) {
    this.ignoreCaret = ignoreCaret;
  }

  public List<RegionDto> getIgnoreRegions() {
    return ignoreRegions;
  }

  public void setIgnoreRegions(List<RegionDto> ignoreRegions) {
    this.ignoreRegions = ignoreRegions;
  }

  public List<RegionDto> getLayoutRegions() {
    return layoutRegions;
  }

  public void setLayoutRegions(List<RegionDto> layoutRegions) {
    this.layoutRegions = layoutRegions;
  }

  public List<RegionDto> getStrictRegions() {
    return strictRegions;
  }

  public void setStrictRegions(List<RegionDto> strictRegions) {
    this.strictRegions = strictRegions;
  }

  public List<RegionDto> getContentRegions() {
    return contentRegions;
  }

  public void setContentRegions(List<RegionDto> contentRegions) {
    this.contentRegions = contentRegions;
  }

  public List<FloatingMatchSettingsDto> getFloatingMatchSettings() {
    return floatingMatchSettings;
  }

  public void setFloatingMatchSettings(List<FloatingMatchSettingsDto> floatingMatchSettings) {
    this.floatingMatchSettings = floatingMatchSettings;
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

  public Boolean getIgnoreDisplacements() {
    return ignoreDisplacements;
  }

  public void setIgnoreDisplacements(Boolean ignoreDisplacements) {
    this.ignoreDisplacements = ignoreDisplacements;
  }

  public List<AccessibilityRegionByRectangleDto> getAccessibility() {
    return accessibility;
  }

  public void setAccessibility(List<AccessibilityRegionByRectangleDto> accessibility) {
    this.accessibility = accessibility;
  }

  public AccessibilitySettingsDto getAccessibilitySettings() {
    return accessibilitySettings;
  }

  public void setAccessibilitySettings(AccessibilitySettingsDto accessibilitySettings) {
    this.accessibilitySettings = accessibilitySettings;
  }
}
