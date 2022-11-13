package com.applitools.eyes.selenium.universal.mapper;

import java.util.List;
import java.util.stream.Collectors;

import com.applitools.eyes.selenium.fluent.FrameLocator;
import com.applitools.eyes.selenium.universal.dto.ContextReferenceDto;
import com.applitools.eyes.selenium.universal.dto.ElementRegionDto;
import com.applitools.eyes.selenium.universal.dto.ScrollRootElementDto;
import com.applitools.eyes.selenium.universal.dto.SelectorRegionDto;
import com.google.common.base.Strings;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.pagefactory.ByAll;
import org.openqa.selenium.support.pagefactory.ByChained;

/**
 * frame locator mapper
 */
public class ContextReferenceMapper {

  public static ContextReferenceDto toContextReferenceDto(FrameLocator frameLocator) {
    if (frameLocator == null) {
      return null;
    }

    ContextReferenceDto contextReferenceDto = new ContextReferenceDto();

    if (!Strings.isNullOrEmpty(frameLocator.getFrameNameOrId())) {
      contextReferenceDto.setFrame(frameLocator.getFrameNameOrId());
    }

    if (frameLocator.getFrameIndex() != null) {
      contextReferenceDto.setFrame(frameLocator.getFrameIndex());
    }

    By by = frameLocator.getFrameSelector();
    if (by != null) {
      SelectorRegionDto selectorRegionDto =  SelectorRegionMapper.toSelectorRegionDto(by);
      contextReferenceDto.setFrame(selectorRegionDto);
    }

    WebElement element = frameLocator.getFrameReference();
    if (element != null) {
      ElementRegionDto elementRegionDto = ElementRegionMapper.toElementRegionDto(element);
      contextReferenceDto.setFrame(elementRegionDto);
    }

    By scrollBy = frameLocator.getScrollRootSelector();
    if (scrollBy != null) {
      SelectorRegionDto scrollSelector;
      if (by instanceof ByAll)
        scrollSelector = SelectorRegionMapper.toSelectorRegionDto((ByAll) scrollBy);
      else if (by instanceof ByChained)
        scrollSelector = SelectorRegionMapper.toSelectorRegionDto((ByChained) scrollBy);
      else
        scrollSelector = SelectorRegionMapper.toSelectorRegionDto(scrollBy);
      contextReferenceDto.setScrollRootElement(scrollSelector);
    }

    WebElement scrollElement = frameLocator.getScrollRootElement();
    if (scrollElement != null) {
      ElementRegionDto elementRegionDto1 = ElementRegionMapper.toElementRegionDto(scrollElement);
      contextReferenceDto.setScrollRootElement(elementRegionDto1);
    }

    return contextReferenceDto;
  }

  public static List<ContextReferenceDto> toContextReferenceDtoList(List<FrameLocator> frameLocatorList) {
    if (frameLocatorList == null || frameLocatorList.isEmpty()) {
      return null;
    }

    return frameLocatorList.stream().map(ContextReferenceMapper::toContextReferenceDto).collect(Collectors.toList());

  }
}
