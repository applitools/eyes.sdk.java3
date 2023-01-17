package com.applitools.eyes.playwright.universal.mapper;

import com.applitools.eyes.Region;
import com.applitools.eyes.fluent.FloatingRegionByRectangle;
import com.applitools.eyes.fluent.GetRegion;
import com.applitools.eyes.playwright.fluent.FloatingRegionElement;
import com.applitools.eyes.playwright.fluent.FloatingRegionSelector;
import com.applitools.eyes.playwright.universal.Refer;
import com.applitools.eyes.playwright.universal.dto.FloatingRegionByElement;
import com.applitools.eyes.playwright.universal.dto.FloatingRegionBySelector;
import com.applitools.eyes.universal.dto.*;

import java.util.List;
import java.util.stream.Collectors;

public class TFloatingRegionMapper {

    public static TFloatingRegion toTFloatingRegionDto(GetRegion getFloatingRegion, Refer refer) {
        if (getFloatingRegion == null) {
            return null;
        }

        if (getFloatingRegion instanceof FloatingRegionSelector) {
            FloatingRegionSelector selector = (FloatingRegionSelector) getFloatingRegion;
            selector.setApplitoolsRefId(refer.ref(getFloatingRegion));

            FloatingRegionBySelector floatingRegionBySelector = new FloatingRegionBySelector();
            floatingRegionBySelector.setRegion(selector);
            floatingRegionBySelector.setRegionId(selector.getRegionId());
            floatingRegionBySelector.setMaxUpOffset(selector.getMaxUpOffset());
            floatingRegionBySelector.setMaxRightOffset(selector.getMaxRightOffset());
            floatingRegionBySelector.setMaxDownOffset(selector.getMaxDownOffset());
            floatingRegionBySelector.setMaxLeftOffset(selector.getMaxLeftOffset());
            return floatingRegionBySelector;

        } else if (getFloatingRegion instanceof FloatingRegionElement) {
            FloatingRegionElement element = (FloatingRegionElement) getFloatingRegion;
            element.setApplitoolsRefId(refer.ref(element.getElementHandle()));

            FloatingRegionByElement floatingRegionByElement = new FloatingRegionByElement();
            floatingRegionByElement.setRegion(element);
            floatingRegionByElement.setRegionId(element.getRegionId());
            floatingRegionByElement.setMaxUpOffset(element.getMaxUpOffset());
            floatingRegionByElement.setMaxRightOffset(element.getMaxRightOffset());
            floatingRegionByElement.setMaxDownOffset(element.getMaxDownOffset());
            floatingRegionByElement.setMaxLeftOffset(element.getMaxLeftOffset());
            return floatingRegionByElement;

        } else if (getFloatingRegion instanceof FloatingRegionByRectangle) {
            FloatingRegionByRectangle floatingRegionByRectangle = (FloatingRegionByRectangle) getFloatingRegion;
            RectangleRegionDto rectangleRegionDto = new RectangleRegionDto();
            RectangleFloatingRegionDto rectangleFloatingRegionDto = new RectangleFloatingRegionDto();

            Region region = floatingRegionByRectangle.getRegion();
            if (region != null) {
                rectangleRegionDto.setX(region.getLeft());
                rectangleRegionDto.setY(region.getTop());
                rectangleRegionDto.setHeight(region.getHeight());
                rectangleRegionDto.setWidth(region.getWidth());

                rectangleFloatingRegionDto.setRegionId(region.getRegionId());
            }

            rectangleFloatingRegionDto.setRegion(rectangleRegionDto);
            rectangleFloatingRegionDto.setMaxUpOffset(floatingRegionByRectangle.getMaxUpOffset());
            rectangleFloatingRegionDto.setMaxRightOffset(floatingRegionByRectangle.getMaxRightOffset());
            rectangleFloatingRegionDto.setMaxDownOffset(floatingRegionByRectangle.getMaxDownOffset());
            rectangleFloatingRegionDto.setMaxLeftOffset(floatingRegionByRectangle.getMaxLeftOffset());

            return rectangleFloatingRegionDto;
        }

        return null;
    }

    public static List<TFloatingRegion> toTFloatingRegionDtoList(List<GetRegion> getFloatingRegionList, Refer refer) {
        if (getFloatingRegionList == null || getFloatingRegionList.isEmpty()) {
            return null;
        }

        return getFloatingRegionList.stream()
                .map(reference -> toTFloatingRegionDto(reference, refer))
                .collect(Collectors.toList());
    }
}
