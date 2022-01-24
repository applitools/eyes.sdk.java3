package com.applitools.eyes.selenium.universal.dto;

/**
 * context reference dto
 */
public class ContextReferenceDto {
  private FrameLocatorDto frame;
  private ScrollRootElementDto scrollRootElement;

  public FrameLocatorDto getFrame() {
    return frame;
  }

  public void setFrame(FrameLocatorDto frame) {
    this.frame = frame;
  }

  public ScrollRootElementDto getScrollRootElement() {
    return scrollRootElement;
  }

  public void setScrollRootElement(ScrollRootElementDto scrollRootElement) {
    this.scrollRootElement = scrollRootElement;
  }
}
