package com.applitools.eyes.selenium.universal.mapper;

import java.util.List;
import java.util.stream.Collectors;

import com.applitools.eyes.fluent.FloatingRegionByRectangle;
import com.applitools.eyes.fluent.GetFloatingRegion;
import com.applitools.eyes.selenium.fluent.FloatingRegionByElement;
import com.applitools.eyes.selenium.fluent.FloatingRegionBySelector;
import com.applitools.eyes.selenium.fluent.SimpleRegionByElement;
import com.applitools.eyes.selenium.fluent.SimpleRegionBySelector;
import com.applitools.eyes.selenium.universal.dto.ElementFloatingRegionDto;
import com.applitools.eyes.selenium.universal.dto.ElementRegionDto;
import com.applitools.eyes.selenium.universal.dto.RectangleFloatingRegionDto;
import com.applitools.eyes.selenium.universal.dto.RectangleRegionDto;
import com.applitools.eyes.selenium.universal.dto.SelectorFloatingRegionDto;
import com.applitools.eyes.selenium.universal.dto.SelectorRegionDto;
import com.applitools.eyes.selenium.universal.dto.TFloatingRegion;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

/**
 * floating region mapper
 */
public class TFloatingRegionMapper {

  public static TFloatingRegion toTFloatingRegionDto(GetFloatingRegion getFloatingRegion) {
    if (getFloatingRegion == null) {
      return null;
    }

    if (getFloatingRegion instanceof FloatingRegionByRectangle) {
      RectangleFloatingRegionDto rectangleFloatingRegionDto = new RectangleFloatingRegionDto();

      RectangleRegionDto rectangleRegionDto = RectangleRegionMapper.toRectangleRegionDto(((FloatingRegionByRectangle) getFloatingRegion).getRegion());
      rectangleFloatingRegionDto.setRegion(rectangleRegionDto);

      rectangleFloatingRegionDto.setMaxUpOffset(((FloatingRegionByRectangle) getFloatingRegion).getMaxUpOffset());
      rectangleFloatingRegionDto.setMaxDownOffset(((FloatingRegionByRectangle) getFloatingRegion).getMaxDownOffset());
      rectangleFloatingRegionDto.setMaxLeftOffset(((FloatingRegionByRectangle) getFloatingRegion).getMaxLeftOffset());
      rectangleFloatingRegionDto.setMaxRightOffset(((FloatingRegionByRectangle) getFloatingRegion).getMaxRightOffset());
      return rectangleFloatingRegionDto;
    } else if (getFloatingRegion instanceof FloatingRegionByElement) {
      ElementFloatingRegionDto elementFloatingRegionDto = new ElementFloatingRegionDto();

      WebElement element = ((FloatingRegionByElement) getFloatingRegion).getElement();
      ElementRegionDto elementRegionDto = ElementRegionMapper.toElementRegionDto(element);
      elementFloatingRegionDto.setRegion(elementRegionDto);

      elementFloatingRegionDto.setMaxUpOffset(((FloatingRegionByElement) getFloatingRegion).getMaxUpOffset());
      elementFloatingRegionDto.setMaxDownOffset(((FloatingRegionByElement) getFloatingRegion).getMaxDownOffset());
      elementFloatingRegionDto.setMaxLeftOffset(((FloatingRegionByElement) getFloatingRegion).getMaxLeftOffset());
      elementFloatingRegionDto.setMaxRightOffset(((FloatingRegionByElement) getFloatingRegion).getMaxRightOffset());
      return elementFloatingRegionDto;
    } else if (getFloatingRegion instanceof FloatingRegionBySelector) {
      SelectorFloatingRegionDto selectorFloatingRegionDto = new SelectorFloatingRegionDto();

      By by = ((FloatingRegionBySelector) getFloatingRegion).getSelector();
      SelectorRegionDto selectorRegionDto = SelectorRegionMapper.toSelectorRegionDto(by);
      selectorFloatingRegionDto.setRegion(selectorRegionDto);

      selectorFloatingRegionDto.setMaxUpOffset(((FloatingRegionBySelector) getFloatingRegion).getMaxUpOffset());
      selectorFloatingRegionDto.setMaxDownOffset(((FloatingRegionBySelector) getFloatingRegion).getMaxDownOffset());
      selectorFloatingRegionDto.setMaxLeftOffset(((FloatingRegionBySelector) getFloatingRegion).getMaxLeftOffset());
      selectorFloatingRegionDto.setMaxRightOffset(((FloatingRegionBySelector) getFloatingRegion).getMaxRightOffset());
      return selectorFloatingRegionDto;
    }

    return null;

  }

  public static List<TFloatingRegion> toTFloatingRegionDtoList(List<GetFloatingRegion> getFloatingRegionList) {
    if (getFloatingRegionList == null || getFloatingRegionList.isEmpty()) {
      return null;
    }

    return getFloatingRegionList.stream().map(TFloatingRegionMapper::toTFloatingRegionDto).collect(Collectors.toList());
  }
}
