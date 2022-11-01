package com.applitools.eyes.selenium.universal.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * selector region dto
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SelectorRegionDto extends TRegion {

  private String selector;
  private String type;
  private SelectorRegionDto fallback;
  private SelectorRegionDto child;

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getSelector() {
    return selector;
  }

  public void setSelector(String selector) {
    this.selector = selector;
  }

  public SelectorRegionDto getFallback() {
    return fallback;
  }

  public void setFallback(SelectorRegionDto fallback) {
    this.fallback = fallback;
  }

  public SelectorRegionDto getChild() {
    return child;
  }

  public void setChild(SelectorRegionDto child) {
    this.child = child;
  }
}
