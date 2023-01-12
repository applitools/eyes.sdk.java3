package com.applitools.eyes.playwright.universal.driver;

import com.applitools.eyes.Padding;
import com.applitools.eyes.universal.Reference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.microsoft.playwright.ElementHandle;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Element extends Reference {

    private String type;

    @JsonIgnore
    private ElementHandle elementHandle;

    @JsonProperty("padding")
    private Padding padding;
    @JsonProperty("regionId")
    private String regionId;

    public Element() {
        this.type = "element";
    }

    public Element(ElementHandle element) {
        this();
        this.elementHandle = element;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public ElementHandle getElementHandle() {
        return elementHandle;
    }

    public void setElementHandle(ElementHandle elementHandle) {
        this.elementHandle = elementHandle;
    }

    public Padding getPadding() {
        return padding;
    }

    public void setPadding(Padding padding) {
        this.padding = padding;
    }

    public String getRegionId() {
        return regionId;
    }

    public void setRegionId(String regionId) {
        this.regionId = regionId;
    }
}
