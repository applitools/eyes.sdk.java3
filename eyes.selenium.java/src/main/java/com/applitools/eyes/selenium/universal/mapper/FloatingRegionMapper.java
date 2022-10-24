package com.applitools.eyes.selenium.universal.mapper;

import java.util.List;
import java.util.stream.Collectors;

import com.applitools.eyes.fluent.FloatingRegionByRectangle;
import com.applitools.eyes.fluent.GetFloatingRegion;
import com.applitools.eyes.selenium.fluent.FloatingRegionByElement;
import com.applitools.eyes.selenium.fluent.FloatingRegionBySelector;
import com.applitools.eyes.selenium.universal.dto.FloatingRegionDto;
import com.applitools.eyes.universal.mapper.RegionMapper;

/**
 * floating region mapper
 */
public class FloatingRegionMapper {

  public static FloatingRegionDto toFloatingRegionDto(GetFloatingRegion getFloatingRegion) {
    if (getFloatingRegion == null) {
      return null;
    }

    FloatingRegionDto floatingRegionDto = new FloatingRegionDto();

    if (getFloatingRegion instanceof FloatingRegionByRectangle) {
      floatingRegionDto.setRegion(RegionMapper.toRegionDto(((FloatingRegionByRectangle)getFloatingRegion).getRegion()));
      floatingRegionDto.setMaxUpOffset(((FloatingRegionByRectangle)getFloatingRegion).getMaxUpOffset());
      floatingRegionDto.setMaxDownOffset(((FloatingRegionByRectangle)getFloatingRegion).getMaxDownOffset());
      floatingRegionDto.setMaxLeftOffset(((FloatingRegionByRectangle)getFloatingRegion).getMaxLeftOffset());
      floatingRegionDto.setMaxRightOffset(((FloatingRegionByRectangle)getFloatingRegion).getMaxRightOffset());
    } else if (getFloatingRegion instanceof FloatingRegionByElement) {
      floatingRegionDto.setElement(((FloatingRegionByElement)getFloatingRegion).getElement());
      floatingRegionDto.setMaxUpOffset(((FloatingRegionByElement)getFloatingRegion).getMaxUpOffset());
      floatingRegionDto.setMaxDownOffset(((FloatingRegionByElement)getFloatingRegion).getMaxDownOffset());
      floatingRegionDto.setMaxLeftOffset(((FloatingRegionByElement)getFloatingRegion).getMaxLeftOffset());
      floatingRegionDto.setMaxRightOffset(((FloatingRegionByElement)getFloatingRegion).getMaxRightOffset());
    } else if (getFloatingRegion instanceof FloatingRegionBySelector) {
      floatingRegionDto.setSelector(((FloatingRegionBySelector)getFloatingRegion).getSelector());
      floatingRegionDto.setMaxUpOffset(((FloatingRegionBySelector)getFloatingRegion).getMaxUpOffset());
      floatingRegionDto.setMaxDownOffset(((FloatingRegionBySelector)getFloatingRegion).getMaxDownOffset());
      floatingRegionDto.setMaxLeftOffset(((FloatingRegionBySelector)getFloatingRegion).getMaxLeftOffset());
      floatingRegionDto.setMaxRightOffset(((FloatingRegionBySelector)getFloatingRegion).getMaxRightOffset());
    }

    return floatingRegionDto;
  }

  public static List<FloatingRegionDto> toFloatingRegionDtoList(List<GetFloatingRegion> getFloatingRegionList) {
    if (getFloatingRegionList == null || getFloatingRegionList.isEmpty()) {
      return null;
    }

    return getFloatingRegionList.stream().map(FloatingRegionMapper::toFloatingRegionDto).collect(Collectors.toList());

  }
}
