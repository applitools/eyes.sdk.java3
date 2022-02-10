package com.applitools.eyes.selenium.universal.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * element accessibility region dto
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ElementAccessibilityRegionDto extends TAccessibilityRegion {
  private ElementRegionDto region;

  public ElementRegionDto getRegion() {
    return region;
  }

  public void setRegion(ElementRegionDto region) {
    this.region = region;
  }
}
