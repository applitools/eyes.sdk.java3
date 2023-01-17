package com.applitools.eyes.playwright.universal.mapper;

import com.applitools.ICheckSettings;
import com.applitools.eyes.Region;
import com.applitools.eyes.playwright.PlaywrightCheckSettings;
import com.applitools.eyes.playwright.universal.Refer;
import com.applitools.eyes.playwright.universal.driver.Element;
import com.applitools.eyes.universal.Reference;
import com.applitools.eyes.universal.dto.TRegion;
import com.applitools.eyes.universal.mapper.RectangleRegionMapper;
import com.microsoft.playwright.ElementHandle;

public class TRegionMapper {

    public static TRegion toTRegionFromCheckSettings(ICheckSettings checkSettings, Refer refer) {
        if (!(checkSettings instanceof PlaywrightCheckSettings)) {
            return null;
        }

        PlaywrightCheckSettings playwrightCheckSettings = (PlaywrightCheckSettings) checkSettings;

        Reference element = playwrightCheckSettings.getTargetElement();
        if (element != null) {
            if (element instanceof Element) {
                ElementHandle elementHandle = ((Element) element).getElementHandle();
                element.setApplitoolsRefId(refer.ref(elementHandle));
            } else {
                element.setApplitoolsRefId(refer.ref(element));
            }
            return element;
        }

        Region region = playwrightCheckSettings.getTargetRegion();
        if (region != null) {
            return RectangleRegionMapper.toRectangleRegionDto(region);
        }

        return null;
    }

    public static TRegion toTRegionDtoFromSRE(Reference scrollRootElement, Refer refer) {
        if (scrollRootElement == null) {
            return null;
        }

        if (scrollRootElement instanceof Element) {
            ElementHandle elementHandle = ((Element) scrollRootElement).getElementHandle();
            scrollRootElement.setApplitoolsRefId(refer.ref(elementHandle));
        } else {
            scrollRootElement.setApplitoolsRefId(refer.ref(scrollRootElement));
        }
        return scrollRootElement;
    }
}
