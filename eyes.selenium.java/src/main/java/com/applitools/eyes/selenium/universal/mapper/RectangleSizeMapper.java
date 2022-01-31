package com.applitools.eyes.selenium.universal.mapper;

import com.applitools.eyes.RectangleSize;
import com.applitools.eyes.selenium.universal.dto.RectangleSizeDto;

/**
 * @author Kanan
 */
public class RectangleSizeMapper {

  public static RectangleSize toRectangleSize(RectangleSizeDto rectangleSizeDto) {
    if (rectangleSizeDto == null) {
      return null;
    }

    return new RectangleSize(rectangleSizeDto.getWidth(), rectangleSizeDto.getHeight());
  }
}
