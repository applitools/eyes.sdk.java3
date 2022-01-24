package com.applitools.eyes.selenium.universal.mapper;

import java.util.Arrays;

import com.applitools.ICheckSettings;
import com.applitools.eyes.selenium.fluent.SeleniumCheckSettings;
import com.applitools.eyes.selenium.universal.dto.CheckSettingsDto;
import com.applitools.universal.mapper.VisualGridOptionMapper;

/**
 * check settings mapper
 */
public class CheckSettingsMapper {

  public static CheckSettingsDto toCheckSettingsDto(ICheckSettings checkSettings) {
    if (!(checkSettings instanceof SeleniumCheckSettings)) {
      return null;
    }

    SeleniumCheckSettings seleniumCheckSettings = (SeleniumCheckSettings) checkSettings;

    CheckSettingsDto checkSettingsDto = new CheckSettingsDto();
    // CheckSettings
    checkSettingsDto.setName(seleniumCheckSettings.getName());
    checkSettingsDto.setDisableBrowserFetching(seleniumCheckSettings.isDisableBrowserFetching());
    checkSettingsDto.setLayoutBreakpoints(seleniumCheckSettings.getLayoutBreakpoints());
    checkSettingsDto.setVisualGridOptions(VisualGridOptionMapper.toVisualGridOptionDtoList(seleniumCheckSettings.getVisualGridOptions()));
    checkSettingsDto.setHooks(seleniumCheckSettings.getScriptHooks());
    checkSettingsDto.setRenderId(null);
    checkSettingsDto.setVariationGroupId(seleniumCheckSettings.getVariationGroupId());
    checkSettingsDto.setTimeout(seleniumCheckSettings.getTimeout());

    // MatchSettings
    checkSettingsDto.setExact(null);
    checkSettingsDto.setMatchLevel(seleniumCheckSettings.getMatchLevel().getName());
    checkSettingsDto.setSendDom(seleniumCheckSettings.isSendDom());
    checkSettingsDto.setUseDom(seleniumCheckSettings.isUseDom());
    checkSettingsDto.setEnablePatterns(seleniumCheckSettings.isEnablePatterns());
    checkSettingsDto.setIgnoreCaret(seleniumCheckSettings.getIgnoreCaret());
    checkSettingsDto.setIgnoreDisplacements(seleniumCheckSettings.isIgnoreDisplacements());
    checkSettingsDto.setAccessibilitySettings(null);
    checkSettingsDto.setIgnoreRegions(SimpleRegionMapper.toSimpleRegionDtoList(Arrays.asList(seleniumCheckSettings.getIgnoreRegions())));
    checkSettingsDto.setLayoutRegions(SimpleRegionMapper.toSimpleRegionDtoList(Arrays.asList(seleniumCheckSettings.getLayoutRegions())));
    checkSettingsDto.setStrictRegions(SimpleRegionMapper.toSimpleRegionDtoList(Arrays.asList(seleniumCheckSettings.getStrictRegions())));
    checkSettingsDto.setContentRegions(SimpleRegionMapper.toSimpleRegionDtoList(Arrays.asList(seleniumCheckSettings.getContentRegions())));
    checkSettingsDto.setFloatingRegions(FloatingRegionMapper.toFloatingRegionDtoList(Arrays.asList(seleniumCheckSettings.getFloatingRegions())));


    return checkSettingsDto;
  }

}
