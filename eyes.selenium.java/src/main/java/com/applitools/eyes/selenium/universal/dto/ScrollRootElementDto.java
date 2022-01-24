package com.applitools.eyes.selenium.universal.dto;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

/**
 * scroll root element dto
 */
public class ScrollRootElementDto {
  private WebElement scrollRootElement;
  private By scrollRootSelector;

  public WebElement getScrollRootElement() {
    return scrollRootElement;
  }

  public void setScrollRootElement(WebElement scrollRootElement) {
    this.scrollRootElement = scrollRootElement;
  }

  public By getScrollRootSelector() {
    return scrollRootSelector;
  }

  public void setScrollRootSelector(By scrollRootSelector) {
    this.scrollRootSelector = scrollRootSelector;
  }
}
