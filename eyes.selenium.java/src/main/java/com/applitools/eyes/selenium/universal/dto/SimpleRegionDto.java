package com.applitools.eyes.selenium.universal.dto;

import com.applitools.eyes.serializers.BySerializer;
import com.applitools.eyes.serializers.WebElementSerializer;
import com.applitools.universal.dto.RegionDto;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

/**
 * simple region dto
 */
public class SimpleRegionDto {
  @JsonSerialize(using = WebElementSerializer.class)
  private WebElement element;

  @JsonSerialize(using = BySerializer.class)
  private By selector;

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

  public RegionDto getRegion() {
    return region;
  }

  public void setRegion(RegionDto region) {
    this.region = region;
  }

  @Override
  public String toString() {
    return "SimpleRegionDto{" +
        "element=" + element +
        ", selector=" + selector +
        ", region=" + region +
        '}';
  }
}
