package com.applitools.eyes.selenium.universal.mapper;

import java.util.List;
import java.util.stream.Collectors;

import com.applitools.eyes.selenium.fluent.FrameLocator;
import com.applitools.eyes.selenium.universal.dto.FrameLocatorDto;

/**
 * frame locator mapper
 */
public class FrameLocatorMapper {

  public static FrameLocatorDto toFrameLocatorDto(FrameLocator frameLocator) {
    if (frameLocator == null) {
      return null;
    }

    FrameLocatorDto frameLocatorDto = new FrameLocatorDto();
    frameLocatorDto.setFrameElement(frameLocator.getFrameReference());
    frameLocator.setFrameSelector(frameLocator.getFrameSelector());
    frameLocator.setFrameNameOrId(frameLocator.getFrameNameOrId());
    frameLocator.setFrameIndex(frameLocator.getFrameIndex());
    frameLocator.setScrollRootSelector(frameLocator.getScrollRootSelector());
    frameLocator.setScrollRootElement(frameLocator.getScrollRootElement());
    return frameLocatorDto;

  }

  public static List<FrameLocatorDto> toFrameLocatorDtoList(List<FrameLocator> frameLocatorList) {
    if (frameLocatorList == null || frameLocatorList.isEmpty()) {
      return null;
    }

    return frameLocatorList.stream().map(FrameLocatorMapper::toFrameLocatorDto).collect(Collectors.toList());

  }
}
