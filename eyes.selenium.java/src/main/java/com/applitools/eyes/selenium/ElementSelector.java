package com.applitools.eyes.selenium;

import com.applitools.eyes.EyesException;
import com.applitools.eyes.selenium.universal.dto.TargetPathLocatorDto;
import com.applitools.eyes.selenium.universal.mapper.SelectorRegionMapper;
import com.applitools.utils.GeneralUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.support.pagefactory.ByAll;
import org.openqa.selenium.support.pagefactory.ByChained;

import java.lang.reflect.Field;

/**
 * element selector
 */
public class ElementSelector extends TargetPathLocatorDto implements PathNodeValue {
  private String type;
  private String selector;
  private ElementSelector fallback;
  private ElementSelector child;

  private ElementSelector() {
  }

  public ElementSelector(By by) {
    if (by instanceof ByAll) {
      populateFromByAll((ByAll) by);
    } else if (by instanceof ByChained) {
      populateFromByChained((ByChained) by);
    } else {
      ElementSelector clone = populateFromBy(by);
      this.selector = clone.getSelector();
      this.type = clone.getType();
    }
  }

  public ElementSelector(String selector) {
    this.type = "css selector";
    this.selector = selector;
  }

  private ElementSelector populateFromBy(By by) {
    ElementSelector es = new ElementSelector();

    String selector = GeneralUtils.getLastWordOfStringWithRegex(by.toString(), ":");
    es.selector = selector;
    if (by instanceof By.ById) {
      es.type = "css selector";
      es.selector = String.format("[id=\"%s\"]", selector);
    } else if (by instanceof By.ByXPath) {
      es.type = "xpath";
    } else if (by instanceof By.ByLinkText) {
      es.type = "link text";
    } else if (by instanceof By.ByPartialLinkText) {
      es.type = "partial link text";
    } else if (by instanceof By.ByName) {
      es.type = "css selector";
      es.selector = String.format("[name=\"%s\"]", selector);
    } else if (by instanceof By.ByTagName) {
      es.type = "css selector";
    } else if (by instanceof By.ByClassName) {
      es.type = "css selector";
      es.selector = ".".concat(selector);
    } else if (by instanceof By.ByCssSelector){
      es.type = "css selector";
    }

    return es;
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

  private void populateFromByAll(ByAll byAll) {
    try {
      Field bys_ = byAll.getClass().getDeclaredField("bys");
      bys_.setAccessible(true);

      By[] bys = (By[]) bys_.get(byAll);

      ElementSelector fallback = null;
      for (int i = bys.length-1; i >= 0; i--) {
        ElementSelector region = new ElementSelector(bys[i]);
        this.setSelector(region.getSelector());
        this.setType(region.getType());
        this.setFallback(fallback);
        fallback = region;
      }
    } catch (NoSuchFieldException | IllegalAccessException e) {
      System.out.println("Got a failure trying to find By[] using reflection! Error " + e.getMessage());
      throw new EyesException("Got a failure trying to find By[] using reflection! Error " + e.getMessage());
    }
  }

  public ElementSelector getFallback() {
    return fallback;
  }

  public void setFallback(ElementSelector fallback) {
    this.fallback = fallback;
  }

  private void populateFromByChained(ByChained byChained) {
    try {
      Field bys_ = byChained.getClass().getDeclaredField("bys");
      bys_.setAccessible(true);

      By[] bys = (By[]) bys_.get(byChained);

      ElementSelector child = null;
      for (int i = bys.length-1; i >= 0; i--) {
        ElementSelector region = new ElementSelector(bys[i]);
        this.setSelector(region.getSelector());
        this.setType(region.getType());
        this.setChild(child);
        child = region;
      }
    } catch (NoSuchFieldException | IllegalAccessException e) {
      System.out.println("Got a failure trying to find By[] using reflection! Error " + e.getMessage());
      throw new EyesException("Got a failure trying to find By[] using reflection! Error " + e.getMessage());
    }
  }

  public ElementSelector getChild() {
    return child;
  }

  public void setChild(ElementSelector child) {
    this.child = child;
  }
}
