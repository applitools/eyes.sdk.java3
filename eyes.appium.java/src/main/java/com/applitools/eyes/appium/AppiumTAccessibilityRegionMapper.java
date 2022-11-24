package com.applitools.eyes.appium;

import com.applitools.eyes.AccessibilityRegionByRectangle;
import com.applitools.eyes.GetAccessibilityRegion;
import com.applitools.eyes.selenium.fluent.AccessibilityRegionByElement;
import com.applitools.eyes.selenium.fluent.AccessibilityRegionBySelector;
import com.applitools.eyes.selenium.universal.dto.ElementAccessibilityRegionDto;
import com.applitools.eyes.selenium.universal.dto.ElementRegionDto;
import com.applitools.eyes.universal.dto.*;
import com.applitools.eyes.selenium.universal.mapper.ElementRegionMapper;
import com.applitools.eyes.universal.mapper.RectangleRegionMapper;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.stream.Collectors;

public class AppiumTAccessibilityRegionMapper {

    public static TAccessibilityRegion toTAccessibilityRegionDto(GetAccessibilityRegion getAccessibilityRegion) {
        if (getAccessibilityRegion == null) {
            return null;
        }

        if (getAccessibilityRegion instanceof AccessibilityRegionByRectangle) {
            RectangleAccessibilityRegionDto rectangleAccessibilityRegionDto = new RectangleAccessibilityRegionDto();

            RectangleRegionDto rectangleRegionDto =
                    RectangleRegionMapper.toRectangleRegionDto(((AccessibilityRegionByRectangle) getAccessibilityRegion).getRegion());
            rectangleAccessibilityRegionDto.setRegion(rectangleRegionDto);

            rectangleAccessibilityRegionDto.setType(((AccessibilityRegionByRectangle) getAccessibilityRegion).getType().name());
            return rectangleAccessibilityRegionDto;
        } else if (getAccessibilityRegion instanceof AccessibilityRegionByElement) {
            ElementAccessibilityRegionDto elementAccessibilityRegionDto = new ElementAccessibilityRegionDto();

            WebElement element = ((AccessibilityRegionByElement) getAccessibilityRegion).getElement();
            ElementRegionDto elementRegionDto = ElementRegionMapper.toElementRegionDto(element);
            elementAccessibilityRegionDto.setRegion(elementRegionDto);

            elementAccessibilityRegionDto.setType(((AccessibilityRegionByElement) getAccessibilityRegion).getAccessibilityRegionType().name());
            return elementAccessibilityRegionDto;
        } else if (getAccessibilityRegion instanceof AccessibilityRegionBySelector) {
            SelectorAccessibilityRegionDto selectorAccessibilityRegionDto = new SelectorAccessibilityRegionDto();

            By by = ((AccessibilityRegionBySelector) getAccessibilityRegion).getSelector();
            SelectorRegionDto selectorRegionDto = AppiumSelectorRegionMapper.toAppiumSelectorRegionDto(by);

            selectorAccessibilityRegionDto.setRegion(selectorRegionDto);

            selectorAccessibilityRegionDto.setType(((AccessibilityRegionBySelector) getAccessibilityRegion).getAccessibilityRegionType().name());
            return selectorAccessibilityRegionDto;
        }

        return null;
    }

    public static List<TAccessibilityRegion> toTAccessibilityRegionDtoList(List<GetAccessibilityRegion> getAccessibilityRegionList) {
        if (getAccessibilityRegionList == null || getAccessibilityRegionList.isEmpty()) {
            return null;
        }

        return getAccessibilityRegionList.stream().map(AppiumTAccessibilityRegionMapper::toTAccessibilityRegionDto).collect(Collectors.toList());
    }
}
