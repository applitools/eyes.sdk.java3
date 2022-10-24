package com.applitools.eyes.selenium.universal.mapper;

import com.applitools.eyes.AccessibilityRegionByRectangle;
import com.applitools.eyes.GetAccessibilityRegion;
import com.applitools.eyes.selenium.fluent.AccessibilityRegionByElement;
import com.applitools.eyes.selenium.fluent.AccessibilityRegionBySelector;
import com.applitools.eyes.selenium.universal.dto.AccessibilityRegionDto;
import com.applitools.eyes.universal.mapper.RegionMapper;

/**
 * accessibility region mapper
 */
public class AccessibilityRegionMapper {

  public static AccessibilityRegionDto toAccessibilityRegionDto(GetAccessibilityRegion getAccessibilityRegion) {
    if (getAccessibilityRegion == null) {
      return null;
    }

    AccessibilityRegionDto accessibilityRegionDto = new AccessibilityRegionDto();

    if (getAccessibilityRegion instanceof AccessibilityRegionByRectangle) {
      accessibilityRegionDto.setRegion(RegionMapper.toRegionDto(((AccessibilityRegionByRectangle)getAccessibilityRegion).getRegion()));
      accessibilityRegionDto.setRegionType(((AccessibilityRegionByRectangle)getAccessibilityRegion).getType().name());
    } else if (getAccessibilityRegion instanceof AccessibilityRegionByElement) {
      accessibilityRegionDto.setElement(((AccessibilityRegionByElement)getAccessibilityRegion).getElement());
      accessibilityRegionDto.setRegionType(((AccessibilityRegionByElement)getAccessibilityRegion).getAccessibilityRegionType().name());
    } else if (getAccessibilityRegion instanceof AccessibilityRegionBySelector) {
      accessibilityRegionDto.setSelector(((AccessibilityRegionBySelector)getAccessibilityRegion).getSelector());
      accessibilityRegionDto.setRegionType(((AccessibilityRegionBySelector)getAccessibilityRegion).getAccessibilityRegionType().name());
    }

    return accessibilityRegionDto;
  }
}
