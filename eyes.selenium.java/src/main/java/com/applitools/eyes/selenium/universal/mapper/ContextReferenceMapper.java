package com.applitools.eyes.selenium.universal.mapper;

import java.util.List;
import java.util.stream.Collectors;

import com.applitools.eyes.selenium.fluent.FrameLocator;
import com.applitools.eyes.selenium.universal.dto.ContextReferenceDto;
import com.applitools.eyes.selenium.universal.dto.ElementRegionDto;
import com.applitools.eyes.selenium.universal.dto.FrameElement;
import com.applitools.eyes.selenium.universal.dto.FrameLocatorDto;
import com.applitools.eyes.selenium.universal.dto.FrameNameOrId;
import com.applitools.eyes.selenium.universal.dto.FrameSelector;
import com.applitools.eyes.selenium.universal.dto.ScrollRootElementDto;
import com.applitools.eyes.selenium.universal.dto.SelectorRegionDto;
import com.google.common.base.Strings;
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


    if (!Strings.isNullOrEmpty(frameLocator.getFrameNameOrId())) {
//      FrameNameOrId fr = new FrameNameOrId();
//      fr.setFrame(frameLocator.getFrameNameOrId());
      contextReferenceDto.setFrame(frameLocator.getFrameNameOrId());
    }


    //FrameLocatorDto frameLocatorDto = new FrameLocatorDto();
    By by = frameLocator.getFrameSelector();
    if (by != null) {
      FrameSelector frameSelector = new FrameSelector();
      SelectorRegionDto selectorRegionDto =  SelectorRegionMapper.toSelectorRegionDto(by);
      frameSelector.setSelector(selectorRegionDto);
      contextReferenceDto.setFrame(frameSelector);
    }

    WebElement element = frameLocator.getFrameReference();
    if (element != null) {
      FrameElement frameElement = new FrameElement();
      ElementRegionDto elementRegionDto = ElementRegionMapper.toElementRegionDto(element);
      frameElement.setElement(elementRegionDto);
      contextReferenceDto.setFrame(frameElement);
    }

    ScrollRootElementDto scrollRootElementDto = null;
    By scrollBy = frameLocator.getScrollRootSelector();
    if (scrollBy != null) {
      SelectorRegionDto scrollSelector =  SelectorRegionMapper.toSelectorRegionDto(scrollBy);
      scrollRootElementDto = new ScrollRootElementDto();
      scrollRootElementDto.setSelector(scrollSelector);
    }

    WebElement scrollElement = frameLocator.getScrollRootElement();
    if (scrollElement != null) {
      ElementRegionDto elementRegionDto1 = ElementRegionMapper.toElementRegionDto(scrollElement);
      scrollRootElementDto = new ScrollRootElementDto();
      scrollRootElementDto.setElement(elementRegionDto1);
    }

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
