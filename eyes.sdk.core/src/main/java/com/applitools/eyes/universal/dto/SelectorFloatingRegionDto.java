package com.applitools.eyes.universal.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * selector floating region dto
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SelectorFloatingRegionDto extends TFloatingRegion {

  private SelectorRegionDto region;

  public SelectorRegionDto getRegion() {
    return region;
  }

  public void setRegion(SelectorRegionDto region) {
    this.region = region;
  }

}
