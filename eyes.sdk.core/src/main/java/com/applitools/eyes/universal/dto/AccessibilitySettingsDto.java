package com.applitools.eyes.universal.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Accessibility settings dto
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AccessibilitySettingsDto {
  private String level; // 'AA' | 'AAA'
  private String guidelinesVersion; // 'WCAG_2_0' | 'WCAG_2_1'

  public String getLevel() {
    return level;
  }

  public void setLevel(String level) {
    this.level = level;
  }

  public String getGuidelinesVersion() {
    return guidelinesVersion;
  }

  public void setGuidelinesVersion(String version) {
    this.guidelinesVersion = version;
  }
}
