package com.applitools.universal.dto;

/**
 * Accessibility settings dto
 */
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
