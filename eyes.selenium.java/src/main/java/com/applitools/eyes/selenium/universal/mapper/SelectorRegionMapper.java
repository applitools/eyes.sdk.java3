package com.applitools.eyes.selenium.universal.mapper;

import com.applitools.eyes.selenium.universal.dto.SelectorRegionDto;
import com.applitools.utils.GeneralUtils;
import org.openqa.selenium.By;

/**
 * selector region mapper
 */
public class SelectorRegionMapper {

  public static SelectorRegionDto toSelectorRegionDto(By by) {
    if (by == null) {
      return null;
    }

    SelectorRegionDto selectorRegionDto = new SelectorRegionDto();
    System.out.println("toString: " + by);
    String selector = GeneralUtils.getLastWordOfStringWithRegex(by.toString(), ":");
    System.out.println("selector: " + selector);
    selectorRegionDto.setSelector(selector);
    if (by instanceof By.ById) {
      selectorRegionDto.setType("css selector");
      selectorRegionDto.setSelector(String.format("[id=\"%s\"]", selector));
    } else if (by instanceof By.ByXPath) {
      selectorRegionDto.setType("xpath");
    } else if (by instanceof By.ByLinkText) {
      selectorRegionDto.setType("link text");
    } else if (by instanceof By.ByPartialLinkText) {
      selectorRegionDto.setType("partial link text");
    } else if (by instanceof By.ByName) {
      selectorRegionDto.setType("css selector");
      selectorRegionDto.setSelector(String.format("[name=\"%s\"]", selector));
    } else if (by instanceof By.ByTagName) {
      selectorRegionDto.setType("css selector");
    } else if (by instanceof By.ByClassName) {
      selectorRegionDto.setType("css selector");
      selectorRegionDto.setSelector(".".concat(selector));
    } else if (by instanceof By.ByCssSelector){
      selectorRegionDto.setType("css selector");
    }

    return selectorRegionDto;
  }
}
