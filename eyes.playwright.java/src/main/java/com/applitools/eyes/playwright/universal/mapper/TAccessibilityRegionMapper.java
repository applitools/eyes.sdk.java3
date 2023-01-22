package com.applitools.eyes.playwright.universal.mapper;

import com.applitools.eyes.AccessibilityRegionByRectangle;
import com.applitools.eyes.fluent.GetRegion;
import com.applitools.eyes.playwright.fluent.AccessibilityElement;
import com.applitools.eyes.playwright.fluent.AccessibilitySelector;
import com.applitools.eyes.playwright.universal.Refer;
import com.applitools.eyes.playwright.universal.dto.AccessibilityRegionByElement;
import com.applitools.eyes.playwright.universal.dto.AccessibilityRegionBySelector;
import com.applitools.eyes.universal.Reference;
import com.applitools.eyes.universal.dto.RectangleAccessibilityRegionDto;
import com.applitools.eyes.universal.dto.RectangleRegionDto;
import com.applitools.eyes.universal.dto.TAccessibilityRegion;

import java.util.List;
import java.util.stream.Collectors;

public class TAccessibilityRegionMapper {

    public static TAccessibilityRegion toTAccessibilityRegionDto(GetRegion getAccessibilityRegion, Refer refer, Reference root) {
        if (getAccessibilityRegion == null) {
            return null;
        }

        if (getAccessibilityRegion instanceof AccessibilitySelector) {
            AccessibilitySelector selector = (AccessibilitySelector) getAccessibilityRegion;
            selector.setApplitoolsRefId(refer.ref(getAccessibilityRegion, root));

            AccessibilityRegionBySelector accessibilityRegionBySelector = new AccessibilityRegionBySelector();
            accessibilityRegionBySelector.setRegion(selector);
            accessibilityRegionBySelector.setType(selector.getAccessibilityRegionType().name());
            return accessibilityRegionBySelector;

        } else if (getAccessibilityRegion instanceof AccessibilityElement) {
            AccessibilityElement element = (AccessibilityElement) getAccessibilityRegion;
            element.setApplitoolsRefId(refer.ref(element.getElementHandle(), root));

            AccessibilityRegionByElement accessibilityRegionByElement = new AccessibilityRegionByElement();
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

    public static List<TAccessibilityRegion> toTAccessibilityRegionDtoList(List<GetRegion> getAccessibilityRegionList, Refer refer, Reference root) {
        if (getAccessibilityRegionList == null || getAccessibilityRegionList.isEmpty()) {
            return null;
        }

        return getAccessibilityRegionList.stream()
                .map(reference -> toTAccessibilityRegionDto(reference, refer, root))
                .collect(Collectors.toList());


    }
}
