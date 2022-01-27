package com.applitools.eyes.selenium.universal.dto;

/**
 * selector accessibility region dto
 */
public class SelectorAccessibilityRegionDto extends TAccessibilityRegion {
  private SelectorRegionDto region;

  public SelectorRegionDto getRegion() {
    return region;
  }

  public void setRegion(SelectorRegionDto region) {
    this.region = region;
  }

}
