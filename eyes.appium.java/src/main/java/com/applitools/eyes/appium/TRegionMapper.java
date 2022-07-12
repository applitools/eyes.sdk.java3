package com.applitools.eyes.appium;

import com.applitools.ICheckSettings;
import com.applitools.eyes.Region;
import com.applitools.eyes.selenium.universal.dto.TRegion;
import com.applitools.eyes.selenium.universal.mapper.ElementRegionMapper;
import com.applitools.eyes.selenium.universal.mapper.RectangleRegionMapper;
import com.applitools.eyes.selenium.universal.mapper.SelectorRegionMapper;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

public class TRegionMapper extends com.applitools.eyes.selenium.universal.mapper.TRegionMapper {

    public static TRegion toTRegionFromCheckSettings(ICheckSettings checkSettings) {
        if (!(checkSettings instanceof AppiumCheckSettings)) {
            return null;
        }

        AppiumCheckSettings appiumCheckSettings = (AppiumCheckSettings) checkSettings;
        By by = appiumCheckSettings.getTargetSelector();

        if (by != null) {
            return AppiumSelectorRegionMapper.toAppiumSelectorRegionDto(by);
        }

        WebElement element = appiumCheckSettings.getTargetElement();
        if (element != null) {
            return ElementRegionMapper.toElementRegionDto(element);
        }

        Region region = appiumCheckSettings.getTargetRegion();

        if (region != null) {
            return RectangleRegionMapper.toRectangleRegionDto(region);
        }

        return null;
    }

    public static TRegion toTRegionDtoFromScrolls(By selector, WebElement element) {
        if (selector != null) {
            return AppiumSelectorRegionMapper.toAppiumSelectorRegionDto(selector);
        }

        if (element != null) {
            return ElementRegionMapper.toElementRegionDto(element);
        }
        return null;
    }
}
