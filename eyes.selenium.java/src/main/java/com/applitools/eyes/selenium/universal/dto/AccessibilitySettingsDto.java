package com.applitools.eyes.selenium.universal.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Accessibility settings dto
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AccessibilitySettingsDto {
  private String level;
  private String version; // TODO version or guidelinesVersion ?

  public String getLevel() {
    return level;
  }

  public void setLevel(String level) {
    this.level = level;
  }

  public String getVersion() {
    return version;
  }

  public void setVersion(String version) {
    this.version = version;
  }
}
