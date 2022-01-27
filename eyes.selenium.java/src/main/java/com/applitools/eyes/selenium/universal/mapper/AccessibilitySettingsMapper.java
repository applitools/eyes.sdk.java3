package com.applitools.eyes.selenium.universal.mapper;

import com.applitools.eyes.AccessibilitySettings;
import com.applitools.eyes.selenium.universal.dto.AccessibilitySettingsDto;

/**
 * Accessibility settings mapper
 */
public class AccessibilitySettingsMapper {

  public static AccessibilitySettingsDto toAccessibilitySettingsDto(AccessibilitySettings accessibilitySettings) {
    if (accessibilitySettings == null) {
      return null;
    }

    AccessibilitySettingsDto accessibilitySettingsDto = new AccessibilitySettingsDto();
    accessibilitySettingsDto.setLevel(accessibilitySettings.getLevel().name());
    accessibilitySettingsDto.setVersion(accessibilitySettings.getGuidelinesVersion().name());
    return accessibilitySettingsDto;
  }
}
