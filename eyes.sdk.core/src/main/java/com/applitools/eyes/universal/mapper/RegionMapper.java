package com.applitools.eyes.universal.mapper;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.applitools.eyes.Region;
import com.applitools.eyes.universal.dto.RegionDto;
import com.applitools.eyes.universal.dto.TRegion;

/**
 * region mapper
 */
public class RegionMapper {

  public static RegionDto toRegionDto(Region region) {
    if (region == null) {
      return null;
    }

    RegionDto regionDto = new RegionDto();
    regionDto.setX(region.getLeft());
    regionDto.setY(region.getTop());
    regionDto.setLeft(region.getLeft());
    regionDto.setTop(region.getTop());
    regionDto.setWidth(region.getWidth());
    regionDto.setHeight(region.getHeight());
    regionDto.setCoordinatesType(region.getCoordinatesType().name());

    return regionDto;
  }

  public static List<TRegion> toRegionDtoList(Region[] regions) {
    if (regions == null || regions.length == 0) {
      return null;
    }

    List<Region> regionList = Arrays.asList(regions);

    return regionList.stream().map(RegionMapper::toRegionDto).collect(Collectors.toList());
  }
}
