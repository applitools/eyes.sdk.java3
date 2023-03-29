package com.applitools.eyes.selenium.fluent;

import com.applitools.eyes.*;
import com.applitools.eyes.fluent.GetRegion;
import com.applitools.eyes.serializers.BySerializer;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.openqa.selenium.*;

public class SimpleRegionBySelector implements GetRegion {

    @JsonSerialize(using = BySerializer.class)
    private final By selector;
    @JsonIgnore
//    private final Borders padding;
    private final Padding padding;

    private String regionId;

    public SimpleRegionBySelector(By selector) {
        this(selector, new Padding());
    }

    public SimpleRegionBySelector(By selector, Padding padding) {
        this.selector = selector;
        this.padding = padding;
    }

    public By getSelector() {
        return selector;
    }

    public SimpleRegionBySelector regionId(String regionId) {
        this.regionId = regionId;
        return this;
    }

    public String getRegionId() {
        return regionId;
    }

    public Padding getPadding() { return this.padding; }
}
