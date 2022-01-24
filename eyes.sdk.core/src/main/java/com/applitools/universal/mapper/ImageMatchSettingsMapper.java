package com.applitools.universal.mapper;

import com.applitools.eyes.ImageMatchSettings;
import com.applitools.universal.dto.ImageMatchSettingsDto;

/**
 * image match settings mapper
 */
public class ImageMatchSettingsMapper {

  public static ImageMatchSettingsDto toImageMatchSettingsDto(ImageMatchSettings imageMatchSettings) {
    if (imageMatchSettings == null) {
      return null;
    }

    ImageMatchSettingsDto imageMatchSettingsDto = new ImageMatchSettingsDto();
    imageMatchSettingsDto.setMatchLevel(imageMatchSettings.getMatchLevel().getName());
    imageMatchSettingsDto.setExact(ExactMatchSettingsMapper.toExactMatchSettingsDto(imageMatchSettings.getExact()));
    imageMatchSettingsDto.setIgnoreCaret(imageMatchSettings.getIgnoreCaret());
    imageMatchSettingsDto.setIgnoreRegions(RegionMapper.toRegionDtoList(imageMatchSettings.getIgnoreRegions()));
    imageMatchSettingsDto.setLayoutRegions(RegionMapper.toRegionDtoList(imageMatchSettings.getLayoutRegions()));
    imageMatchSettingsDto.setStrictRegions(RegionMapper.toRegionDtoList(imageMatchSettings.getStrictRegions()));
    imageMatchSettingsDto.setContentRegions(RegionMapper.toRegionDtoList(imageMatchSettings.getContentRegions()));
    imageMatchSettingsDto
        .setFloatingMatchSettings(FloatingMatchSettingsMapper
            .toFloatingMatchSettingsDtoList(imageMatchSettings.getFloatingRegions()));

    imageMatchSettingsDto.setUseDom(imageMatchSettings.isUseDom());
    imageMatchSettingsDto.setEnablePatterns(imageMatchSettings.isEnablePatterns());
    imageMatchSettingsDto.setIgnoreDisplacements(imageMatchSettings.isIgnoreDisplacements());
    imageMatchSettingsDto
        .setAccessibility(AccessibilityRegionByRectangleMapper
            .toAccessibilityRegionByRectangleDtoList(imageMatchSettings.getAccessibility()));

    imageMatchSettingsDto
        .setAccessibilitySettings(AccessibilitySettingsMapper
            .toAccessibilitySettingsDto(imageMatchSettings.getAccessibilitySettings()));

    return imageMatchSettingsDto;

  }
}
