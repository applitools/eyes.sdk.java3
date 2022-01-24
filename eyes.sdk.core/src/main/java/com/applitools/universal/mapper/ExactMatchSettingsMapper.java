package com.applitools.universal.mapper;

import com.applitools.eyes.ExactMatchSettings;
import com.applitools.universal.dto.ExactMatchSettingsDto;

/**
 * exact match settings mapper
 */
public class ExactMatchSettingsMapper {

  public static ExactMatchSettingsDto toExactMatchSettingsDto(ExactMatchSettings exactMatchSettings) {
    if (exactMatchSettings == null) {
      return null;
    }

    ExactMatchSettingsDto exactMatchSettingsDto = new ExactMatchSettingsDto();
    exactMatchSettingsDto.setMinDiffIntensity(exactMatchSettings.getMinDiffIntensity());
    exactMatchSettingsDto.setMinDiffWidth(exactMatchSettings.getMinDiffWidth());
    exactMatchSettingsDto.setMinDiffHeight(exactMatchSettings.getMinDiffHeight());
    exactMatchSettingsDto.setMatchThreshold(exactMatchSettings.getMatchThreshold());
    return exactMatchSettingsDto;

  }
}
