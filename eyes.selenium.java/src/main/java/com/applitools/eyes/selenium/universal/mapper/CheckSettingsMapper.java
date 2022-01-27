package com.applitools.eyes.selenium.universal.mapper;

import java.util.Arrays;

import com.applitools.ICheckSettings;
import com.applitools.eyes.selenium.fluent.SeleniumCheckSettings;
import com.applitools.eyes.selenium.universal.dto.CheckSettingsDto;

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
    checkSettingsDto.setMatchLevel(seleniumCheckSettings.getMatchLevel() == null ? null : seleniumCheckSettings.getMatchLevel().getName());
    checkSettingsDto.setSendDom(seleniumCheckSettings.isSendDom());
    checkSettingsDto.setUseDom(seleniumCheckSettings.isUseDom());
    checkSettingsDto.setEnablePatterns(seleniumCheckSettings.isEnablePatterns());
    checkSettingsDto.setIgnoreCaret(seleniumCheckSettings.getIgnoreCaret());
    checkSettingsDto.setIgnoreDisplacements(seleniumCheckSettings.isIgnoreDisplacements());
    checkSettingsDto.setAccessibilitySettings(null);
    checkSettingsDto.setIgnoreRegions(TRegionMapper.toTRegionList(Arrays.asList(seleniumCheckSettings.getIgnoreRegions())));
    checkSettingsDto.setLayoutRegions(TRegionMapper.toTRegionList(Arrays.asList(seleniumCheckSettings.getLayoutRegions())));
    checkSettingsDto.setStrictRegions(TRegionMapper.toTRegionList(Arrays.asList(seleniumCheckSettings.getStrictRegions())));
    checkSettingsDto.setContentRegions(TRegionMapper.toTRegionList(Arrays.asList(seleniumCheckSettings.getContentRegions())));
    checkSettingsDto.setFloatingRegions(TFloatingRegionMapper.toTFloatingRegionDtoList(Arrays.asList(seleniumCheckSettings.getFloatingRegions())));
    checkSettingsDto.setAccessibilityRegions(TAccessibilityRegionMapper.toTAccessibilityRegionDtoList(Arrays.asList(seleniumCheckSettings.getAccessibilityRegions())));
    checkSettingsDto.setRegion(TRegionMapper.toTRegionFromCheckSettings(checkSettings));
    checkSettingsDto.setFrames(ContextReferenceMapper.toContextReferenceDtoList(seleniumCheckSettings.getFrameChain()));
    checkSettingsDto
        .setScrollRootElement(TRegionMapper
            .toTRegionDtoFromScrolls(seleniumCheckSettings.getScrollRootSelector(),
                seleniumCheckSettings.getScrollRootElement()));

    checkSettingsDto.setFully(seleniumCheckSettings.getStitchContent());

    return checkSettingsDto;
  }

}
