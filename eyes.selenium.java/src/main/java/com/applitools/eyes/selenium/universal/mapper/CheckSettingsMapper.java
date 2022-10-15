package com.applitools.eyes.selenium.universal.mapper;

import com.applitools.ICheckSettings;
import com.applitools.eyes.selenium.Configuration;
import com.applitools.eyes.selenium.fluent.SeleniumCheckSettings;
import com.applitools.eyes.selenium.universal.dto.CheckSettingsDto;

import java.util.Arrays;

import static com.applitools.eyes.selenium.universal.mapper.SettingsMapper.toImageCropRect;
import static com.applitools.eyes.selenium.universal.mapper.SettingsMapper.toNormalizationDto;

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
    checkSettingsDto.setLayoutBreakpoints(seleniumCheckSettings
            .getLayoutBreakpoints().isEmpty() ?
            seleniumCheckSettings.isDefaultLayoutBreakpointsSet() : seleniumCheckSettings.getLayoutBreakpoints());
    checkSettingsDto.setUfgOptions(VisualGridOptionMapper.toVisualGridOptionDtoList(seleniumCheckSettings.getVisualGridOptions()));
    checkSettingsDto.setHooks(seleniumCheckSettings.getScriptHooks());
//    checkSettingsDto.setRenderId(null);
//    checkSettingsDto.setVariationGroupId(seleniumCheckSettings.getVariationGroupId());
//    checkSettingsDto.setTimeout(seleniumCheckSettings.getTimeout());
    checkSettingsDto.setWaitBeforeCapture(seleniumCheckSettings.getWaitBeforeCapture());

    // MatchSettings
