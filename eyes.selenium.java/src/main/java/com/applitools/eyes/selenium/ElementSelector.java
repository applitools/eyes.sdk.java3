package com.applitools.eyes.selenium;

import com.applitools.utils.GeneralUtils;
import org.openqa.selenium.By;

/**
 * element selector
 */
public class ElementSelector implements PathNodeValue {
  private String type;
  private String selector;

  public ElementSelector(By by) {
    System.out.println("toString: " + by);
    String selector = GeneralUtils.getLastWordOfStringWithRegex(by.toString(), ":");
    this.selector = selector;
    if (by instanceof By.ById) {
      this.type = "css selector";
      this.selector = String.format("[id=\"%s\"]", selector);
    } else if (by instanceof By.ByXPath) {
      this.type = "xpath";
    } else if (by instanceof By.ByLinkText) {
      this.type = "link text";
    } else if (by instanceof By.ByPartialLinkText) {
      this.type = "partial link text";
    } else if (by instanceof By.ByName) {
      this.type = "css selector";
      this.selector = String.format("[name=\"%s\"]", selector);
    } else if (by instanceof By.ByTagName) {
      this.type = "css selector";
    } else if (by instanceof By.ByClassName) {
      this.type = "css selector";
      this.selector = ".".concat(selector);
    } else if (by instanceof By.ByCssSelector){
      this.type = "css selector";
    }
  }

  public ElementSelector(String selector) {
    this.type = "css selector";
    this.selector = selector;
  }

  public String getSelector() {
    return selector;
  }

  public String getType() {
    return type;
  }

  public void setSelector(String selector) {
    this.selector = selector;
  }

  public void setType(String type) {
    this.type = type;
  }

}
