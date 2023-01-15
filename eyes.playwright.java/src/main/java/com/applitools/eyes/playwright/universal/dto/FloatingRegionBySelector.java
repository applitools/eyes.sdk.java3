package com.applitools.eyes.playwright.universal.dto;

import com.applitools.eyes.fluent.GetRegion;
import com.applitools.eyes.playwright.universal.driver.Selector;
import com.applitools.eyes.universal.dto.TFloatingRegion;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class FloatingRegionBySelector extends TFloatingRegion implements GetRegion {

    protected Selector region;


    public Selector getRegion() {
        return region;
    }

    public void setRegion(Selector region) {
        this.region = region;
    }
}
