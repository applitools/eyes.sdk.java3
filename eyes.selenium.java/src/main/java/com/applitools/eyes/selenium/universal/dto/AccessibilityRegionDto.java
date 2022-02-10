package com.applitools.eyes.selenium.universal.dto;

import com.applitools.eyes.serializers.BySerializer;
import com.applitools.eyes.serializers.WebElementSerializer;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

/**
 * accessibility region dto
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AccessibilityRegionDto {
  @JsonSerialize(using = WebElementSerializer.class)
  private WebElement element;

  @JsonSerialize(using = BySerializer.class)
  private By selector;

  private String regionType;

  private RegionDto region;

  public WebElement getElement() {
    return element;
  }

  public void setElement(WebElement element) {
    this.element = element;
  }

  public By getSelector() {
    return selector;
  }

  public void setSelector(By selector) {
    this.selector = selector;
  }

  public String getRegionType() {
    return regionType;
  }

  public void setRegionType(String regionType) {
    this.regionType = regionType;
  }

  public RegionDto getRegion() {
    return region;
  }

  public void setRegion(RegionDto region) {
    this.region = region;
  }
}
