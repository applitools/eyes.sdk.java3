package com.applitools.eyes.selenium.universal.dto;

/**
 * element floating region dto
 */
public class ElementFloatingRegionDto extends TFloatingRegion {

  private ElementRegionDto region;

  public ElementRegionDto getRegion() {
    return region;
  }

  public void setRegion(ElementRegionDto region) {
    this.region = region;
  }

}
