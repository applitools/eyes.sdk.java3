package com.applitools.eyes.playwright.universal.mapper;

import com.applitools.ICheckSettings;
import com.applitools.eyes.config.Configuration;
import com.applitools.eyes.playwright.IPlaywrightCheckSettings;
import com.applitools.eyes.playwright.PlaywrightCheckSettings;
import com.applitools.eyes.universal.dto.CheckSettingsDto;
import com.applitools.eyes.universal.dto.DebugScreenshotHandlerDto;
import com.applitools.eyes.universal.dto.ImageCropRectDto;
import com.applitools.eyes.universal.mapper.AccessibilitySettingsMapper;
import com.applitools.eyes.universal.mapper.ProxyMapper;
import com.applitools.eyes.universal.mapper.VisualGridOptionMapper;

import java.util.Arrays;

import static com.applitools.eyes.universal.mapper.SettingsMapper.toImageCropRect;
import static com.applitools.eyes.universal.mapper.SettingsMapper.toNormalizationDto;

public class PlaywrightCheckSettingsMapper {
    public static CheckSettingsDto toCheckSettingsDto(ICheckSettings checkSettings, Configuration config) {
        if (!(checkSettings instanceof PlaywrightCheckSettings)) {
            return null;
        }

        PlaywrightCheckSettings playwrightCheckSettings = (PlaywrightCheckSettings) checkSettings;

        CheckSettingsDto checkSettingsDto = new CheckSettingsDto();
        // CheckSettings
//        checkSettingsDto.setRegion(TRegionMapper.toTRegionFromCheckSettings(checkSettings));
//        checkSettingsDto.setFrames(ContextReferenceMapper.toContextReferenceDtoList(playwrightCheckSettings.getFrameChain()));
        checkSettingsDto.setFully(playwrightCheckSettings.getStitchContent());
//        checkSettingsDto.setScrollRootElement(TRegionMapper.toTRegionDtoFromScrolls(playwrightCheckSettings.getScrollRootSelector(),
//                playwrightCheckSettings.getScrollRootElement()));
        checkSettingsDto.setStitchMode(config.getStitchMode() == null ? null : config.getStitchMode().getName());
        checkSettingsDto.setHideScrollbars(config.getHideScrollbars());
        checkSettingsDto.setHideCaret(config.getHideCaret());
        ImageCropRectDto overlap = new ImageCropRectDto();
        overlap.setBottom(config.getStitchOverlap());
        checkSettingsDto.setOverlap(config.getStitchOverlap() == null ? null : overlap);
        checkSettingsDto.setWaitBeforeCapture(playwrightCheckSettings.getWaitBeforeCapture());
        checkSettingsDto.setLazyLoad(playwrightCheckSettings.getLazyLoadOptions());
        checkSettingsDto.setIgnoreDisplacements(playwrightCheckSettings.isIgnoreDisplacements());
        checkSettingsDto.setNormalization(toNormalizationDto(
                toImageCropRect(config.getCutProvider(), config.getContentInset()), config.getRotation(), config.getScaleRatio()));
        checkSettingsDto.setDebugImages(new DebugScreenshotHandlerDto(config.getSaveDebugScreenshots(),
                config.getDebugScreenshotsPath(), config.getDebugScreenshotsPrefix()));
        checkSettingsDto.setName(playwrightCheckSettings.getName());
//        checkSettingsDto.setPageId(playwrightCheckSettings.getPageId());
//        checkSettingsDto.setIgnoreRegions(CodedRegionReferenceMapper
//                .toCodedRegionReferenceList(Arrays.asList(playwrightCheckSettings.getIgnoreRegions())));
//        checkSettingsDto.setLayoutRegions(CodedRegionReferenceMapper
//                .toCodedRegionReferenceList(Arrays.asList(playwrightCheckSettings.getLayoutRegions())));
//        checkSettingsDto.setStrictRegions(CodedRegionReferenceMapper
//                .toCodedRegionReferenceList(Arrays.asList(playwrightCheckSettings.getStrictRegions())));
//        checkSettingsDto.setContentRegions(CodedRegionReferenceMapper
//                .toCodedRegionReferenceList(Arrays.asList(playwrightCheckSettings.getContentRegions())));
//        checkSettingsDto.setFloatingRegions(TFloatingRegionMapper.toTFloatingRegionDtoList(Arrays.asList(playwrightCheckSettings.getFloatingRegions())));
//        checkSettingsDto.setAccessibilityRegions(TAccessibilityRegionMapper.toTAccessibilityRegionDtoList(Arrays.asList(playwrightCheckSettings.getAccessibilityRegions())));
//        checkSettingsDto.setAccessibilitySettings(AccessibilitySettingsMapper.toAccessibilitySettingsDto(((SeleniumCheckSettings) checkSettings).getAccessibilityValidation()));
        checkSettingsDto.setMatchLevel(playwrightCheckSettings.getMatchLevel() == null ? null : playwrightCheckSettings.getMatchLevel().getName());
        checkSettingsDto.setRetryTimeout(config.getMatchTimeout()); //I'm NEW - former matchTimeout
        checkSettingsDto.setSendDom(playwrightCheckSettings.isSendDom());
        checkSettingsDto.setUseDom(playwrightCheckSettings.isUseDom());
        checkSettingsDto.setEnablePatterns(playwrightCheckSettings.isEnablePatterns());
        checkSettingsDto.setIgnoreCaret(playwrightCheckSettings.getIgnoreCaret());
        checkSettingsDto.setUfgOptions(VisualGridOptionMapper.toVisualGridOptionDtoList(playwrightCheckSettings.getVisualGridOptions())); //I'm NEW - former visualGridOptions
        checkSettingsDto.setLayoutBreakpoints(playwrightCheckSettings
                .getLayoutBreakpoints().isEmpty() ?
                playwrightCheckSettings.isDefaultLayoutBreakpointsSet() : playwrightCheckSettings.getLayoutBreakpoints());
        checkSettingsDto.setDisableBrowserFetching(playwrightCheckSettings.isDisableBrowserFetching());
        checkSettingsDto.setAutProxy(ProxyMapper.toAutProxyDto(config.getAutProxy()));
        checkSettingsDto.setHooks(playwrightCheckSettings.getScriptHooks());
        checkSettingsDto.setUserCommandId(playwrightCheckSettings.getVariationGroupId());

        return checkSettingsDto;
    }
}
