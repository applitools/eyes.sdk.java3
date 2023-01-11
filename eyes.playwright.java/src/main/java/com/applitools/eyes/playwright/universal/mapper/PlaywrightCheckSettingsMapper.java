package com.applitools.eyes.playwright.universal.mapper;

import com.applitools.ICheckSettings;
import com.applitools.eyes.config.Configuration;
import com.applitools.eyes.playwright.IPlaywrightCheckSettings;
import com.applitools.eyes.playwright.PlaywrightCheckSettings;
import com.applitools.eyes.playwright.universal.Refer;
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
    public static CheckSettingsDto toCheckSettingsDto(ICheckSettings checkSettings, Configuration config, Refer refer) {
        if (!(checkSettings instanceof PlaywrightCheckSettings)) {
            return null;
        }

        PlaywrightCheckSettings playwrightCheckSettings = (PlaywrightCheckSettings) checkSettings;

        CheckSettingsDto checkSettingsDto = new CheckSettingsDto();
        // CheckSettings
        //TODO target region
        checkSettingsDto.setRegion(TRegionMapper.toTRegionFromCheckSettings(playwrightCheckSettings, refer));
        checkSettingsDto.setFrames(TFramesMapper.toTFramesFromCheckSettings(playwrightCheckSettings.getFrameChain(), refer));
        checkSettingsDto.setFully(playwrightCheckSettings.getStitchContent());
        //TODO scroll root element
        checkSettingsDto.setScrollRootElement(TRegionMapper.toTRegionDtoFromSRE(playwrightCheckSettings.getScrollRootElement(), refer));
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
        checkSettingsDto.setPageId(playwrightCheckSettings.getPageId());
        checkSettingsDto.setIgnoreRegions(CodedRegionReferenceMapper
                .toCodedRegionReferenceList(Arrays.asList(playwrightCheckSettings.getIgnoreRegions())));
        checkSettingsDto.setLayoutRegions(CodedRegionReferenceMapper
                .toCodedRegionReferenceList(Arrays.asList(playwrightCheckSettings.getLayoutRegions())));
        checkSettingsDto.setStrictRegions(CodedRegionReferenceMapper
                .toCodedRegionReferenceList(Arrays.asList(playwrightCheckSettings.getStrictRegions())));
        checkSettingsDto.setContentRegions(CodedRegionReferenceMapper
                .toCodedRegionReferenceList(Arrays.asList(playwrightCheckSettings.getContentRegions())));
        checkSettingsDto.setFloatingRegions(TFloatingRegionMapper.toTFloatingRegionDtoList(Arrays.asList(playwrightCheckSettings.getFloatingRegions())));
        //TODO
        checkSettingsDto.setAccessibilityRegions(TAccessibilityRegionMapper.toTAccessibilityRegionDtoList(Arrays.asList(playwrightCheckSettings.getAccessibilityRegions())));
        checkSettingsDto.setAccessibilitySettings(AccessibilitySettingsMapper.toAccessibilitySettingsDto(((PlaywrightCheckSettings) checkSettings).getAccessibilityValidation()));
        checkSettingsDto.setMatchLevel(playwrightCheckSettings.getMatchLevel() == null ? null : playwrightCheckSettings.getMatchLevel().getName());
        checkSettingsDto.setRetryTimeout(config.getMatchTimeout());
        checkSettingsDto.setSendDom(playwrightCheckSettings.isSendDom());
        checkSettingsDto.setUseDom(playwrightCheckSettings.isUseDom());
        checkSettingsDto.setEnablePatterns(playwrightCheckSettings.isEnablePatterns());
        checkSettingsDto.setIgnoreCaret(playwrightCheckSettings.getIgnoreCaret());
        checkSettingsDto.setUfgOptions(VisualGridOptionMapper.toVisualGridOptionDtoList(playwrightCheckSettings.getVisualGridOptions()));
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
