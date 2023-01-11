package com.applitools.eyes.playwright.universal.mapper;

import com.applitools.eyes.AccessibilityRegionByRectangle;
import com.applitools.eyes.GetAccessibilityRegion;
import com.applitools.eyes.universal.dto.*;
import com.applitools.eyes.universal.mapper.RectangleRegionMapper;

import java.util.List;
import java.util.stream.Collectors;

public class TAccessibilityRegionMapper {

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
        }
//        else if (getAccessibilityRegion instanceof AccessibilityRegionByElement) {
//            ElementAccessibilityRegionDto elementAccessibilityRegionDto = new ElementAccessibilityRegionDto();
//
//            WebElement element = ((AccessibilityRegionByElement) getAccessibilityRegion).getElement();
//            ElementRegionDto elementRegionDto = ElementRegionMapper.toElementRegionDto(element);
//            elementAccessibilityRegionDto.setRegion(elementRegionDto);
//
//            elementAccessibilityRegionDto.setType(((AccessibilityRegionByElement) getAccessibilityRegion).getAccessibilityRegionType().name());
//            return elementAccessibilityRegionDto;
//        } else if (getAccessibilityRegion instanceof AccessibilityRegionBySelector) {
//            SelectorAccessibilityRegionDto selectorAccessibilityRegionDto = new SelectorAccessibilityRegionDto();
//
//            By by = ((AccessibilityRegionBySelector) getAccessibilityRegion).getSelector();
//            SelectorRegionDto selectorRegionDto = SelectorRegionMapper.toSelectorRegionDto(by);
//
//            selectorAccessibilityRegionDto.setRegion(selectorRegionDto);
//
//            selectorAccessibilityRegionDto.setType(((AccessibilityRegionBySelector) getAccessibilityRegion).getAccessibilityRegionType().name());
//            return selectorAccessibilityRegionDto;
//        }

        return null;
    }

    //TODO
    public static List<TAccessibilityRegion> toTAccessibilityRegionDtoList(List<GetAccessibilityRegion> getAccessibilityRegionList) {
        if (getAccessibilityRegionList == null || getAccessibilityRegionList.isEmpty()) {
            return null;
        }

        return getAccessibilityRegionList.stream().map(TAccessibilityRegionMapper::toTAccessibilityRegionDto).collect(Collectors.toList());
    }
}
