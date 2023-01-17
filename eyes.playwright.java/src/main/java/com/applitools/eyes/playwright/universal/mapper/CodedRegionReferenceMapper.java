package com.applitools.eyes.playwright.universal.mapper;

import com.applitools.eyes.fluent.GetRegion;
import com.applitools.eyes.fluent.SimpleRegionByRectangle;
import com.applitools.eyes.playwright.universal.Refer;
import com.applitools.eyes.playwright.universal.driver.Element;
import com.applitools.eyes.playwright.universal.driver.Selector;
import com.applitools.eyes.universal.dto.CodedRegionReference;
import com.applitools.eyes.universal.dto.TRegion;
import com.applitools.eyes.universal.mapper.RectangleRegionMapper;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class CodedRegionReferenceMapper {

    public static CodedRegionReference toCodedRegionReference(GetRegion getSimpleRegion, Refer refer) {
        if (getSimpleRegion == null) {
            return null;
        }

        CodedRegionReference codedRegionReference = new CodedRegionReference();

        if (getSimpleRegion instanceof Element) {
            Element element = (Element) getSimpleRegion;
            element.setApplitoolsRefId(refer.ref(element.getElementHandle()));
            codedRegionReference.setRegion(element);
            codedRegionReference.setRegionId(((Element) getSimpleRegion).getRegionId());
            codedRegionReference.setPadding(((Element) getSimpleRegion).getPadding());
        } else if (getSimpleRegion instanceof Selector) {
            Selector selector = (Selector) getSimpleRegion;
            selector.setApplitoolsRefId(refer.ref(getSimpleRegion));
            codedRegionReference.setRegion(selector);
            codedRegionReference.setRegionId(((Selector) getSimpleRegion).getRegionId());
            codedRegionReference.setPadding(((Selector) getSimpleRegion).getPadding());
        } else if (getSimpleRegion instanceof SimpleRegionByRectangle) {
            SimpleRegionByRectangle simpleRegionByRectangle = (SimpleRegionByRectangle) getSimpleRegion;
            TRegion region = RectangleRegionMapper.toRectangleRegionDto(simpleRegionByRectangle.getRegion());
            codedRegionReference.setRegion(region);
            codedRegionReference.setRegionId(simpleRegionByRectangle.getRegion().getRegionId());
            codedRegionReference.setPadding(simpleRegionByRectangle.getPadding());
        }

        return codedRegionReference;
    }

    public static List<CodedRegionReference> toCodedRegionReferenceList(List<GetRegion> getSimpleRegionList, Refer refer) {
        if (getSimpleRegionList == null || getSimpleRegionList.isEmpty()) {
            return null;
        }

        return getSimpleRegionList
                .stream()
                .filter(Objects::nonNull)
                .map(reference -> toCodedRegionReference(reference, refer))
                .collect(Collectors.toList());
    }
}
