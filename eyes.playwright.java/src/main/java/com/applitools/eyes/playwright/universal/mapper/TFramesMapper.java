package com.applitools.eyes.playwright.universal.mapper;

import com.applitools.eyes.playwright.universal.Refer;
import com.applitools.eyes.playwright.universal.dto.Element;
import com.applitools.eyes.playwright.universal.dto.FrameLocator;
import com.applitools.eyes.playwright.universal.dto.Selector;
import com.applitools.eyes.universal.Reference;
import com.applitools.eyes.universal.dto.ContextReferenceDto;
import com.google.common.base.Strings;
import com.microsoft.playwright.ElementHandle;
import com.microsoft.playwright.Locator;

import java.util.List;
import java.util.stream.Collectors;

public class TFramesMapper {

    public static ContextReferenceDto toContextReferenceDto(FrameLocator frame, Refer refer, Reference root) {
        if (frame == null) {
            return null;
        }

        ContextReferenceDto contextReferenceDto = new ContextReferenceDto();

        String frameNameOrId = frame.getFrameNameOrId();
        if (!Strings.isNullOrEmpty(frameNameOrId)) {
            contextReferenceDto.setFrame(frameNameOrId);
        }

        Integer frameIndex = frame.getFrameIndex();
        if (frameIndex != null) {
            contextReferenceDto.setFrame(frameIndex);
        }

        Element frameElement = frame.getFrameElement();
        if (frameElement != null) {
            ElementHandle elementHandle = frameElement.getElementHandle();
            frameElement.setApplitoolsRefId(refer.ref(elementHandle, root));
            contextReferenceDto.setFrame(frameElement);
        }

        Selector frameSelector = frame.getFrameSelector();
        if (frameSelector != null) {
            if (frameSelector.getLocator() != null) {
                Locator locator = frameSelector.getLocator();
                frameSelector.setApplitoolsRefId(refer.ref(locator, root));
            }
            contextReferenceDto.setFrame(frameSelector);
        }

        Selector scrollSelector = frame.getScrollRootSelector();
        if (scrollSelector != null) {
            if (scrollSelector.getLocator() != null) {
                Locator locator = scrollSelector.getLocator();
                scrollSelector.setApplitoolsRefId(refer.ref(locator, root));
            }
            contextReferenceDto.setScrollRootElement(scrollSelector);
        }

        Element scrollElement = frame.getScrollRootElement();
        if (scrollElement != null) {
            ElementHandle elementHandle = scrollElement.getElementHandle();
            scrollElement.setApplitoolsRefId(refer.ref(elementHandle, root));
            contextReferenceDto.setScrollRootElement(scrollElement);
        }

        return contextReferenceDto;
    }

    public static List<ContextReferenceDto> toTFramesFromCheckSettings(List<FrameLocator> frameChain, Refer refer, Reference root) {
        if (frameChain == null || frameChain.isEmpty()) {
            return null;
        }

        return frameChain.stream()
                .map(frame -> toContextReferenceDto(frame, refer, root))
                .collect(Collectors.toList());
    }
}
