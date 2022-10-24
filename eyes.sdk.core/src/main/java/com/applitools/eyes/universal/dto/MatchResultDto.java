package com.applitools.eyes.universal.dto;

/**
 * match result dto
 */
public class MatchResultDto {
  private Boolean asExpected;
  private Long windowId;

  public MatchResultDto() {
  }

  public MatchResultDto(Boolean asExpected, Long windowId) {
    this.asExpected = asExpected;
    this.windowId = windowId;
  }

  public Boolean getAsExpected() {
    return asExpected;
  }

  public void setAsExpected(Boolean asExpected) {
    this.asExpected = asExpected;
  }

  public Long getWindowId() {
    return windowId;
  }

  public void setWindowId(Long windowId) {
    this.windowId = windowId;
  }

  @Override
  public String toString() {
    return "MatchResultDto{" +
        "asExpected=" + asExpected +
        ", windowId=" + windowId +
        '}';
  }
}
