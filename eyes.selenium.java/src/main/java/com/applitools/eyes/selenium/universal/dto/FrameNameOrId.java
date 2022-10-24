package com.applitools.eyes.selenium.universal.dto;

import com.applitools.eyes.universal.dto.IFrame;
import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * frameNameOrId
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FrameNameOrId implements IFrame {
  private String frame;

  public FrameNameOrId(String frame) {
    this.frame = frame;
  }

  public FrameNameOrId() {
  }

  public String getFrame() {
    return frame;
  }

  public void setFrame(String frame) {
    this.frame = frame;
  }
}
