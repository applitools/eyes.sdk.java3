package com.applitools.eyes.universal.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * floating match settings dto
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FloatingMatchSettingsDto {
  private RegionDto region;
  private Integer maxUpOffset;
  private Integer maxDownOffset;
  private Integer maxLeftOffset;
  private Integer maxRightOffset;

  public Integer getMaxUpOffset() {
    return maxUpOffset;
  }

  public void setMaxUpOffset(Integer maxUpOffset) {
    this.maxUpOffset = maxUpOffset;
  }

  public Integer getMaxDownOffset() {
    return maxDownOffset;
  }

  public void setMaxDownOffset(Integer maxDownOffset) {
    this.maxDownOffset = maxDownOffset;
  }

  public Integer getMaxLeftOffset() {
    return maxLeftOffset;
  }

  public void setMaxLeftOffset(Integer maxLeftOffset) {
    this.maxLeftOffset = maxLeftOffset;
  }

  public Integer getMaxRightOffset() {
    return maxRightOffset;
  }

  public void setMaxRightOffset(Integer maxRightOffset) {
    this.maxRightOffset = maxRightOffset;
  }

  public RegionDto getRegion() {
    return region;
  }

  public void setRegion(RegionDto region) {
    this.region = region;
  }
}
