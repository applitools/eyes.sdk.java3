package com.applitools.eyes.selenium.universal.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * scroll root element dto
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ScrollRootElementDto {
  private SelectorRegionDto selector;
  private ElementRegionDto element;

  public SelectorRegionDto getSelector() {
    return selector;
  }

  public void setSelector(SelectorRegionDto selector) {
    this.selector = selector;
  }

  public ElementRegionDto getElement() {
    return element;
  }

  public void setElement(ElementRegionDto element) {
    this.element = element;
  }
}
