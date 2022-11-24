package com.applitools.eyes.universal.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * visual locator settings dto
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
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
