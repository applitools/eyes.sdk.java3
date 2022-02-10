package com.applitools.eyes.selenium.universal.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * selector region dto
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SelectorRegionDto extends TRegion{

  private String type;
  private String selector;

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
}
