package com.applitools.eyes.selenium.universal.dto;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

/**
 * frame locator dto
 */
public class FrameLocatorDto {
  private WebElement frameElement;
  private By frameSelector;
  private String frameNameOrId;
  private Integer frameIndex;
  private By scrollRootSelector;
  private WebElement scrollRootElement;

  public WebElement getFrameElement() {
    return frameElement;
  }

  public void setFrameElement(WebElement frameElement) {
    this.frameElement = frameElement;
  }

  public By getFrameSelector() {
    return frameSelector;
  }

  public void setFrameSelector(By frameSelector) {
    this.frameSelector = frameSelector;
  }

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

  public By getScrollRootSelector() {
    return scrollRootSelector;
  }

  public void setScrollRootSelector(By scrollRootSelector) {
    this.scrollRootSelector = scrollRootSelector;
  }

  public WebElement getScrollRootElement() {
    return scrollRootElement;
  }

  public void setScrollRootElement(WebElement scrollRootElement) {
    this.scrollRootElement = scrollRootElement;
  }
}
