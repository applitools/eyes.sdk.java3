package com.applitools.eyes.playwright.universal.mapper;

import com.applitools.eyes.playwright.universal.Refer;
import com.applitools.eyes.playwright.universal.driver.Element;
import com.applitools.eyes.universal.Reference;
import com.microsoft.playwright.ElementHandle;

import java.util.List;

public class TFramesMapper {
    public static Object toTFramesFromCheckSettings(List<Reference> frameChain, Refer refer) {
        if (frameChain == null || frameChain.isEmpty()) {
            return null;
        }

        for (Reference ref : frameChain) {

            if (ref instanceof Element) {
                ElementHandle elementHandle = ((Element) ref).getElementHandle();
                ref.setApplitoolsRefId(refer.ref(elementHandle));
            } else {
                ref.setApplitoolsRefId(refer.ref(ref));
            }
        }

        return frameChain;
    }
}
