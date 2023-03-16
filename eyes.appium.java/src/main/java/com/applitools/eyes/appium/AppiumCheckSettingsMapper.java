package com.applitools.eyes.appium;

import com.applitools.ICheckSettings;
import com.applitools.eyes.config.Configuration;
import com.applitools.eyes.universal.ManagerType;
import com.applitools.eyes.universal.dto.CheckSettingsDto;
import com.applitools.eyes.universal.mapper.AccessibilitySettingsMapper;
import com.applitools.eyes.universal.mapper.NMGOptionsMapper;
import com.applitools.eyes.universal.mapper.ProxyMapper;
import com.applitools.eyes.universal.mapper.VisualGridOptionMapper;

import java.util.Arrays;

public class AppiumCheckSettingsMapper {

    public static CheckSettingsDto toCheckSettingsDtoV3(ICheckSettings checkSettings, Configuration config) {
        if (!(checkSettings instanceof AppiumCheckSettings)) {
            return null;
        }
        AppiumCheckSettings appiumCheckSettings = (AppiumCheckSettings) checkSettings;
        CheckSettingsDto checkSettingsDto = new CheckSettingsDto();

        // CheckSettings
        checkSettingsDto.setRegion(AppiumTRegionMapper.toTRegionFromCheckSettings(checkSettings));
//        checkSettingsDto.setFrames(ContextReferenceMapper.toContextReferenceDtoList(appiumCheckSettings.getFrameChain()));
        checkSettingsDto.setFully(appiumCheckSettings.getStitchContent());
        checkSettingsDto.setScrollRootElement(AppiumTRegionMapper.toTRegionDtoFromScrolls(appiumCheckSettings
                .getScrollRootElementSelector(), appiumCheckSettings.getScrollRootElement()));
        checkSettingsDto.setStitchMode(config.getStitchMode() == null ? null : config.getStitchMode().getName());
        checkSettingsDto.setHideScrollbars(config.getHideScrollbars());
        checkSettingsDto.setHideCaret(config.getHideCaret());
        checkSettingsDto.setOverlap(config.getOverlap());
        checkSettingsDto.setWaitBeforeCapture(appiumCheckSettings.getWaitBeforeCapture());
        checkSettingsDto.setLazyLoad(appiumCheckSettings.getLazyLoadOptions());
        checkSettingsDto.setIgnoreDisplacements(appiumCheckSettings.isIgnoreDisplacements());
        checkSettingsDto.setWebview(appiumCheckSettings.getWebview() != null?
                appiumCheckSettings.getWebview() : (appiumCheckSettings.getIsDefaultWebview() != null?
                appiumCheckSettings.getIsDefaultWebview() : null));
//    private Normalization normalization; //TODO I'm NEW

        checkSettingsDto.setDebugImages(null); //TODO I'm NEW
        checkSettingsDto.setName(appiumCheckSettings.getName());
        checkSettingsDto.setPageId(appiumCheckSettings.getPageId());
        checkSettingsDto.setIgnoreRegions(AppiumCodedRegionReferenceMapper
                .toCodedRegionReferenceList(Arrays.asList(appiumCheckSettings.getIgnoreRegions())));
        checkSettingsDto.setLayoutRegions(AppiumCodedRegionReferenceMapper
                .toCodedRegionReferenceList(Arrays.asList(appiumCheckSettings.getLayoutRegions())));
        checkSettingsDto.setStrictRegions(AppiumCodedRegionReferenceMapper
                .toCodedRegionReferenceList(Arrays.asList(appiumCheckSettings.getStrictRegions())));
        checkSettingsDto.setContentRegions(AppiumCodedRegionReferenceMapper
                .toCodedRegionReferenceList(Arrays.asList(appiumCheckSettings.getContentRegions())));
        checkSettingsDto.setFloatingRegions(AppiumTFloatingRegionMapper.toTFloatingRegionDtoList(Arrays.asList(appiumCheckSettings.getFloatingRegions())));
        checkSettingsDto.setAccessibilityRegions(AppiumTAccessibilityRegionMapper.toTAccessibilityRegionDtoList(Arrays.asList(appiumCheckSettings.getAccessibilityRegions())));
        checkSettingsDto.setAccessibilitySettings(AccessibilitySettingsMapper.toAccessibilitySettingsDto(appiumCheckSettings.getAccessibilityValidation()));
        checkSettingsDto.setMatchLevel(appiumCheckSettings.getMatchLevel() == null ? null : appiumCheckSettings.getMatchLevel().getName());
        checkSettingsDto.setRetryTimeout(config.getMatchTimeout()); //I'm NEW - former matchTimeout
        checkSettingsDto.setSendDom(appiumCheckSettings.isSendDom());
        checkSettingsDto.setUseDom(appiumCheckSettings.isUseDom());
        checkSettingsDto.setEnablePatterns(appiumCheckSettings.isEnablePatterns());
        checkSettingsDto.setIgnoreCaret(appiumCheckSettings.getIgnoreCaret());
        checkSettingsDto.setUfgOptions(VisualGridOptionMapper.toVisualGridOptionDtoList(appiumCheckSettings.getVisualGridOptions())); //I'm NEW - former visualGridOptions
        checkSettingsDto.setLayoutBreakpoints(null); //TODO why I am null?
        checkSettingsDto.setDisableBrowserFetching(appiumCheckSettings.isDisableBrowserFetching());
        checkSettingsDto.setAutProxy(ProxyMapper.toAutProxyDto(config.getAutProxy()));
        checkSettingsDto.setHooks(appiumCheckSettings.getScriptHooks());
        checkSettingsDto.setDensityMetrics(appiumCheckSettings.getDensityMetrics());

        checkSettingsDto.setType(NMGOptionsMapper.toNMGOptionsMapperDtoList(appiumCheckSettings.getNMGOptions()) == null? null : ManagerType.CLASSIC.value);

        return checkSettingsDto;
    }
}
