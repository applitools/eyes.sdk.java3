package com.applitools.eyes.selenium.universal.mapper;

import com.applitools.eyes.selenium.fluent.SeleniumCheckSettings;
import com.applitools.eyes.selenium.universal.dto.ScrollRootElementDto;

/**
 * scroll root element mapper
 */
public class ScrollRootElementMapper {

  public static ScrollRootElementDto toScrollRootElementDto(SeleniumCheckSettings seleniumCheckSettings) {
    if (seleniumCheckSettings == null) {
      return null;
    }

    ScrollRootElementDto scrollRootElementDto = new ScrollRootElementDto();
    scrollRootElementDto.setScrollRootElement(seleniumCheckSettings.getScrollRootElement());
    scrollRootElementDto.setScrollRootSelector(seleniumCheckSettings.getScrollRootSelector());
    return scrollRootElementDto;

  }
}
