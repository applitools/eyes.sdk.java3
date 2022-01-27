package com.applitools.eyes.selenium.universal.dto;

/**
 * selector floating region dto
 */
public class SelectorFloatingRegionDto extends TFloatingRegion {

  private SelectorRegionDto region;

  public SelectorRegionDto getRegion() {
    return region;
  }

  public void setRegion(SelectorRegionDto region) {
    this.region = region;
  }

}