//    checkSettingsDto.setExact(null);
    checkSettingsDto.setMatchLevel(seleniumCheckSettings.getMatchLevel() == null ? null : seleniumCheckSettings.getMatchLevel().getName());
    checkSettingsDto.setSendDom(seleniumCheckSettings.isSendDom());
    checkSettingsDto.setUseDom(seleniumCheckSettings.isUseDom());
    checkSettingsDto.setEnablePatterns(seleniumCheckSettings.isEnablePatterns());
    checkSettingsDto.setIgnoreCaret(seleniumCheckSettings.getIgnoreCaret());
    checkSettingsDto.setIgnoreDisplacements(seleniumCheckSettings.isIgnoreDisplacements());
    checkSettingsDto.setAccessibilitySettings(null);
    checkSettingsDto.setIgnoreRegions(CodedRegionReferenceMapper
            .toCodedRegionReferenceList(Arrays.asList(seleniumCheckSettings.getIgnoreRegions())));
    checkSettingsDto.setLayoutRegions(CodedRegionReferenceMapper
            .toCodedRegionReferenceList(Arrays.asList(seleniumCheckSettings.getLayoutRegions())));
    checkSettingsDto.setStrictRegions(CodedRegionReferenceMapper
            .toCodedRegionReferenceList(Arrays.asList(seleniumCheckSettings.getStrictRegions())));
    checkSettingsDto.setContentRegions(CodedRegionReferenceMapper
            .toCodedRegionReferenceList(Arrays.asList(seleniumCheckSettings.getContentRegions())));

    checkSettingsDto.setFloatingRegions(TFloatingRegionMapper.toTFloatingRegionDtoList(Arrays.asList(seleniumCheckSettings.getFloatingRegions())));

    checkSettingsDto.setAccessibilityRegions(TAccessibilityRegionMapper.toTAccessibilityRegionDtoList(Arrays.asList(seleniumCheckSettings.getAccessibilityRegions())));
    checkSettingsDto.setPageId(seleniumCheckSettings.getPageId());
    checkSettingsDto.setLazyLoad(seleniumCheckSettings.getLazyLoadOptions());

    // ScreenshotSettings
    checkSettingsDto.setRegion(TRegionMapper.toTRegionFromCheckSettings(checkSettings));
    checkSettingsDto.setFrames(ContextReferenceMapper.toContextReferenceDtoList(seleniumCheckSettings.getFrameChain()));

    checkSettingsDto.setScrollRootElement(TRegionMapper.toTRegionDtoFromScrolls(seleniumCheckSettings.getScrollRootSelector(),
        seleniumCheckSettings.getScrollRootElement()));

    checkSettingsDto.setFully(seleniumCheckSettings.getStitchContent());


    return checkSettingsDto;
  }

  public static CheckSettingsDto toCheckSettingsDtoV3(ICheckSettings checkSettings, Configuration config) {
    if (!(checkSettings instanceof SeleniumCheckSettings)) {
      return null;
    }

    SeleniumCheckSettings seleniumCheckSettings = (SeleniumCheckSettings) checkSettings;

    CheckSettingsDto checkSettingsDto = new CheckSettingsDto();
    // CheckSettings
    checkSettingsDto.setRegion(TRegionMapper.toTRegionFromCheckSettings(checkSettings));
    checkSettingsDto.setFrames(ContextReferenceMapper.toContextReferenceDtoList(seleniumCheckSettings.getFrameChain()));
    checkSettingsDto.setFully(seleniumCheckSettings.getStitchContent());
    checkSettingsDto.setScrollRootElement(TRegionMapper.toTRegionDtoFromScrolls(seleniumCheckSettings.getScrollRootSelector(),
            seleniumCheckSettings.getScrollRootElement()));
    checkSettingsDto.setStitchMode(config.getStitchMode() == null ? null : config.getStitchMode().getName());
    checkSettingsDto.setHideScrollBars(config.getHideScrollbars());
    checkSettingsDto.setHideCaret(config.getHideCaret());
    checkSettingsDto.setOverlap(config.getStitchOverlap());
    checkSettingsDto.setWaitBeforeCapture(seleniumCheckSettings.getWaitBeforeCapture());
    checkSettingsDto.setLazyLoad(seleniumCheckSettings.getLazyLoadOptions());
    checkSettingsDto.setIgnoreDisplacements(seleniumCheckSettings.isIgnoreDisplacements());
    checkSettingsDto.setNormalization(toNormalizationDto(
            toImageCropRect(config.getCutProvider(), config.getContentInset()), config.getRotation(), config.getScaleRatio()));
    checkSettingsDto.setDebugImages(null); //TODO I'm NEW
    checkSettingsDto.setName(seleniumCheckSettings.getName());
    checkSettingsDto.setPageId(seleniumCheckSettings.getPageId());
    checkSettingsDto.setIgnoreRegions(CodedRegionReferenceMapper
            .toCodedRegionReferenceList(Arrays.asList(seleniumCheckSettings.getIgnoreRegions())));
    checkSettingsDto.setLayoutRegions(CodedRegionReferenceMapper
            .toCodedRegionReferenceList(Arrays.asList(seleniumCheckSettings.getLayoutRegions())));
    checkSettingsDto.setStrictRegions(CodedRegionReferenceMapper
            .toCodedRegionReferenceList(Arrays.asList(seleniumCheckSettings.getStrictRegions())));
    checkSettingsDto.setContentRegions(CodedRegionReferenceMapper
            .toCodedRegionReferenceList(Arrays.asList(seleniumCheckSettings.getContentRegions())));
    checkSettingsDto.setFloatingRegions(TFloatingRegionMapper.toTFloatingRegionDtoList(Arrays.asList(seleniumCheckSettings.getFloatingRegions())));
    checkSettingsDto.setAccessibilityRegions(TAccessibilityRegionMapper.toTAccessibilityRegionDtoList(Arrays.asList(seleniumCheckSettings.getAccessibilityRegions())));
    checkSettingsDto.setAccessibilitySettings(null); //TODO why I am null?
    checkSettingsDto.setMatchLevel(seleniumCheckSettings.getMatchLevel() == null ? null : seleniumCheckSettings.getMatchLevel().getName());
    checkSettingsDto.setRetryTimeout(config.getMatchTimeout()); //I'm NEW - former matchTimeout
    checkSettingsDto.setSendDom(seleniumCheckSettings.isSendDom());
    checkSettingsDto.setUseDom(seleniumCheckSettings.isUseDom());
    checkSettingsDto.setEnablePatterns(seleniumCheckSettings.isEnablePatterns());
    checkSettingsDto.setIgnoreCaret(seleniumCheckSettings.getIgnoreCaret());
    checkSettingsDto.setUfgOptions(VisualGridOptionMapper.toVisualGridOptionDtoList(seleniumCheckSettings.getVisualGridOptions())); //I'm NEW - former visualGridOptions
    checkSettingsDto.setLayoutBreakpoints(seleniumCheckSettings
            .getLayoutBreakpoints().isEmpty() ?
            seleniumCheckSettings.isDefaultLayoutBreakpointsSet() : seleniumCheckSettings.getLayoutBreakpoints());
    checkSettingsDto.setDisableBrowserFetching(seleniumCheckSettings.isDisableBrowserFetching());
    checkSettingsDto.setAutProxy(ProxyMapper.toAutProxyDto(config.getAutProxy()));
    checkSettingsDto.setHooks(seleniumCheckSettings.getScriptHooks());


    return checkSettingsDto;
  }
}
