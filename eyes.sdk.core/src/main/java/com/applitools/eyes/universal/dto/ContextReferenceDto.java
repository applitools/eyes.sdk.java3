package com.applitools.eyes.universal.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * context reference dto
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ContextReferenceDto {
  private Object frame;
  private TRegion scrollRootElement;

  public Object getFrame() {
    return frame;
  }

  public void setFrame(Object frame) {
    this.frame = frame;
  }

  public TRegion getScrollRootElement() {
    return scrollRootElement;
  }

  public void setScrollRootElement(TRegion scrollRootElement) {
    this.scrollRootElement = scrollRootElement;
  }
}
