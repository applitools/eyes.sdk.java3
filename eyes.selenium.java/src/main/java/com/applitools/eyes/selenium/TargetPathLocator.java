package com.applitools.eyes.selenium;

/**
 * target path locator
 */
public class TargetPathLocator {
  protected TargetPathLocator parent;
  protected PathNodeValue value;

  public TargetPathLocator() {
  }

  public TargetPathLocator(TargetPathLocator parent, PathNodeValue value) {
    this.parent = parent;
    this.value = value;
  }

  public PathNodeValue getValue() {
    return value;
  }

  public TargetPathLocator getParent() {
    return parent;
  }

}
