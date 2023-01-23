package com.applitools.eyes.playwright.universal.mapper;

import com.applitools.ICheckSettings;
import com.applitools.eyes.Region;
import com.applitools.eyes.playwright.fluent.PlaywrightCheckSettings;
import com.applitools.eyes.playwright.universal.Refer;
import com.applitools.eyes.playwright.universal.dto.Element;
import com.applitools.eyes.playwright.universal.dto.Selector;
import com.applitools.eyes.universal.Reference;
import com.applitools.eyes.universal.dto.TRegion;
import com.applitools.eyes.universal.mapper.RectangleRegionMapper;
import com.microsoft.playwright.ElementHandle;

public class TRegionMapper {

    public static TRegion toTRegionFromCheckSettings(ICheckSettings checkSettings, Refer refer, Reference root) {
        if (!(checkSettings instanceof PlaywrightCheckSettings)) {
            return null;
        }

        PlaywrightCheckSettings playwrightCheckSettings = (PlaywrightCheckSettings) checkSettings;

        Reference element = playwrightCheckSettings.getTargetElement();
        if (element != null) {
            if (element instanceof Element) {
                ElementHandle elementHandle = ((Element) element).getElementHandle();
                element.setApplitoolsRefId(refer.ref(elementHandle, root));
            } else if (element instanceof Selector){
                Selector selector = (Selector) element;
                if (selector.getLocator() != null) {
                    element.setApplitoolsRefId(refer.ref(element, root));
                }
            }
            return element;
        }

        Region region = playwrightCheckSettings.getTargetRegion();
        if (region != null) {
            return RectangleRegionMapper.toRectangleRegionDto(region);
        }

        return null;
    }

    public static TRegion toTRegionDtoFromSRE(Reference scrollRootElement, Refer refer, Reference root) {
        if (scrollRootElement == null) {
            return null;
        }

        if (scrollRootElement instanceof Element) {
            ElementHandle elementHandle = ((Element) scrollRootElement).getElementHandle();
            scrollRootElement.setApplitoolsRefId(refer.ref(elementHandle, root));
        } else {
            scrollRootElement.setApplitoolsRefId(refer.ref(scrollRootElement, root));
        }
        return scrollRootElement;
    }
}
