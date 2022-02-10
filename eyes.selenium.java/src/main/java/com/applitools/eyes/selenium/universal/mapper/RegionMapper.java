package com.applitools.eyes.selenium.universal.mapper;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.applitools.eyes.Region;
import com.applitools.eyes.selenium.universal.dto.RegionDto;

/**
 * region mapper
 */
public class RegionMapper {

  public static RegionDto toRegionDto(Region region) {
    if (region == null) {
      return null;
    }

    RegionDto regionDto = new RegionDto();
    regionDto.setLeft(region.getLeft());
    regionDto.setTop(region.getTop());
    regionDto.setWidth(region.getWidth());
    regionDto.setHeight(region.getHeight());
    regionDto.setCoordinatesType(region.getCoordinatesType().name());

    return regionDto;
  }

  public static List<RegionDto> toRegionDtoList(Region[] regions) {
    if (regions == null || regions.length == 0) {
      return null;
    }

    List<Region> regionList = Arrays.asList(regions);

    return regionList.stream().map(RegionMapper::toRegionDto).collect(Collectors.toList());
  }
}