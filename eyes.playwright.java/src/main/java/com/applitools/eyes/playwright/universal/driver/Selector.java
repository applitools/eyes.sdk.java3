package com.applitools.eyes.playwright.universal.driver;

import com.applitools.eyes.Padding;
import com.applitools.eyes.universal.Reference;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Selector extends Reference {

    @JsonProperty("type")
    private String type;
    @JsonProperty("selector")
    private String selector;
    @JsonProperty("padding")
    private Padding padding;

    public Selector() {
        this.type = "selector";
    }

    public Selector(String selector) {
        this();
        this.selector = selector;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSelector() {
        return selector;
    }

    public void setSelector(String selector) {
        this.selector = selector;
    }

    public void setPadding(Padding padding) {
        this.padding = padding;
    }

    public Padding getPadding() {
        return padding;
    }
}
