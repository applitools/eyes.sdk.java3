package com.applitools.eyes.playwright.universal.mapper;

import com.applitools.eyes.playwright.universal.Refer;
import com.applitools.eyes.universal.Reference;

import java.util.List;

public class TFramesMapper {
    public static Object toTFramesFromCheckSettings(List<Reference> frameChain, Refer refer) {
        if (frameChain == null || frameChain.isEmpty()) {
            return null;
        }

        for (Reference ref : frameChain) {
            String refId = refer.ref(ref);
            ref.setApplitoolsRefId(refId);
        }

        return frameChain;
    }
}
