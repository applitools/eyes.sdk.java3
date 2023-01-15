package com.applitools.eyes.playwright.universal.dto;

import com.applitools.eyes.fluent.GetRegion;
import com.applitools.eyes.playwright.universal.driver.Element;
import com.applitools.eyes.universal.dto.TFloatingRegion;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class FloatingRegionByElement extends TFloatingRegion implements GetRegion {

    protected Element region;

    public Element getRegion() {
        return region;
    }

    public void setRegion(Element region) {
        this.region = region;
    }
}
