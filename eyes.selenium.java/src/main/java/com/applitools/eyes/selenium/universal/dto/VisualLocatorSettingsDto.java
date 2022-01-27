package com.applitools.eyes.selenium.universal.dto;

import java.util.List;

/**
 * visual locator settings dto
 */
public class VisualLocatorSettingsDto {
  private List<String> names;
  private Boolean firstOnly;

  public List<String> getNames() {
    return names;
  }

  public void setNames(List<String> names) {
    this.names = names;
  }

  public Boolean getFirstOnly() {
    return firstOnly;
  }

  public void setFirstOnly(Boolean firstOnly) {
    this.firstOnly = firstOnly;
  }

}
