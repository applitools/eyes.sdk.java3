package com.applitools.eyes.selenium;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

/**
 * shadow dom locator
 */
public class ShadowDomLocator extends TargetPathLocator {

  public ShadowDomLocator(TargetPathLocator locator, PathNodeValue value) {
    super(locator, value);
  }

  public RegionLocator region(TargetPathLocator locator, WebElement element) {
    return new RegionLocator(locator, new ElementReference(element));
  }

  public RegionLocator region(TargetPathLocator locator, By by, String selector) {
    return new RegionLocator(locator, new ElementSelector(by, selector));
  }

  public RegionLocator region(TargetPathLocator locator, String selector) {
    return new RegionLocator(locator, new ElementSelector(By.ByCssSelector.cssSelector(selector), selector));
  }

  public ShadowDomLocator shadow(TargetPathLocator locator, WebElement element) {
    return new ShadowDomLocator(locator, new ElementReference(element));
  }

  public ShadowDomLocator shadow(TargetPathLocator locator, By by, String selector) {
    return new ShadowDomLocator(locator, new ElementSelector(by, selector));
  }

  public ShadowDomLocator shadow(TargetPathLocator locator, String selector) {
    return new ShadowDomLocator(locator, new ElementSelector(By.ByCssSelector.cssSelector(selector), selector));
  }

}
