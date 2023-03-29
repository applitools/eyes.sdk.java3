package com.applitools.eyes.selenium.fluent;

import com.applitools.eyes.Padding;
import com.applitools.eyes.fluent.GetRegion;
import com.applitools.eyes.serializers.WebElementSerializer;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.openqa.selenium.WebElement;

public class SimpleRegionByElement implements GetRegion {

    @JsonSerialize(using = WebElementSerializer.class)
    protected final WebElement element;

    @JsonIgnore
//    protected final Borders padding;
    protected final Padding padding;

    private String regionId;

    public SimpleRegionByElement(WebElement element) {
        this(element, new Padding());
    }

    public SimpleRegionByElement(WebElement element, Padding padding) {
        this.element = element;
        this.padding = padding;
    }

    @JsonProperty("element")
    public WebElement getElement() {
        return element;
    }

    public SimpleRegionByElement regionId(String regionId) {
        this.regionId = regionId;
        return this;
    }

    public String getRegionId() {
        return regionId;
    }

    public Padding getPadding() { return this.padding; }
}
