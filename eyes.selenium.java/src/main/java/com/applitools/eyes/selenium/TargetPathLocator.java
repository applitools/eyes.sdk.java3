package com.applitools.eyes.selenium;

/**
 * target path locator
 */
public class TargetPathLocator {
  protected TargetPathLocator parent;
  protected PathNodeValue value;

  public TargetPathLocator() {
  }

  public TargetPathLocator(TargetPathLocator locator, PathNodeValue value) {
    this.parent = locator;
    this.value = value;
  }

}
