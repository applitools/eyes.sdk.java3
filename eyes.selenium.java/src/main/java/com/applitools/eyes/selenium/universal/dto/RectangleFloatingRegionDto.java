package com.applitools.eyes.selenium.universal.dto;

/**
 * rectangle floating region dto
 */
public class RectangleFloatingRegionDto extends TFloatingRegion {
  private RectangleRegionDto region;

  public RectangleRegionDto getRegion() {
    return region;
  }

  public void setRegion(RectangleRegionDto region) {
    this.region = region;
  }
}
