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
    selectorRegionDto.setSelector(GeneralUtils.getLastWordOfStringWithRegex(by.toString(), ":"));
    if (by instanceof By.ById) {
      selectorRegionDto.setType("id");
    } else if (by instanceof By.ByXPath) {
      selectorRegionDto.setType("xpath");
    } else if (by instanceof By.ByLinkText) {
      selectorRegionDto.setType("link text");
    } else if (by instanceof By.ByPartialLinkText) {
      selectorRegionDto.setType("partial link text");
    } else if (by instanceof By.ByName) {
      selectorRegionDto.setType("name");
    } else if (by instanceof By.ByTagName) {
      selectorRegionDto.setType("tag name");
    } else if (by instanceof By.ByClassName) {
      selectorRegionDto.setType("class name");
    } else if (by instanceof By.ByCssSelector){
      selectorRegionDto.setType("css selector");
    }
    return selectorRegionDto;
  }
}
