package com.applitools.eyes.universal.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * rectangle accessibility region dto
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RectangleAccessibilityRegionDto extends TAccessibilityRegion {
  private RectangleRegionDto region;

  public RectangleRegionDto getRegion() {
    return region;
  }

  public void setRegion(RectangleRegionDto region) {
    this.region = region;
  }
}
