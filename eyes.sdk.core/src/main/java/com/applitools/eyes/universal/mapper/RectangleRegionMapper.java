package com.applitools.eyes.universal.mapper;

import com.applitools.eyes.Region;
import com.applitools.eyes.universal.dto.RectangleRegionDto;

/**
 * rectangle region mapper
 */
public class RectangleRegionMapper {

   public static RectangleRegionDto toRectangleRegionDto(Region region) {
    if (region == null) {
      return null;
    }

    RectangleRegionDto rectangleRegionDto = new RectangleRegionDto();
    rectangleRegionDto.setX(region.getLeft());
    rectangleRegionDto.setY(region.getTop());
    rectangleRegionDto.setHeight(region.getHeight());
    rectangleRegionDto.setWidth(region.getWidth());
    return rectangleRegionDto;
  }
}
