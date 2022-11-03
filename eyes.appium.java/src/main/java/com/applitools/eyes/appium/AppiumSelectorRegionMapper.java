package com.applitools.eyes.appium;

import com.applitools.eyes.EyesException;
import com.applitools.eyes.selenium.universal.dto.SelectorRegionDto;
import com.applitools.utils.GeneralUtils;
import io.appium.java_client.AppiumBy;
import io.appium.java_client.MobileBy;
import io.appium.java_client.pagefactory.bys.builder.ByAll;
import io.appium.java_client.pagefactory.bys.builder.ByChained;
import org.openqa.selenium.By;

import java.lang.reflect.Field;
import java.util.List;

public class AppiumSelectorRegionMapper {

  public static SelectorRegionDto toAppiumSelectorRegionDto(By by) {
    if (by == null) {
      return null;
    }

    if (by instanceof ByAll) {
      return toAppiumSelectorRegionDto((ByAll) by);
    } else if (by instanceof ByChained) {
      return toAppiumSelectorRegionDto((ByChained) by);
    }
    return toDto(by);
  }

  private static SelectorRegionDto toDto(By by) {
    SelectorRegionDto selectorRegionDto = new SelectorRegionDto();
    String selector = GeneralUtils.getLastWordOfStringWithRegex(by.toString(), ":");
    selectorRegionDto.setSelector(selector);

    if (by instanceof AppiumBy.ById) {
      selectorRegionDto.setType("id");
    } else if (by instanceof AppiumBy.ByXPath) {
      selectorRegionDto.setType("xpath");
    } else if (by instanceof AppiumBy.ByLinkText) {
      selectorRegionDto.setType("link text");
    } else if (by instanceof AppiumBy.ByPartialLinkText) {
      selectorRegionDto.setType("partial link text");
    } else if (by instanceof AppiumBy.ByName) {
      selectorRegionDto.setType("name");
    } else if (by instanceof AppiumBy.ByTagName) {
      selectorRegionDto.setType("tag name");
    } else if (by instanceof AppiumBy.ByClassName) {
      selectorRegionDto.setType("class name");
    } else if (by instanceof AppiumBy.ByCssSelector) {
      selectorRegionDto.setType("css selector");
    } else if (by instanceof AppiumBy.ByAccessibilityId) {
      selectorRegionDto.setType("accessibility id");
    } else if (by instanceof AppiumBy.ByAndroidUIAutomator) {
      selectorRegionDto.setType("-android uiautomator");
    } else if (by instanceof AppiumBy.ByAndroidViewTag) {
      selectorRegionDto.setType("-android viewtag");
    } else if (by instanceof MobileBy.ByWindowsAutomation) {
      selectorRegionDto.setType("-windows uiautomation");
    } else if ("ByIosUIAutomation".equals(by.getClass().getSimpleName())) {
      selectorRegionDto.setType("-ios uiautomation");
    } else if (by instanceof AppiumBy.ByIosNsPredicate) {
      selectorRegionDto.setType("-ios predicate string");
    } else if (by instanceof AppiumBy.ByIosClassChain) {
      selectorRegionDto.setType("-ios class chain");
    } else if (by instanceof AppiumBy.ByImage) {
      selectorRegionDto.setType("-image");
    } else if (by instanceof AppiumBy.ByCustom) {
      selectorRegionDto.setType("-custom");
    }
    return selectorRegionDto;
  }

  private static SelectorRegionDto toAppiumSelectorRegionDto(ByAll byAll) {
    try {
      Field bys_ = byAll.getClass().getDeclaredField("bys");
      bys_.setAccessible(true);

      List<By> bys = (List<By>) bys_.get(byAll);

      SelectorRegionDto fallback = null;
      SelectorRegionDto region = null;
      for (int i = bys.size()-1; i >= 0; i--) {
        region = toDto(bys.get(i));
        region.setFallback(fallback);
        fallback = region;
      }

      return region;
    } catch (NoSuchFieldException | IllegalAccessException e) {
      System.out.println("Got a failure trying to find By[] using reflection! Error " + e.getMessage());
      throw new EyesException("Got a failure trying to find By[] using reflection! Error " + e.getMessage());
    }
  }

  private static SelectorRegionDto toAppiumSelectorRegionDto(ByChained byChained) {
    try {
      Field bys_ = byChained.getClass().getDeclaredField("bys");
      bys_.setAccessible(true);

      By[] bys = (By[]) bys_.get(byChained);

      SelectorRegionDto child = null;
      SelectorRegionDto region = null;
      for (int i = bys.length-1; i >= 0; i--) {
        region = toDto(bys[i]);
        region.setChild(child);
        child = region;
      }

      return region;
    } catch (NoSuchFieldException | IllegalAccessException e) {
      System.out.println("Got a failure trying to find By[] using reflection! Error " + e.getMessage());
      throw new EyesException("Got a failure trying to find By[] using reflection! Error " + e.getMessage());
    }
  }
}
