package com.applitools.eyes.images;

import com.applitools.eyes.locators.BaseOcrRegion;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.awt.image.BufferedImage;

public class OcrRegion extends BaseOcrRegion {

    @JsonIgnore
    private final BufferedImage image;

    public OcrRegion(BufferedImage image) {
        this.image = image;
    }

    public BufferedImage getImage() {
        return image;
    }
}
