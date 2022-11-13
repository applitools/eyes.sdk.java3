package com.applitools.eyes;

import com.applitools.eyes.selenium.ElementReference;
import com.applitools.eyes.selenium.ElementSelector;
import com.applitools.eyes.selenium.RegionLocator;
import com.applitools.eyes.selenium.ShadowDomLocator;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.pagefactory.ByAll;
import org.openqa.selenium.support.pagefactory.ByChained;

public class TargetPath {

  public static RegionLocator region(By by) {
    return new RegionLocator(null, new ElementSelector(by));
  }

//  public static RegionLocator region(ByAll byAll) {
//    return new RegionLocator(null, new ElementSelector(byAll));
//  }

//  public static RegionLocator region(ByChained byChained) {
//    return new RegionLocator(null, new ElementSelector(byChained));
//  }

  public static RegionLocator region(WebElement element) {
    return new RegionLocator(null, new ElementReference(element));
  }

  public static RegionLocator region(String selector) {
    return new RegionLocator(null, new ElementSelector(selector));
  }

  public static ShadowDomLocator shadow(By by) {
    return new ShadowDomLocator(null, new ElementSelector(by));
  }

  public static ShadowDomLocator shadow(WebElement element) {
    return new ShadowDomLocator(null, new ElementReference(element));
  }

  public static ShadowDomLocator shadow(String selector) {
    return new ShadowDomLocator(null, new ElementSelector(selector));
  }

}
