package com.applitools.eyes.universal.dto;

import com.applitools.eyes.Location;
import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * the image target.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ImageTargetDto implements ITargetDto {

    private String image;
    private String name;
    private String source;
    private String dom;
    private Location locationInViewport;
    private Location locationInView;
    private RectangleSizeDto fullViewSize;


    public String getImage() { return image; }

    public void setImage(String image) { this.image = image; }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public String getSource() { return source; }

    public void setSource(String source) { this.source = source; }

    public String getDom() { return dom; }

    public void setDom(String dom) { this.dom = dom; }

    public Location getLocationInViewport() { return locationInViewport; }

    public void setLocationInViewport(Location locationInViewport) { this.locationInViewport = locationInViewport; }

    public Location getLocationInView() { return locationInView; }

    public void setLocationInView(Location locationInView) { this.locationInView = locationInView; }

    public RectangleSizeDto getFullViewSize() { return fullViewSize; }

    public void setFullViewSize(RectangleSizeDto fullViewSize) { this.fullViewSize = fullViewSize; }
}
