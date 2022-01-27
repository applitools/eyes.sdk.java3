package com.applitools.eyes.selenium.universal.dto;

/**
 * rectangle accessibility region dto
 */
public class RectangleAccessibilityRegionDto extends TAccessibilityRegion {
  private RectangleRegionDto region;

  public RectangleRegionDto getRegion() {
    return region;
  }

  public void setRegion(RectangleRegionDto region) {
    this.region = region;
  }
}
