package com.applitools.eyes.images;

import com.applitools.ICheckSettings;
import com.applitools.eyes.Region;
import com.applitools.eyes.config.Configuration;
import com.applitools.eyes.selenium.fluent.SeleniumCheckSettings;
import com.applitools.eyes.selenium.universal.dto.CheckSettingsDto;
import com.applitools.eyes.selenium.universal.dto.DebugScreenshotHandlerDto;
import com.applitools.eyes.selenium.universal.dto.TRegion;
import com.applitools.eyes.selenium.universal.mapper.*;

import java.util.Arrays;

import static com.applitools.eyes.selenium.universal.mapper.SettingsMapper.toImageCropRect;
import static com.applitools.eyes.selenium.universal.mapper.SettingsMapper.toNormalizationDto;

public class ImagesCheckSettingsMapper {

    public static CheckSettingsDto toCheckSettingsDto(ICheckSettings checkSettings, Configuration config) {
        if (!(checkSettings instanceof ImagesCheckSettings)) {
            return null;
        }

        return toCheckSettingsDto(checkSettings, config, null);
    }

    public static CheckSettingsDto toCheckSettingsDto(ICheckSettings checkSettings, Configuration config, Region region) {

        ImagesCheckSettings imagesCheckSettings = (ImagesCheckSettings) checkSettings;
        CheckSettingsDto checkSettingsDto = new CheckSettingsDto();

        checkSettingsDto.setRegion(toTRegionFromRegion(region));
        checkSettingsDto.setFrames(null);
        checkSettingsDto.setFully(imagesCheckSettings.getStitchContent());
        checkSettingsDto.setScrollRootElement(null);
        checkSettingsDto.setStitchMode(config.getStitchMode() == null ? null : config.getStitchMode().getName());
        checkSettingsDto.setHideScrollBars(config.getHideScrollbars());
        checkSettingsDto.setHideCaret(config.getHideCaret());
        checkSettingsDto.setOverlap(config.getStitchOverlap());
        checkSettingsDto.setWaitBeforeCapture(imagesCheckSettings.getWaitBeforeCapture());
        checkSettingsDto.setLazyLoad(imagesCheckSettings.getLazyLoadOptions());
        checkSettingsDto.setIgnoreDisplacements(imagesCheckSettings.isIgnoreDisplacements());
        checkSettingsDto.setNormalization(toNormalizationDto(
                toImageCropRect(config.getCutProvider(), config.getContentInset()), config.getRotation(), config.getScaleRatio()));

        checkSettingsDto.setDebugImages(new DebugScreenshotHandlerDto(config.getSaveDebugScreenshots(),
                config.getDebugScreenshotsPath(), config.getDebugScreenshotsPrefix()));
        checkSettingsDto.setName(imagesCheckSettings.getName());
        checkSettingsDto.setPageId(null);
        checkSettingsDto.setIgnoreRegions(CodedRegionReferenceMapper
                .toCodedRegionReferenceList(Arrays.asList(imagesCheckSettings.getIgnoreRegions())));
        checkSettingsDto.setLayoutRegions(CodedRegionReferenceMapper
                .toCodedRegionReferenceList(Arrays.asList(imagesCheckSettings.getLayoutRegions())));
        checkSettingsDto.setStrictRegions(CodedRegionReferenceMapper
                .toCodedRegionReferenceList(Arrays.asList(imagesCheckSettings.getStrictRegions())));
        checkSettingsDto.setContentRegions(CodedRegionReferenceMapper
                .toCodedRegionReferenceList(Arrays.asList(imagesCheckSettings.getContentRegions())));
        checkSettingsDto.setFloatingRegions(TFloatingRegionMapper.toTFloatingRegionDtoList(Arrays.asList(imagesCheckSettings.getFloatingRegions())));
        checkSettingsDto.setAccessibilityRegions(TAccessibilityRegionMapper.toTAccessibilityRegionDtoList(Arrays.asList(imagesCheckSettings.getAccessibilityRegions())));
        checkSettingsDto.setAccessibilitySettings(AccessibilitySettingsMapper.toAccessibilitySettingsDto(imagesCheckSettings.getAccessibilityValidation()));
        checkSettingsDto.setMatchLevel(imagesCheckSettings.getMatchLevel() == null ? null : imagesCheckSettings.getMatchLevel().getName());
        checkSettingsDto.setRetryTimeout(config.getMatchTimeout()); //I'm NEW - former matchTimeout
        checkSettingsDto.setSendDom(imagesCheckSettings.isSendDom());
        checkSettingsDto.setUseDom(imagesCheckSettings.isUseDom());
        checkSettingsDto.setEnablePatterns(imagesCheckSettings.isEnablePatterns());
        checkSettingsDto.setIgnoreCaret(imagesCheckSettings.getIgnoreCaret());
        checkSettingsDto.setUfgOptions(VisualGridOptionMapper.toVisualGridOptionDtoList(imagesCheckSettings.getVisualGridOptions())); //I'm NEW - former visualGridOptions
        checkSettingsDto.setLayoutBreakpoints(imagesCheckSettings
                .getLayoutBreakpoints().isEmpty() ?
                imagesCheckSettings.isDefaultLayoutBreakpointsSet() : imagesCheckSettings.getLayoutBreakpoints());
        checkSettingsDto.setDisableBrowserFetching(imagesCheckSettings.isDisableBrowserFetching());
        checkSettingsDto.setAutProxy(ProxyMapper.toAutProxyDto(config.getAutProxy()));
        checkSettingsDto.setHooks(imagesCheckSettings.getScriptHooks());

        return checkSettingsDto;
    }

    private static TRegion toTRegionFromRegion(Region region) {
        if (region != null) {
            return RectangleRegionMapper.toRectangleRegionDto(region);
        }

        return null;
    }
}
