package com.applitools.eyes.appium;

import com.applitools.eyes.universal.dto.SelectorRegionDto;
import com.applitools.utils.GeneralUtils;
import io.appium.java_client.AppiumBy;
import io.appium.java_client.MobileBy;
import org.openqa.selenium.By;

public class AppiumSelectorRegionMapper {

  public static SelectorRegionDto toAppiumSelectorRegionDto(By by) {
    if (by == null) {
      return null;
    }

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

}
