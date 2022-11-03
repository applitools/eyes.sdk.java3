package com.applitools.eyes.appium;

import com.applitools.eyes.fluent.FloatingRegionByRectangle;
import com.applitools.eyes.fluent.GetFloatingRegion;
import com.applitools.eyes.selenium.fluent.FloatingRegionByElement;
import com.applitools.eyes.selenium.fluent.FloatingRegionBySelector;
import com.applitools.eyes.selenium.universal.dto.*;
import com.applitools.eyes.selenium.universal.mapper.ElementRegionMapper;
import com.applitools.eyes.selenium.universal.mapper.RectangleRegionMapper;
import io.appium.java_client.pagefactory.bys.builder.ByAll;
import io.appium.java_client.pagefactory.bys.builder.ByChained;
import org.openqa.selenium.By;

import java.util.List;
import java.util.stream.Collectors;

public class AppiumTFloatingRegionMapper {

    public static TFloatingRegion toTFloatingRegionDto(GetFloatingRegion getFloatingRegion) {
        if (getFloatingRegion == null) {
            return null;
        }

        if (getFloatingRegion instanceof FloatingRegionByRectangle) {
            FloatingRegionByRectangle floatingRegionByRectangle = (FloatingRegionByRectangle) getFloatingRegion;

            RectangleRegionDto rectangleRegionDto = RectangleRegionMapper
                    .toRectangleRegionDto(((FloatingRegionByRectangle) getFloatingRegion).getRegion());

            RectangleFloatingRegionDto response = new RectangleFloatingRegionDto();
            response.setRegion(rectangleRegionDto);
            response.setMaxUpOffset(floatingRegionByRectangle.getMaxUpOffset());
            response.setMaxDownOffset(floatingRegionByRectangle.getMaxDownOffset());
            response.setMaxLeftOffset(floatingRegionByRectangle.getMaxLeftOffset());
            response.setMaxRightOffset(floatingRegionByRectangle.getMaxRightOffset());
            response.setRegionId(floatingRegionByRectangle.getRegion().getRegionId());

            return response;
        } else if (getFloatingRegion instanceof FloatingRegionByElement) {
            FloatingRegionByElement floatingRegionByElement = (FloatingRegionByElement) getFloatingRegion;

            ElementRegionDto elementRegionDto = ElementRegionMapper.toElementRegionDto(floatingRegionByElement.getElement());

            ElementFloatingRegionDto elementFloatingRegionDto = new ElementFloatingRegionDto();
            elementFloatingRegionDto.setRegion(elementRegionDto);
            elementFloatingRegionDto.setMaxUpOffset(floatingRegionByElement.getMaxUpOffset());
            elementFloatingRegionDto.setMaxDownOffset(floatingRegionByElement.getMaxDownOffset());
            elementFloatingRegionDto.setMaxLeftOffset(floatingRegionByElement.getMaxLeftOffset());
            elementFloatingRegionDto.setMaxRightOffset(floatingRegionByElement.getMaxRightOffset());
            elementFloatingRegionDto.setRegionId(floatingRegionByElement.getRegionId());

            return elementFloatingRegionDto;
        } else if (getFloatingRegion instanceof FloatingRegionBySelector) {
            FloatingRegionBySelector floatingRegionBySelector = (FloatingRegionBySelector) getFloatingRegion;

            SelectorRegionDto selectorRegionDto;
            By by = floatingRegionBySelector.getSelector();
            if (by instanceof ByAll)
                selectorRegionDto = AppiumSelectorRegionMapper.toAppiumSelectorRegionDto((ByAll) by);
            else if (by instanceof ByChained)
                selectorRegionDto = AppiumSelectorRegionMapper.toAppiumSelectorRegionDto((ByChained) by);
            else
                selectorRegionDto = AppiumSelectorRegionMapper.toAppiumSelectorRegionDto(by);



            SelectorFloatingRegionDto selectorFloatingRegionDto = new SelectorFloatingRegionDto();
            selectorFloatingRegionDto.setRegion(selectorRegionDto);
            selectorFloatingRegionDto.setMaxUpOffset(floatingRegionBySelector.getMaxUpOffset());
            selectorFloatingRegionDto.setMaxDownOffset(floatingRegionBySelector.getMaxDownOffset());
            selectorFloatingRegionDto.setMaxLeftOffset(floatingRegionBySelector.getMaxLeftOffset());
            selectorFloatingRegionDto.setMaxRightOffset(floatingRegionBySelector.getMaxRightOffset());
            selectorFloatingRegionDto.setRegionId(floatingRegionBySelector.getRegionId());

            return selectorFloatingRegionDto;
        }

        return null;

    }

    public static List<TFloatingRegion> toTFloatingRegionDtoList(List<GetFloatingRegion> getFloatingRegionList) {
        if (getFloatingRegionList == null || getFloatingRegionList.isEmpty()) {
            return null;
        }

        return getFloatingRegionList.stream().map(AppiumTFloatingRegionMapper::toTFloatingRegionDto).collect(Collectors.toList());
    }
}
