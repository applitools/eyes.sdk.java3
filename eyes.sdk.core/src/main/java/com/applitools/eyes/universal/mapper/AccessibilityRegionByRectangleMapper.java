package com.applitools.eyes.universal.mapper;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.applitools.eyes.AccessibilityRegionByRectangle;
import com.applitools.eyes.universal.dto.AccessibilityRegionByRectangleDto;
import com.applitools.eyes.universal.dto.RegionDto;

/**
 * Accessibility region by rectangle mapper
 */
public class AccessibilityRegionByRectangleMapper {

  public static AccessibilityRegionByRectangleDto toAccessibilityRegionByRectangleDto(AccessibilityRegionByRectangle accRegion) {
    if (accRegion == null) {
      return null;
    }

    AccessibilityRegionByRectangleDto accRegionDto = new AccessibilityRegionByRectangleDto();
    RegionDto region = new RegionDto();
    region.setY(accRegion.getTop());
    region.setX(accRegion.getLeft());
    region.setWidth(accRegion.getWidth());
    region.setHeight(accRegion.getHeight());
    accRegionDto.setRegion(region);
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
