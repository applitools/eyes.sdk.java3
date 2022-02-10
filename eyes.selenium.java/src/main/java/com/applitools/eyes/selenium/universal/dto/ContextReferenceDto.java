package com.applitools.eyes.selenium.universal.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * context reference dto
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ContextReferenceDto {
  private IFrame frame; // IFrame
  private ScrollRootElementDto scrollRootElement;

  public IFrame getFrame() {
    return frame;
  }

  public void setFrame(IFrame frame) {
    this.frame = frame;
  }

  public ScrollRootElementDto getScrollRootElement() {
    return scrollRootElement;
  }

  public void setScrollRootElement(ScrollRootElementDto scrollRootElement) {
    this.scrollRootElement = scrollRootElement;
  }
}
