package com.applitools.eyes.selenium.universal.dto;

/**
 * element accessibility region dto
 */
public class ElementAccessibilityRegionDto extends TAccessibilityRegion {
  private ElementRegionDto region;

  public ElementRegionDto getRegion() {
    return region;
  }

  public void setRegion(ElementRegionDto region) {
    this.region = region;
  }
}
