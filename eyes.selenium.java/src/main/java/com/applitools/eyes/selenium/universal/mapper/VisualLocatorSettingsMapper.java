package com.applitools.eyes.selenium.universal.mapper;

import com.applitools.eyes.locators.VisualLocatorSettings;
import com.applitools.eyes.selenium.universal.dto.VisualLocatorSettingsDto;

/**
 * visual locator settings mapper
 */
public class VisualLocatorSettingsMapper {

  public static VisualLocatorSettingsDto toVisualLocatorSettingsDto(VisualLocatorSettings visualLocatorSettings) {
    if (visualLocatorSettings == null) {
      return null;
    }

    VisualLocatorSettingsDto visualLocatorSettingsDto = new VisualLocatorSettingsDto();
    visualLocatorSettingsDto.setLocatorNames(visualLocatorSettings.getNames());
    visualLocatorSettingsDto.setFirstOnly(visualLocatorSettings.isFirstOnly());

    return visualLocatorSettingsDto;
  }

}
