package com.applitools.eyes.selenium.universal.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * accessibility region by rectangle dto
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AccessibilityRegionByRectangleDto {
  private RegionDto region;
  private String type;

  public RegionDto getRegion() {
    return region;
  }

  public void setRegion(RegionDto region) {
    this.region = region;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }
}
