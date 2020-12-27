package com.applitools.eyes.images;

import com.applitools.eyes.fluent.CheckSettings;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.awt.image.BufferedImage;

public class ImagesCheckSettings extends CheckSettings implements IImagesCheckTarget {

    @JsonIgnore
    private final BufferedImage image;

    public ImagesCheckSettings(BufferedImage image){

        this.image = image;
    }

    public BufferedImage getImage() {
        return image;
    }

    @Override
    public ImagesCheckSettings clone(){
        ImagesCheckSettings clone = new ImagesCheckSettings(this.image);
        super.populateClone(clone);
        return clone;
    }
}
