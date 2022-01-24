package com.applitools.universal.mapper;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.applitools.eyes.FloatingMatchSettings;
import com.applitools.universal.dto.FloatingMatchSettingsDto;

/**
 * floating match settings mapper
 */
public class FloatingMatchSettingsMapper {

  public static FloatingMatchSettingsDto toFloatingMatchSettingsDto(FloatingMatchSettings floatingMatchSettings) {
    if (floatingMatchSettings == null) {
      return null;
    }

    FloatingMatchSettingsDto floatingMatchSettingsDto = new FloatingMatchSettingsDto();
    floatingMatchSettingsDto.setTop(floatingMatchSettings.getTop());
    floatingMatchSettingsDto.setLeft(floatingMatchSettings.getLeft());
    floatingMatchSettingsDto.setWidth(floatingMatchSettings.getWidth());
    floatingMatchSettingsDto.setHeight(floatingMatchSettings.getHeight());
    floatingMatchSettingsDto.setMaxUpOffset(floatingMatchSettings.getMaxUpOffset());
    floatingMatchSettingsDto.setMaxDownOffset(floatingMatchSettings.getMaxDownOffset());
    floatingMatchSettingsDto.setMaxLeftOffset(floatingMatchSettings.getMaxLeftOffset());
    floatingMatchSettingsDto.setMaxRightOffset(floatingMatchSettings.getMaxRightOffset());
    return floatingMatchSettingsDto;
  }

  public static List<FloatingMatchSettingsDto> toFloatingMatchSettingsDtoList(FloatingMatchSettings[] floatingMatchSettings) {
    if (floatingMatchSettings == null || floatingMatchSettings.length == 0) {
      return null;
    }

    List<FloatingMatchSettings> floatingMatchSettingsList = Arrays.asList(floatingMatchSettings);
    return floatingMatchSettingsList
        .stream()
        .map(FloatingMatchSettingsMapper::toFloatingMatchSettingsDto)
        .collect(Collectors.toList());
  }


}
