package com.applitools.eyes.selenium;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

/**
 * shadow dom locator
 */
public class ShadowDomLocator extends TargetPathLocator {

  public ShadowDomLocator(TargetPathLocator parent, PathNodeValue value) {
    super(parent, value);
  }

  public RegionLocator region(WebElement element) {
    return new RegionLocator(this, new ElementReference(element));
  }

  public RegionLocator region(By by, String selector) {
    return new RegionLocator(this, new ElementSelector(by));
  }

  public RegionLocator region(String selector) {
    return new RegionLocator(this, new ElementSelector(selector));
  }

  public ShadowDomLocator shadow(WebElement element) {
    return new ShadowDomLocator(this, new ElementReference(element));
  }

  public ShadowDomLocator shadow(By by, String selector) {
    return new ShadowDomLocator(this, new ElementSelector(by));
  }

  public ShadowDomLocator shadow(String selector) {
    return new ShadowDomLocator(this, new ElementSelector(selector));
  }

}
