package com.applitools.eyes.playwright.universal.mapper;

import com.applitools.eyes.AccessibilityRegionByRectangle;
import com.applitools.eyes.fluent.GetRegion;
import com.applitools.eyes.playwright.fluent.AccessibilityElement;
import com.applitools.eyes.playwright.fluent.AccessibilitySelector;
import com.applitools.eyes.playwright.universal.Refer;
import com.applitools.eyes.playwright.universal.dto.AccessibilityRegionByElement;
import com.applitools.eyes.playwright.universal.dto.AccessibilityRegionBySelector;
import com.applitools.eyes.universal.dto.RectangleAccessibilityRegionDto;
import com.applitools.eyes.universal.dto.RectangleRegionDto;
import com.applitools.eyes.universal.dto.TAccessibilityRegion;

import java.util.List;
import java.util.stream.Collectors;

public class TAccessibilityRegionMapper {

    public static TAccessibilityRegion toTAccessibilityRegionDto(GetRegion getAccessibilityRegion, Refer refer) {
        if (getAccessibilityRegion == null) {
            return null;
        }

        if (getAccessibilityRegion instanceof AccessibilitySelector) {
            AccessibilityRegionBySelector accessibilityRegionBySelector = new AccessibilityRegionBySelector();
            AccessibilitySelector selector = (AccessibilitySelector) getAccessibilityRegion;
            selector.setApplitoolsRefId(refer.ref(getAccessibilityRegion));

            accessibilityRegionBySelector.setRegion(selector);
            accessibilityRegionBySelector.setType(selector.getAccessibilityRegionType().name());
            return accessibilityRegionBySelector;

        } else if (getAccessibilityRegion instanceof AccessibilityElement) {
            AccessibilityRegionByElement accessibilityRegionByElement = new AccessibilityRegionByElement();
            AccessibilityElement element = (AccessibilityElement) getAccessibilityRegion;
            element.setApplitoolsRefId(refer.ref(getAccessibilityRegion));

            accessibilityRegionByElement.setRegion(element);
            accessibilityRegionByElement.setType(element.getAccessibilityRegionType().name());
            return accessibilityRegionByElement;

        } else if (getAccessibilityRegion instanceof AccessibilityRegionByRectangle) {
            RectangleAccessibilityRegionDto rectangleAccessibilityRegionDto = new RectangleAccessibilityRegionDto();
            AccessibilityRegionByRectangle accessibilityRegionByRectangle = (AccessibilityRegionByRectangle) getAccessibilityRegion;

            RectangleRegionDto rectangleRegionDto = new RectangleRegionDto();
            rectangleRegionDto.setX(accessibilityRegionByRectangle.getLeft());
            rectangleRegionDto.setY(accessibilityRegionByRectangle.getTop());
            rectangleRegionDto.setHeight(accessibilityRegionByRectangle.getHeight());
            rectangleRegionDto.setWidth(accessibilityRegionByRectangle.getWidth());

            rectangleAccessibilityRegionDto.setRegion(rectangleRegionDto);
            rectangleAccessibilityRegionDto.setType(accessibilityRegionByRectangle.getType().name());
            return rectangleAccessibilityRegionDto;
        }

        return null;
    }

    public static List<TAccessibilityRegion> toTAccessibilityRegionDtoList(List<GetRegion> getAccessibilityRegionList, Refer refer) {
        if (getAccessibilityRegionList == null || getAccessibilityRegionList.isEmpty()) {
            return null;
        }

        return getAccessibilityRegionList.stream()
                .map(reference -> toTAccessibilityRegionDto(reference, refer))
                .collect(Collectors.toList());


    }
}