package com.applitools.eyes.selenium.universal.dto;

import com.applitools.eyes.universal.dto.IFrame;
import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * frame element
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FrameElement implements IFrame {
  private ElementRegionDto element;

  public FrameElement() {
  }

  public FrameElement(ElementRegionDto element) {
    this.element = element;
  }

  public ElementRegionDto getElement() {
    return element;
  }

  public void setElement(ElementRegionDto element) {
    this.element = element;
  }
}
