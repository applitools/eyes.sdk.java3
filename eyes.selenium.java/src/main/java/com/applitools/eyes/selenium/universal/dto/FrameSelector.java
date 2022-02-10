package com.applitools.eyes.selenium.universal.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * frame selector
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FrameSelector implements IFrame {
  private SelectorRegionDto selector;

  public FrameSelector() {
  }

  public FrameSelector(SelectorRegionDto selector) {
    this.selector = selector;
  }

  public SelectorRegionDto getSelector() {
    return selector;
  }

  public void setSelector(SelectorRegionDto selector) {
    this.selector = selector;
  }
}
