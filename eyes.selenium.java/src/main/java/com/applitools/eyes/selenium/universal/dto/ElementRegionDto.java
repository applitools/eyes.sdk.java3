package com.applitools.eyes.selenium.universal.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * element region dto
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ElementRegionDto extends TRegion {
  private String elementId;

  public String getElementId() {
    return elementId;
  }

  public void setElementId(String elementId) {
    this.elementId = elementId;
  }

}
