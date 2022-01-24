package com.applitools.universal.mapper;

import com.applitools.eyes.RectangleSize;
import com.applitools.universal.dto.RectangleSizeDto;

/**
 * rectangle size mapper
 */
public class ViewportSizeMapper {

  public static RectangleSizeDto toViewportSizeDto(RectangleSize rectangleSize) {
    if (rectangleSize == null) {
      return null;
    }

    RectangleSizeDto rectangleSizeDto = new RectangleSizeDto();
    rectangleSizeDto.setWidth(rectangleSize.getWidth());
    rectangleSizeDto.setHeight(rectangleSize.getHeight());
    return rectangleSizeDto;
  }
}
