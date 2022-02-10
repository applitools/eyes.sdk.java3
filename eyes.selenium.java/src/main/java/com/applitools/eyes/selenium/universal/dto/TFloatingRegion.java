package com.applitools.eyes.selenium.universal.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * TFloating region
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public abstract class TFloatingRegion {
  protected Integer maxUpOffset;
  protected Integer maxDownOffset;
  protected Integer maxLeftOffset;
  protected Integer maxRightOffset;

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
}
