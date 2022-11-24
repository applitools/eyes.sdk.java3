package com.applitools.eyes.selenium.universal.mapper;

import java.util.List;
import java.util.stream.Collectors;

import com.applitools.eyes.fluent.GetSimpleRegion;
import com.applitools.eyes.fluent.SimpleRegionByRectangle;
import com.applitools.eyes.selenium.fluent.SimpleRegionByElement;
import com.applitools.eyes.selenium.fluent.SimpleRegionBySelector;
import com.applitools.eyes.selenium.universal.dto.SimpleRegionDto;
import com.applitools.eyes.universal.mapper.RegionMapper;

/**
 * simple region mapper
 */
public class SimpleRegionMapper {

  public static SimpleRegionDto toSimpleRegionDto(GetSimpleRegion getSimpleRegion) {
    if (getSimpleRegion == null) {
      return null;
    }

    SimpleRegionDto simpleRegionDto = new SimpleRegionDto();

    if (getSimpleRegion instanceof SimpleRegionByRectangle) {
      simpleRegionDto.setRegion(RegionMapper.toRegionDto(((SimpleRegionByRectangle)getSimpleRegion).getRegion()));
    } else if (getSimpleRegion instanceof SimpleRegionByElement) {
      simpleRegionDto.setElement(((SimpleRegionByElement) getSimpleRegion).getElement());
    } else if (getSimpleRegion instanceof SimpleRegionBySelector) {
      //simpleRegionDto.setSelector(((SimpleRegionBySelector)getSimpleRegion).getSelector());
    }

    return simpleRegionDto;
  }

  public static List<SimpleRegionDto> toSimpleRegionDtoList(List<GetSimpleRegion> getSimpleRegionList) {
    return getSimpleRegionList.stream().map(SimpleRegionMapper::toSimpleRegionDto).collect(Collectors.toList());
  }

}
