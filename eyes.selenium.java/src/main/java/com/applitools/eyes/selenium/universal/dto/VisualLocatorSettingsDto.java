package com.applitools.eyes.selenium.universal.dto;

import java.util.List;

/**
 * visual locator settings dto
 */
public class VisualLocatorSettingsDto {
  private List<String> locatorNames;
  private Boolean firstOnly;

  public List<String> getLocatorNames() {
    return locatorNames;
  }

  public void setLocatorNames(List<String> locatorNames) {
    this.locatorNames = locatorNames;
  }

  public Boolean getFirstOnly() {
    return firstOnly;
  }

  public void setFirstOnly(Boolean firstOnly) {
    this.firstOnly = firstOnly;
  }

}
