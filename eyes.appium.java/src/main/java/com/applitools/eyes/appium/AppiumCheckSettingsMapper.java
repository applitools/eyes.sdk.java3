package com.applitools.eyes.appium;

import com.applitools.ICheckSettings;
import com.applitools.eyes.selenium.universal.dto.CheckSettingsDto;
import com.applitools.eyes.selenium.universal.mapper.TAccessibilityRegionMapper;
import com.applitools.eyes.selenium.universal.mapper.TFloatingRegionMapper;
import com.applitools.eyes.selenium.universal.mapper.VisualGridOptionMapper;
import com.applitools.eyes.appium.TRegionMapper;

import java.util.Arrays;

public class AppiumCheckSettingsMapper {
    public static CheckSettingsDto toCheckSettingsDto(ICheckSettings checkSettings) {
        if (!(checkSettings instanceof AppiumCheckSettings)) {
            return null;
        }
        AppiumCheckSettings appiumCheckSettings = (AppiumCheckSettings) checkSettings;
        CheckSettingsDto checkSettingsDto = new CheckSettingsDto();

        // CheckSettings
        checkSettingsDto.setName(appiumCheckSettings.getName());
        checkSettingsDto.setDisableBrowserFetching(appiumCheckSettings.isDisableBrowserFetching());
        checkSettingsDto.setVisualGridOptions(VisualGridOptionMapper.toVisualGridOptionDtoList(appiumCheckSettings.getVisualGridOptions()));
        checkSettingsDto.setHooks(appiumCheckSettings.getScriptHooks());
        checkSettingsDto.setRenderId(null);
        checkSettingsDto.setVariationGroupId(appiumCheckSettings.getVariationGroupId());
        checkSettingsDto.setTimeout(appiumCheckSettings.getTimeout());

        // MatchSettings
        checkSettingsDto.setExact(null);
        checkSettingsDto.setMatchLevel(appiumCheckSettings.getMatchLevel() == null ? null : appiumCheckSettings.getMatchLevel().getName());
        checkSettingsDto.setSendDom(appiumCheckSettings.isSendDom());
        checkSettingsDto.setUseDom(appiumCheckSettings.isUseDom());
        checkSettingsDto.setEnablePatterns(appiumCheckSettings.isEnablePatterns());
        checkSettingsDto.setIgnoreCaret(appiumCheckSettings.getIgnoreCaret());
        checkSettingsDto.setIgnoreDisplacements(appiumCheckSettings.isIgnoreDisplacements());
        checkSettingsDto.setAccessibilitySettings(null);

        checkSettingsDto.setIgnoreRegions(TRegionMapper.toTRegionList(Arrays.asList(appiumCheckSettings.getIgnoreRegions())));
        checkSettingsDto.setLayoutRegions(TRegionMapper.toTRegionList(Arrays.asList(appiumCheckSettings.getLayoutRegions())));
        checkSettingsDto.setStrictRegions(TRegionMapper.toTRegionList(Arrays.asList(appiumCheckSettings.getStrictRegions())));
        checkSettingsDto.setContentRegions(TRegionMapper.toTRegionList(Arrays.asList(appiumCheckSettings.getContentRegions())));

        checkSettingsDto.setFloatingRegions(TFloatingRegionMapper.toTFloatingRegionDtoList(Arrays.asList(appiumCheckSettings.getFloatingRegions())));

        checkSettingsDto.setAccessibilityRegions(TAccessibilityRegionMapper.toTAccessibilityRegionDtoList(Arrays.asList(appiumCheckSettings.getAccessibilityRegions())));
        checkSettingsDto.setPageId(appiumCheckSettings.getPageId());

        // ScreenshotSettings
        checkSettingsDto.setRegion(TRegionMapper.toTRegionFromCheckSettings(checkSettings));

        checkSettingsDto.setFully(appiumCheckSettings.getStitchContent());

        return checkSettingsDto;
    }
}
