package com.applitools.eyes.selenium.universal.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * image match settings
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MatchSettingsDto {
  private ExactMatchSettingsDto exact;
  private String matchLevel;
  private Boolean sendDom;
  private Boolean useDom;
  private Boolean enablePatterns;
  private Boolean ignoreCaret;
  private Boolean ignoreDisplacements;
  private AccessibilitySettingsDto accessibilitySettings;

  private List<TRegion> ignoreRegions;
  private List<TRegion> layoutRegions;
  private List<TRegion> strictRegions;
  private List<TRegion> contentRegions;

  private Object floatingRegions; // (TRegion | FloatingRegion<TRegion>)[]
  private Object accessibilityRegions; //  (TRegion | AccessibilityRegion<TRegion>)[]

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

  public AccessibilitySettingsDto getAccessibilitySettings() {
    return accessibilitySettings;
  }

  public void setAccessibilitySettings(AccessibilitySettingsDto accessibilitySettings) {
    this.accessibilitySettings = accessibilitySettings;
  }

  public Boolean getSendDom() {
    return sendDom;
  }

  public void setSendDom(Boolean sendDom) {
    this.sendDom = sendDom;
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

  public List<TRegion> getIgnoreRegions() {
    return ignoreRegions;
  }

  public void setIgnoreRegions(List<TRegion> ignoreRegions) {
    this.ignoreRegions = ignoreRegions;
  }

  public List<TRegion> getLayoutRegions() {
    return layoutRegions;
  }

  public void setLayoutRegions(List<TRegion> layoutRegions) {
    this.layoutRegions = layoutRegions;
  }

  public List<TRegion> getStrictRegions() {
    return strictRegions;
  }

  public void setStrictRegions(List<TRegion> strictRegions) {
    this.strictRegions = strictRegions;
  }

  public List<TRegion> getContentRegions() {
    return contentRegions;
  }

  public void setContentRegions(List<TRegion> contentRegions) {
    this.contentRegions = contentRegions;
  }
}
