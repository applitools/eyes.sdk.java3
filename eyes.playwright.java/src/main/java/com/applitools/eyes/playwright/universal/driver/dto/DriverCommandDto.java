package com.applitools.eyes.playwright.universal.driver.dto;

import com.applitools.eyes.playwright.universal.driver.Selector;
import com.applitools.eyes.universal.Reference;
import com.applitools.eyes.universal.dto.RectangleSizeDto;
import com.fasterxml.jackson.annotation.JsonProperty;

public class DriverCommandDto {

    @JsonProperty("driver")
    protected Reference driver;

    @JsonProperty("context")
    protected Reference context;

    @JsonProperty("element")
    protected Reference element;

    @JsonProperty("parent")
    protected Reference parent;

    @JsonProperty("selector")
    protected Selector selector;

    @JsonProperty("script")
    protected String script;

    @JsonProperty("arg")
    protected Object arg;

    @JsonProperty("size")
    protected RectangleSizeDto size;

    @JsonProperty("url")
    protected String url;

    DriverCommandDto() {

    }

    public Reference getDriver() {
        return driver;
    }

    public void setDriver(Reference driver) {
        this.driver = driver;
    }

    public Reference getContext() {
        return context;
    }

    public void setContext(Reference context) {
        this.context = context;
    }

    public Reference getElement() {
        return element;
    }

    public void setElement(Reference element) {
        this.element = element;
    }

    public Reference getParent() {
        return parent;
    }

    public void setParent(Reference parent) {
        this.parent = parent;
    }

    public Selector getSelector() {
        return selector;
    }

    public void setSelector(Selector selector) {
        this.selector = selector;
    }

    public String getScript() {
        return script;
    }

    public void setScript(String script) {
        this.script = script;
    }

    public Object getArg() {
        return arg;
    }

    public void setArg(Object arg) {
        this.arg = arg;
    }

    public RectangleSizeDto getSize() {
        return size;
    }

    public void setSize(RectangleSizeDto size) {
        this.size = size;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
