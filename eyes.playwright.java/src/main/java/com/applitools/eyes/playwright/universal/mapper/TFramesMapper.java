package com.applitools.eyes.playwright.universal.mapper;

import com.applitools.eyes.playwright.universal.Refer;
import com.applitools.eyes.playwright.universal.dto.Element;
import com.applitools.eyes.playwright.universal.dto.FrameLocator;
import com.applitools.eyes.playwright.universal.dto.Selector;
import com.applitools.eyes.universal.dto.ContextReferenceDto;
import com.microsoft.playwright.ElementHandle;

import java.util.List;
import java.util.stream.Collectors;

public class TFramesMapper {

    public static ContextReferenceDto toContextReferenceDto(FrameLocator frame, Refer refer) {
        if (frame == null) {
            return null;
        }

        ContextReferenceDto contextReferenceDto = new ContextReferenceDto();

        String frameNameOrId = frame.getFrameNameOrId();
        if (frameNameOrId != null || !frameNameOrId.equals("")) {
            contextReferenceDto.setFrame(frameNameOrId);
        }

        Integer frameIndex = frame.getFrameIndex();
        if (frameIndex != null) {
            contextReferenceDto.setFrame(frameIndex);
        }

        Element frameElement = frame.getFrameReference();
        if (frameElement != null) {
            ElementHandle elementHandle = frameElement.getElementHandle();
            frameElement.setApplitoolsRefId(refer.ref(elementHandle));
            contextReferenceDto.setFrame(frameElement);
        }

        Selector scrollSelector = frame.getScrollRootSelector();
        if (scrollSelector != null) {
            scrollSelector.setApplitoolsRefId(refer.ref(scrollSelector));
            contextReferenceDto.setScrollRootElement(scrollSelector);
        }

        Element scrollElement = frame.getScrollRootElement();
        if (scrollElement != null) {
            ElementHandle elementHandle = scrollElement.getElementHandle();
            scrollElement.setApplitoolsRefId(refer.ref(elementHandle));
            contextReferenceDto.setScrollRootElement(scrollElement);
        }

        return contextReferenceDto;
    }

    public static List<ContextReferenceDto> toTFramesFromCheckSettings(List<FrameLocator> frameChain, Refer refer) {
        if (frameChain == null || frameChain.isEmpty()) {
            return null;
        }

        return frameChain.stream()
                .map(frame -> toContextReferenceDto(frame, refer))
                .collect(Collectors.toList());
    }
}
