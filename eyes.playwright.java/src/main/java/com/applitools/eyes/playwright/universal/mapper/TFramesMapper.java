package com.applitools.eyes.playwright.universal.mapper;

import com.applitools.eyes.playwright.universal.Refer;
import com.applitools.eyes.playwright.universal.driver.Element;
import com.applitools.eyes.playwright.universal.driver.Selector;
import com.applitools.eyes.universal.Reference;

import java.util.List;

public class TFramesMapper {
    public static Object toTFramesFromCheckSettings(List<Reference> frameChain, Refer refer) {
        if (frameChain == null || frameChain.isEmpty()) {
            return null;
        }

        for (Reference ref : frameChain) {
            if (ref instanceof Element) {
                String refId = refer.ref(ref);
                ref.setApplitoolsRefId(refId);
            } else if (ref instanceof Selector) {
                String refId = refer.ref(ref);
                ref.setApplitoolsRefId(refId);
            }
        }

        return frameChain;
    }
}
