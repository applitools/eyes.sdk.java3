package com.applitools.eyes.universal.mapper;

import com.applitools.eyes.ImageMatchSettings;
import com.applitools.eyes.universal.dto.MatchSettingsDto;

/**
 * image match settings mapper
 */
public class MatchSettingsMapper {

  public static MatchSettingsDto toMatchSettingsDto(Object object) {
    if (object == null) {
      return null;
    }

    if (object instanceof ImageMatchSettings) {
      ImageMatchSettings imageMatchSettings = (ImageMatchSettings) object;
      MatchSettingsDto dto = new MatchSettingsDto();

      dto.setExact(ExactMatchSettingsMapper.toExactMatchSettingsDto(imageMatchSettings.getExact()));
      dto.setMatchLevel(imageMatchSettings.getMatchLevel().getName());
      dto.setSendDom(null);
      dto.setUseDom(imageMatchSettings.isUseDom());
      dto.setEnablePatterns(imageMatchSettings.isEnablePatterns());
      dto.setIgnoreCaret(imageMatchSettings.getIgnoreCaret());
      dto.setIgnoreDisplacements(imageMatchSettings.isIgnoreDisplacements());
      dto.setAccessibilitySettings(AccessibilitySettingsMapper.toAccessibilitySettingsDto(imageMatchSettings.getAccessibilitySettings()));

      // TODO find generic solution
      dto.setIgnoreRegions(RegionMapper.toRegionDtoList(imageMatchSettings.getIgnoreRegions()));
      dto.setLayoutRegions(RegionMapper.toRegionDtoList(imageMatchSettings.getLayoutRegions()));
      dto.setStrictRegions(RegionMapper.toRegionDtoList(imageMatchSettings.getStrictRegions()));
      dto.setContentRegions(RegionMapper.toRegionDtoList(imageMatchSettings.getContentRegions()));

      dto.setFloatingRegions(FloatingMatchSettingsMapper.toFloatingMatchSettingsDtoList(imageMatchSettings.getFloatingRegions()));
      dto.setAccessibilityRegions(AccessibilityRegionByRectangleMapper.toAccessibilityRegionByRectangleDtoList(imageMatchSettings.getAccessibility()));

      return dto;
    }

    return null;
  }
}
