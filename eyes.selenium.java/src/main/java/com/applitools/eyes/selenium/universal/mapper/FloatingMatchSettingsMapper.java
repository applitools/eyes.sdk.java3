package com.applitools.eyes.selenium.universal.mapper;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.applitools.eyes.FloatingMatchSettings;
import com.applitools.eyes.selenium.universal.dto.FloatingMatchSettingsDto;
import com.applitools.eyes.selenium.universal.dto.RegionDto;

/**
 * floating match settings mapper
 */
public class FloatingMatchSettingsMapper {

  public static FloatingMatchSettingsDto toFloatingMatchSettingsDto(FloatingMatchSettings floatingMatchSettings) {
    if (floatingMatchSettings == null) {
      return null;
    }

    RegionDto regionDto = new RegionDto();
    regionDto.setTop(floatingMatchSettings.getTop());
    regionDto.setHeight(floatingMatchSettings.getHeight());
    regionDto.setWidth(floatingMatchSettings.getWidth());
    regionDto.setLeft(floatingMatchSettings.getLeft());
    regionDto.setCoordinatesType(null);

    FloatingMatchSettingsDto dto = new FloatingMatchSettingsDto();
    dto.setRegion(regionDto);
    dto.setMaxUpOffset(floatingMatchSettings.getMaxUpOffset());
    dto.setMaxDownOffset(floatingMatchSettings.getMaxDownOffset());
    dto.setMaxLeftOffset(floatingMatchSettings.getMaxLeftOffset());
    dto.setMaxRightOffset(floatingMatchSettings.getMaxRightOffset());
    return dto;
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
