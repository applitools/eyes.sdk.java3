package com.applitools.eyes.appium;

import com.applitools.eyes.selenium.universal.dto.SelectorRegionDto;
import com.applitools.utils.GeneralUtils;
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
    if (by instanceof MobileBy.ById) {
      selectorRegionDto.setType("id");
    } else if (by instanceof MobileBy.ByXPath) {
      selectorRegionDto.setType("xpath");
    } else if (by instanceof MobileBy.ByLinkText) {
      selectorRegionDto.setType("link text");
    } else if (by instanceof MobileBy.ByPartialLinkText) {
      selectorRegionDto.setType("partial link text");
    } else if (by instanceof MobileBy.ByName) {
      selectorRegionDto.setType("name");
    } else if (by instanceof MobileBy.ByTagName) {
      selectorRegionDto.setType("tag name");
    } else if (by instanceof MobileBy.ByClassName) {
      selectorRegionDto.setType("class name");
    } else if (by instanceof MobileBy.ByCssSelector) {
      selectorRegionDto.setType("css selector");
    } else if (by instanceof MobileBy.ByAccessibilityId) {
      selectorRegionDto.setType("accessibility id");
    } else if (by instanceof MobileBy.ByAndroidUIAutomator) {
      selectorRegionDto.setType("-android uiautomator");
    } else if (by instanceof MobileBy.ByAndroidViewTag) {
      selectorRegionDto.setType("-android viewtag");
    } else if (by instanceof MobileBy.ByWindowsAutomation) {
      selectorRegionDto.setType("-windows uiautomation");
    } else if ("ByIosUIAutomation".equals(by.getClass().getSimpleName())) {
      selectorRegionDto.setType("-ios uiautomation");
    } else if (by instanceof MobileBy.ByIosNsPredicate) {
      selectorRegionDto.setType("-ios predicate string");
    } else if (by instanceof MobileBy.ByIosClassChain) {
      selectorRegionDto.setType("-ios class chain");
    } else if (by instanceof MobileBy.ByImage) {
      selectorRegionDto.setType("-image");
    } else if (by instanceof MobileBy.ByCustom) {
      selectorRegionDto.setType("-custom");
    }
    return selectorRegionDto;

  }

}
