package com.applitools.eyes.selenium.universal.mapper;

import java.util.List;
import java.util.stream.Collectors;

import com.applitools.eyes.selenium.fluent.FrameLocator;
import com.applitools.eyes.selenium.universal.dto.ContextReferenceDto;
import com.applitools.eyes.selenium.universal.dto.ElementRegionDto;
import com.applitools.eyes.selenium.universal.dto.FrameLocatorDto;
import com.applitools.eyes.selenium.universal.dto.ScrollRootElementDto;
import com.applitools.eyes.selenium.universal.dto.SelectorRegionDto;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

/**
 * frame locator mapper
 */
public class ContextReferenceMapper {

  public static ContextReferenceDto toContextReferenceDto(FrameLocator frameLocator) {
    if (frameLocator == null) {
      return null;
    }

    ContextReferenceDto contextReferenceDto = new ContextReferenceDto();

    FrameLocatorDto frameLocatorDto = new FrameLocatorDto();
    By by = frameLocator.getFrameSelector();
    SelectorRegionDto selectorRegionDto =  SelectorRegionMapper.toSelectorRegionDto(by);
    frameLocatorDto.setSelector(selectorRegionDto);

    WebElement element = frameLocator.getFrameReference();
    ElementRegionDto elementRegionDto = ElementRegionMapper.toElementRegionDto(element);
    frameLocatorDto.setElement(elementRegionDto);

    frameLocatorDto.setFrameNameOrId(frameLocator.getFrameNameOrId());
    frameLocatorDto.setFrameIndex(frameLocator.getFrameIndex());

    contextReferenceDto.setFrame(frameLocatorDto);

    ScrollRootElementDto scrollRootElementDto = new ScrollRootElementDto();
    By scrollBy = frameLocator.getScrollRootSelector();
    SelectorRegionDto scrollSelector =  SelectorRegionMapper.toSelectorRegionDto(scrollBy);
    scrollRootElementDto.setSelector(scrollSelector);

    WebElement scrollElement = frameLocator.getScrollRootElement();
    ElementRegionDto elementRegionDto1 = ElementRegionMapper.toElementRegionDto(scrollElement);
    scrollRootElementDto.setElement(elementRegionDto1);
    contextReferenceDto.setScrollRootElement(scrollRootElementDto);

    return contextReferenceDto;

  }

  public static List<ContextReferenceDto> toContextReferenceDtoList(List<FrameLocator> frameLocatorList) {
    if (frameLocatorList == null || frameLocatorList.isEmpty()) {
      return null;
    }

    return frameLocatorList.stream().map(ContextReferenceMapper::toContextReferenceDto).collect(Collectors.toList());

  }
}
