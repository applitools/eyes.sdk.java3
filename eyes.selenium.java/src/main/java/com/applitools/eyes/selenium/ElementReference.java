package com.applitools.eyes.selenium;

import org.openqa.selenium.WebElement;

/**
 * element reference
 */
public class ElementReference implements PathNodeValue {
  private WebElement element;

  public ElementReference() {

  }

  public ElementReference(WebElement element) {
    this.element = element;
  }

  public WebElement getElement() {
    return element;
  }

  public void setElement(WebElement element) {
    this.element = element;
  }

}
