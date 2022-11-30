package com.applitools.eyes.universal.dto;

import com.applitools.eyes.Padding;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * TFloating region
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public abstract class TFloatingRegion {
  @JsonProperty("offset")
  protected Padding offset = new Padding();
  protected String regionId;

  @JsonIgnore
  public Integer getMaxUpOffset() {
    return offset.getTop();
  }

  @JsonIgnore
  public void setMaxUpOffset(Integer maxUpOffset) {
    this.offset = offset.setTop(maxUpOffset);
  }

  @JsonIgnore
  public Integer getMaxDownOffset() {
    return offset.getBottom();
  }

  @JsonIgnore
  public void setMaxDownOffset(Integer maxDownOffset) {
    this.offset = offset.setBottom(maxDownOffset);
  }

  @JsonIgnore
  public Integer getMaxLeftOffset() {
    return offset.getLeft();
  }

  @JsonIgnore
  public void setMaxLeftOffset(Integer maxLeftOffset) {
    this.offset = offset.setLeft(maxLeftOffset);
  }

  @JsonIgnore
  public Integer getMaxRightOffset() {
    return offset.getRight();
  }

  @JsonIgnore
  public void setMaxRightOffset(Integer maxRightOffset) {
    this.offset = offset.setRight(maxRightOffset);
  }

  public String getRegionId() {
    return regionId;
  }

  public void setRegionId(String regionId) {
    this.regionId = regionId;
  }
}
