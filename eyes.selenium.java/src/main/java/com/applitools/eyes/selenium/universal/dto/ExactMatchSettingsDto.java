package com.applitools.eyes.selenium.universal.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * exact match settings
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ExactMatchSettingsDto {
  private Integer minDiffIntensity;
  private Integer minDiffWidth;
  private Integer minDiffHeight;
  private Float matchThreshold;

  public Integer getMinDiffIntensity() {
    return minDiffIntensity;
  }

  public void setMinDiffIntensity(Integer minDiffIntensity) {
    this.minDiffIntensity = minDiffIntensity;
  }

  public Integer getMinDiffWidth() {
    return minDiffWidth;
  }

  public void setMinDiffWidth(Integer minDiffWidth) {
    this.minDiffWidth = minDiffWidth;
  }

  public Integer getMinDiffHeight() {
    return minDiffHeight;
  }

  public void setMinDiffHeight(Integer minDiffHeight) {
    this.minDiffHeight = minDiffHeight;
  }

  public Float getMatchThreshold() {
    return matchThreshold;
  }

  public void setMatchThreshold(Float matchThreshold) {
    this.matchThreshold = matchThreshold;
  }

  @Override
  public String toString() {
    return "ExactMatchSettingsDto{" +
        "minDiffIntensity=" + minDiffIntensity +
        ", minDiffWidth=" + minDiffWidth +
        ", minDiffHeight=" + minDiffHeight +
        ", matchThreshold=" + matchThreshold +
        '}';
  }
}
