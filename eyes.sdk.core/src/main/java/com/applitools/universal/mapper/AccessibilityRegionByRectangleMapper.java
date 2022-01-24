package com.applitools.universal.mapper;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.applitools.eyes.AccessibilityRegionByRectangle;
import com.applitools.universal.dto.AccessibilityRegionByRectangleDto;

/**
 * Accessibility region by rectangle mapper
 */
public class AccessibilityRegionByRectangleMapper {

  public static AccessibilityRegionByRectangleDto toAccessibilityRegionByRectangleDto(AccessibilityRegionByRectangle accRegion) {
    if (accRegion == null) {
      return null;
    }

    AccessibilityRegionByRectangleDto accRegionDto = new AccessibilityRegionByRectangleDto();
    accRegionDto.setLeft(accRegion.getLeft());
    accRegionDto.setTop(accRegion.getTop());
    accRegionDto.setWidth(accRegion.getWidth());
    accRegionDto.setHeight(accRegion.getHeight());
    accRegionDto.setType(accRegion.getType().name());
    return accRegionDto;
  }

  public static List<AccessibilityRegionByRectangleDto> toAccessibilityRegionByRectangleDtoList(AccessibilityRegionByRectangle[] accRegions) {
    if (accRegions == null || accRegions.length == 0) {
      return null;
    }

    List<AccessibilityRegionByRectangle> accRegionsList = Arrays.asList(accRegions);

    return accRegionsList
        .stream()
        .map(AccessibilityRegionByRectangleMapper::toAccessibilityRegionByRectangleDto)
        .collect(Collectors.toList());
  }


}
