package com.applitools.eyes.playwright.universal.mapper;

import com.applitools.eyes.fluent.GetRegion;
import com.applitools.eyes.fluent.SimpleRegionByRectangle;
import com.applitools.eyes.playwright.universal.Refer;
import com.applitools.eyes.playwright.universal.dto.Element;
import com.applitools.eyes.playwright.universal.dto.Selector;
import com.applitools.eyes.universal.Reference;
import com.applitools.eyes.universal.dto.CodedRegionReference;
import com.applitools.eyes.universal.dto.TRegion;
import com.applitools.eyes.universal.mapper.RectangleRegionMapper;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class CodedRegionReferenceMapper {

    public static CodedRegionReference toCodedRegionReference(GetRegion getSimpleRegion, Refer refer, Reference root) {
        if (getSimpleRegion == null) {
            return null;
        }

        CodedRegionReference codedRegionReference = new CodedRegionReference();

        if (getSimpleRegion instanceof Element) {
            Element element = (Element) getSimpleRegion;
            element.setApplitoolsRefId(refer.ref(element.getElementHandle(), root));
            codedRegionReference.setRegion(element);
            codedRegionReference.setRegionId(element.getRegionId());
            codedRegionReference.setPadding(element.getPadding());
        } else if (getSimpleRegion instanceof Selector) {
            Selector selector = (Selector) getSimpleRegion;
            selector.setApplitoolsRefId(refer.ref(getSimpleRegion, root));
            codedRegionReference.setRegion(selector);
            codedRegionReference.setRegionId(selector.getRegionId());
            codedRegionReference.setPadding(selector.getPadding());
        } else if (getSimpleRegion instanceof SimpleRegionByRectangle) {
            SimpleRegionByRectangle simpleRegionByRectangle = (SimpleRegionByRectangle) getSimpleRegion;
            TRegion region = RectangleRegionMapper.toRectangleRegionDto(simpleRegionByRectangle.getRegion());
            codedRegionReference.setRegion(region);
            codedRegionReference.setRegionId(simpleRegionByRectangle.getRegion().getRegionId());
            codedRegionReference.setPadding(simpleRegionByRectangle.getPadding());
        }

        return codedRegionReference;
    }

    public static List<CodedRegionReference> toCodedRegionReferenceList(List<GetRegion> getSimpleRegionList, Refer refer, Reference root) {
        if (getSimpleRegionList == null || getSimpleRegionList.isEmpty()) {
            return null;
        }

        return getSimpleRegionList
                .stream()
                .filter(Objects::nonNull)
                .map(reference -> toCodedRegionReference(reference, refer, root))
                .collect(Collectors.toList());
    }
}
