
package com.applitools.eyes.metadata;

import com.applitools.eyes.Location;
import com.applitools.eyes.RectangleSize;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "id",
        "size"
})
public class Image {

    @JsonProperty("id")
    private String id;
    @JsonProperty("size")
    private RectangleSize size;
    @JsonProperty("hasDom")
    private boolean hasDom;
    @JsonProperty("domId")
    private String domId;
    @JsonProperty("location")
    private Location location;

    @JsonProperty("id")
    public String getId() {
        return id;
    }

    @JsonProperty("id")
    public void setId(String id) {
        this.id = id;
    }

    @JsonProperty("size")
    public RectangleSize getSize() {
        return size;
    }

    @JsonProperty("size")
    public void setSize(RectangleSize size) {
        this.size = size;
    }

    @JsonProperty("hasDom")
    public boolean getHasDom() {
        return hasDom;
    }

    @JsonProperty("hasDom")
    public void setHasDom(boolean hasDom) {
        this.hasDom = hasDom;
    }

    @JsonProperty("domId")
    public String getDomId() {
        return this.domId;
    }

    @JsonProperty("domId")
    public void setDomId(String domId) {
        this.domId = domId;
    }

    @JsonProperty("location")
    public Location getLocation() {
        return location;
    }

    @JsonProperty("location")
    public void setLocation(Location location) {
        this.location = location;
    }
}
