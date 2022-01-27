package com.applitools.eyes.selenium.universal.dto;

/**
 * scroll root element dto
 */
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
