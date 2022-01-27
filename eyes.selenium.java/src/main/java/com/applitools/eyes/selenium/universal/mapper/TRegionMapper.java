package com.applitools.eyes.selenium.universal.mapper;

import java.util.List;
import java.util.stream.Collectors;

import com.applitools.ICheckSettings;
import com.applitools.eyes.Region;
import com.applitools.eyes.fluent.CheckSettings;
import com.applitools.eyes.fluent.GetSimpleRegion;
import com.applitools.eyes.fluent.SimpleRegionByRectangle;
import com.applitools.eyes.selenium.fluent.SeleniumCheckSettings;
import com.applitools.eyes.selenium.fluent.SimpleRegionByElement;
import com.applitools.eyes.selenium.fluent.SimpleRegionBySelector;
import com.applitools.eyes.selenium.universal.dto.TRegion;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

/**
 * TRegion mapper
 */
public class TRegionMapper {

  public static TRegion toTRegion(GetSimpleRegion getSimpleRegion) {
    if (getSimpleRegion == null) {
      return null;
    }

    if (getSimpleRegion instanceof SimpleRegionByRectangle) {
      return RectangleRegionMapper.toRectangleRegionDto(((SimpleRegionByRectangle) getSimpleRegion).getRegion());
    } else if (getSimpleRegion instanceof SimpleRegionByElement) {
      WebElement element = ((SimpleRegionByElement) getSimpleRegion).getElement();
      return ElementRegionMapper.toElementRegionDto(element);
    } else if (getSimpleRegion instanceof SimpleRegionBySelector) {
      By by = ((SimpleRegionBySelector) getSimpleRegion).getSelector();
      return SelectorRegionMapper.toSelectorRegionDto(by);
    }

    return null;
  }

  public static List<TRegion> toTRegionList(List<GetSimpleRegion> getSimpleRegionList) {
    if (getSimpleRegionList == null || getSimpleRegionList.isEmpty()) {
      return null;
    }

    return getSimpleRegionList.stream().map(TRegionMapper::toTRegion).collect(Collectors.toList());
  }

  public static TRegion toTargetRegion(CheckSettings checkSettings) {
    if (!(checkSettings instanceof SeleniumCheckSettings)) {
      return null;
    }

    return null;
  }

  public static TRegion toTRegionFromCheckSettings(ICheckSettings checkSettings) {
    if (!(checkSettings instanceof SeleniumCheckSettings)) {
      return null;
    }

    SeleniumCheckSettings seleniumCheckSettings = (SeleniumCheckSettings) checkSettings;
    By by = seleniumCheckSettings.getTargetSelector();

    if (by != null) {
      return SelectorRegionMapper.toSelectorRegionDto(by);
    }

    WebElement element = seleniumCheckSettings.getTargetElement();
    if (element != null) {
      return ElementRegionMapper.toElementRegionDto(element);
    }

    Region region = seleniumCheckSettings.getTargetRegion();

    if (region != null) {
      return RectangleRegionMapper.toRectangleRegionDto(region);
    }

    return null;
  }

  public static TRegion toTRegionDtoFromScrolls(By selector, WebElement element) {
    if (selector != null) {
      return SelectorRegionMapper.toSelectorRegionDto(selector);
    }

    if (element != null) {
      return ElementRegionMapper.toElementRegionDto(element);
    }

    return null;
  }
}
