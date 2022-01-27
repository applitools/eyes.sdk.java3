package com.applitools.eyes.selenium.universal.mapper;

import com.applitools.eyes.locators.TextRegionSettings;
import com.applitools.eyes.selenium.universal.dto.OCRSearchSettingsDto;

/**
 * OCR search settings mapper
 */
public class OCRSearchSettingsMapper {

  public static OCRSearchSettingsDto toOCRSearchSettingsDto(TextRegionSettings textRegionSettings) {
    if (textRegionSettings == null) {
      return null;
    }

    OCRSearchSettingsDto ocrSearchSettingsDto = new OCRSearchSettingsDto();
    ocrSearchSettingsDto.setPatterns(textRegionSettings.getPatterns());
    ocrSearchSettingsDto.setIgnoreCase(textRegionSettings.getIgnoreCase());
    ocrSearchSettingsDto.setFirstOnly(textRegionSettings.getFirstOnly());
    ocrSearchSettingsDto.setLanguage(textRegionSettings.getLanguage());

    return ocrSearchSettingsDto;
  }
}
