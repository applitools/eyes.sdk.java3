package com.applitools.eyes.universal.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * selector accessibility region dto
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SelectorAccessibilityRegionDto extends TAccessibilityRegion {
  private SelectorRegionDto region;

  public SelectorRegionDto getRegion() {
    return region;
  }

  public void setRegion(SelectorRegionDto region) {
    this.region = region;
  }

}
