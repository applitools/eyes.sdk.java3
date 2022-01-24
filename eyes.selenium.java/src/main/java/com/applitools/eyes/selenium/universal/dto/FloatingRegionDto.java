package com.applitools.eyes.selenium.universal.dto;

import com.applitools.eyes.serializers.BySerializer;
import com.applitools.eyes.serializers.WebElementSerializer;
import com.applitools.universal.dto.RegionDto;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

/**
 * floating region dto
 */
public class FloatingRegionDto {
  @JsonSerialize(using = WebElementSerializer.class)
  private WebElement element;

  @JsonSerialize(using = BySerializer.class)
  private By selector;

  private RegionDto region;

  private Integer maxUpOffset;
  private Integer maxDownOffset;
  private Integer maxLeftOffset;
  private Integer maxRightOffset;

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

  public Integer getMaxUpOffset() {
    return maxUpOffset;
  }

  public void setMaxUpOffset(Integer maxUpOffset) {
    this.maxUpOffset = maxUpOffset;
  }

  public Integer getMaxDownOffset() {
    return maxDownOffset;
  }

  public void setMaxDownOffset(Integer maxDownOffset) {
    this.maxDownOffset = maxDownOffset;
  }

  public Integer getMaxLeftOffset() {
    return maxLeftOffset;
  }

  public void setMaxLeftOffset(Integer maxLeftOffset) {
    this.maxLeftOffset = maxLeftOffset;
  }

  public Integer getMaxRightOffset() {
    return maxRightOffset;
  }

  public void setMaxRightOffset(Integer maxRightOffset) {
    this.maxRightOffset = maxRightOffset;
  }
}
