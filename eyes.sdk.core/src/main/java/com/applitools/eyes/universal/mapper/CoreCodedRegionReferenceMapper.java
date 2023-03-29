package com.applitools.eyes.universal.mapper;

import com.applitools.eyes.universal.dto.CodedRegionReference;
import com.applitools.eyes.universal.dto.TRegion;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class CoreCodedRegionReferenceMapper {

    public static CodedRegionReference toCodedRegionReference(TRegion region) {
        if (region == null) {
            return null;
        }

        CodedRegionReference codedRegionReference = new CodedRegionReference();

        codedRegionReference.setRegion(region);
        codedRegionReference.setRegionId(null);
        codedRegionReference.setPadding(null);

        return codedRegionReference;
    }

    public static List<CodedRegionReference> toCodedRegionReferenceList(List<TRegion> getSimpleRegionList) {
        if (getSimpleRegionList == null || getSimpleRegionList.isEmpty()) {
            return null;
        }

        return getSimpleRegionList
                .stream()
                .filter(Objects::nonNull)
                .map(CoreCodedRegionReferenceMapper::toCodedRegionReference)
                .collect(Collectors.toList());
    }
}
