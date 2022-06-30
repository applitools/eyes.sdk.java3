package com.applitools.eyes.selenium.universal.mapper;

import com.applitools.eyes.selenium.ElementReference;
import com.applitools.eyes.selenium.ElementSelector;
import com.applitools.eyes.selenium.PathNodeValue;
import com.applitools.eyes.selenium.TargetPathLocator;
import com.applitools.eyes.selenium.universal.dto.ElementRegionDto;
import com.applitools.eyes.selenium.universal.dto.TargetPathLocatorDto;

public class TargetPathLocatorMapper {
    public static TargetPathLocatorDto toTargetPathLocatorDto(TargetPathLocator locator) {
        if(locator == null) {
            return null;
        }

        /*
         * Algorithm:
         * create the current element, and create a parent.
         * Always put the current element as the shadow of the parent.
         * If the parent doesn't actually exist, we'll drop it (this happens in the last iteration).
         */
        TargetPathLocatorDto currentDto;
        TargetPathLocatorDto parentDto = new TargetPathLocatorDto();
        TargetPathLocator parentLocator = locator;
        do  {
            locator = parentLocator;
            parentLocator = locator.getParent();

            currentDto = parentDto;
            parentDto = new TargetPathLocatorDto();
            parentDto.setShadow(currentDto);

            PathNodeValue value = locator.getValue();
            if (value instanceof ElementSelector) {
                currentDto.setType(((ElementSelector) value).getType());
                currentDto.setSelector(((ElementSelector) value).getSelector());
            } else if (value instanceof ElementReference) {
                ElementRegionDto erd = ElementRegionMapper.toElementRegionDto(((ElementReference) value).getElement());
                currentDto.setElementId(erd.getElementId());
            }
        } while (locator.getParent()!=null);

        return currentDto;
    }
}
