package com.applitools.eyes.appium;

import com.applitools.eyes.fluent.GetRegion;
import com.applitools.eyes.fluent.SimpleRegionByRectangle;
import com.applitools.eyes.selenium.fluent.SimpleRegionByElement;
import com.applitools.eyes.selenium.fluent.SimpleRegionBySelector;
import com.applitools.eyes.universal.dto.CodedRegionReference;
import com.applitools.eyes.universal.dto.TRegion;
import com.applitools.eyes.selenium.universal.mapper.ElementRegionMapper;
import com.applitools.eyes.universal.mapper.RectangleRegionMapper;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class AppiumCodedRegionReferenceMapper {

    public static CodedRegionReference toCodedRegionReference(GetRegion getSimpleRegion) {
        if (getSimpleRegion == null) {
            return null;
        }

        CodedRegionReference codedRegionReference = new CodedRegionReference();

        if (getSimpleRegion instanceof SimpleRegionByRectangle) {
            SimpleRegionByRectangle simpleRegionByRectangle = (SimpleRegionByRectangle) getSimpleRegion;
            TRegion region = RectangleRegionMapper.toRectangleRegionDto(simpleRegionByRectangle.getRegion());
            codedRegionReference.setRegion(region);
            codedRegionReference.setRegionId(simpleRegionByRectangle.getRegion().getRegionId());
            // set padding here
        } else if (getSimpleRegion instanceof SimpleRegionByElement) {
            SimpleRegionByElement simpleRegionByElement = (SimpleRegionByElement) getSimpleRegion;
            TRegion region = ElementRegionMapper.toElementRegionDto(simpleRegionByElement.getElement());
            codedRegionReference.setRegion(region);
            codedRegionReference.setRegionId(simpleRegionByElement.getRegionId());
            // set padding here
        } else if (getSimpleRegion instanceof SimpleRegionBySelector) {
            SimpleRegionBySelector simpleRegionBySelector = (SimpleRegionBySelector) getSimpleRegion;
            TRegion region = AppiumSelectorRegionMapper.toAppiumSelectorRegionDto(simpleRegionBySelector.getSelector());
            codedRegionReference.setRegion(region);
            codedRegionReference.setRegionId(simpleRegionBySelector.getRegionId());
            // set padding here
        }

        return codedRegionReference;
    }

    public static List<CodedRegionReference> toCodedRegionReferenceList(List<GetRegion> getSimpleRegionList) {
        if (getSimpleRegionList == null || getSimpleRegionList.isEmpty()) {
            return null;
        }

        return getSimpleRegionList
                .stream()
                .filter(Objects::nonNull)
                .map(AppiumCodedRegionReferenceMapper::toCodedRegionReference)
                .collect(Collectors.toList());
    }
}
