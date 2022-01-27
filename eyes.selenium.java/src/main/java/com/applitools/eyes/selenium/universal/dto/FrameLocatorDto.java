package com.applitools.eyes.selenium.universal.dto;

/**
 * frame locator dto
 */
public class FrameLocatorDto {
  private SelectorRegionDto selector;
  private ElementRegionDto element;
  private String frameNameOrId;
  private Integer frameIndex;

  public String getFrameNameOrId() {
    return frameNameOrId;
  }

  public void setFrameNameOrId(String frameNameOrId) {
    this.frameNameOrId = frameNameOrId;
  }

  public Integer getFrameIndex() {
    return frameIndex;
  }

  public void setFrameIndex(Integer frameIndex) {
    this.frameIndex = frameIndex;
  }

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
