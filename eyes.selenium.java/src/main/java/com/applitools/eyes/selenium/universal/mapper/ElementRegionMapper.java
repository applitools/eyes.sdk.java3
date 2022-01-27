package com.applitools.eyes.selenium.universal.mapper;

import com.applitools.eyes.selenium.universal.dto.ElementRegionDto;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebElement;

/**
 * element region mapper
 */
public class ElementRegionMapper {

  public static ElementRegionDto toElementRegionDto(WebElement element) {
    if (!(element instanceof RemoteWebElement)) {
      return null;
    }
    ElementRegionDto elementRegionDto = new ElementRegionDto();
    elementRegionDto.setElementId(((RemoteWebElement) element).getId());
    return elementRegionDto;
  }
}
