
package com.applitools.eyes.metadata;

import com.applitools.eyes.FloatingMatchSettings;
import com.applitools.eyes.Region;
import com.fasterxml.jackson.annotation.*;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "floating",
    "ignore",
    "strict",
    "content",
    "layout"
})
@JsonIgnoreProperties({"remarks", "mismatching"})
public class Annotations {

    @JsonProperty("floating")
    private FloatingMatchSettings[] floating = null;
    @JsonProperty("ignore")
    private Region[] ignore = null;
    @JsonProperty("strict")
    private Region[] strict = null;
    @JsonProperty("content")
    private Region[] content = null;
    @JsonProperty("layout")
    private Region[] layout = null;

    @JsonProperty("floating")
    public FloatingMatchSettings[] getFloating() {
        return floating;
    }

    @JsonProperty("floating")
    public void setFloating(FloatingMatchSettings[] floating) {
        this.floating = floating;
    }

    @JsonProperty("ignore")
    public Region[] getIgnore() {
        return ignore;
    }

    @JsonProperty("ignore")
    public void setIgnore(Region[] ignore) {
        this.ignore = ignore;
    }

    @JsonProperty("strict")
    public Region[] getStrict() {
        return strict;
    }

    @JsonProperty("strict")
    public void setStrict(Region[] strict) {
        this.strict = strict;
    }

    @JsonProperty("content")
    public Region[] getContent() {
        return content;
    }

    @JsonProperty("content")
    public void setContent(Region[] content) {
        this.content = content;
    }

    @JsonProperty("layout")
    public Region[] getLayout() {
        return layout;
    }

    @JsonProperty("layout")
    public void setLayout(Region[] layout) {
        this.layout = layout;
    }
}
