package com.applitools.eyes.selenium;

/**
 * manager type
 */
public enum ManagerType {
  CLASSIC("classic"), VISUAL_GRID("vg");

  public final String value;

  ManagerType(String value) {
    this.value = value;
  }

}
