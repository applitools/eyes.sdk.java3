package com.applitools.eyes.selenium;

import org.openqa.selenium.By;

/**
 * element selector
 */
public class ElementSelector implements PathNodeValue {
  private By type;
  private String selector;

  public ElementSelector(By by, String selector) {
    this.type = by;
    this.selector = selector;
  }

  public ElementSelector(String selector) {
    this.type = By.ByCssSelector.cssSelector(selector);
    this.selector = selector;
  }

}
