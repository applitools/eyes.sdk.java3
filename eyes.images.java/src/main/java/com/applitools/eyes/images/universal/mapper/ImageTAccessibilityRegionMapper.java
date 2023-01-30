package com.applitools.eyes.images.universal.mapper;

import com.applitools.eyes.AccessibilityRegionByRectangle;
import com.applitools.eyes.fluent.GetRegion;
import com.applitools.eyes.universal.dto.*;
import com.applitools.eyes.universal.mapper.RectangleRegionMapper;

import java.util.List;
import java.util.stream.Collectors;

public class ImageTAccessibilityRegionMapper {

    public static TAccessibilityRegion toTAccessibilityRegionDto(GetRegion getAccessibilityRegion) {
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

        return null;
    }

    public static List<TAccessibilityRegion> toTAccessibilityRegionDtoList(List<GetRegion> getAccessibilityRegionList) {
        if (getAccessibilityRegionList == null || getAccessibilityRegionList.isEmpty()) {
            return null;
        }

        return getAccessibilityRegionList.stream().map(ImageTAccessibilityRegionMapper::toTAccessibilityRegionDto).collect(Collectors.toList());
    }
}
