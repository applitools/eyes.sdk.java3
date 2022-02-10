package com.applitools.eyes.selenium.universal.dto;

import com.applitools.eyes.serializers.WebElementSerializer;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.openqa.selenium.WebElement;

/**
 * simple region dto
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SimpleRegionDto {
  private String type;
  private String selector;
  private String elementId;

  @JsonSerialize(using = WebElementSerializer.class)
  private WebElement element;


  private RegionDto region;

  public WebElement getElement() {
    return element;
  }

  public void setElement(WebElement element) {
    this.element = element;
  }


  public RegionDto getRegion() {
    return region;
  }

  public void setRegion(RegionDto region) {
    this.region = region;
  }

}
