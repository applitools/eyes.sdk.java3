package com.applitools.eyes.images;

import com.applitools.eyes.Region;
import com.applitools.eyes.fluent.CheckSettings;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.awt.image.BufferedImage;

@JsonIgnoreProperties({
        "image",
        "path",
})
public class ImagesCheckSettings extends CheckSettings implements IImagesCheckTarget {

    private BufferedImage image;
    private String path;

    public ImagesCheckSettings(BufferedImage image){
        this.image = image;
    }

    public ImagesCheckSettings(String path) {
        this.path = path;
    }

    public ImagesCheckSettings(BufferedImage image, Region region){
        this.image = image;
        updateTargetRegion(region);
    }

    public BufferedImage getImage() {
        return image;
    }

    public String getPath() {
        return path;
    }

    @Override
    public ImagesCheckSettings clone(){
        ImagesCheckSettings clone = new ImagesCheckSettings(this.image);
        super.populateClone(clone);
        clone.image = this.image;
        clone.path = this.path;
        return clone;
    }

    @Override
    public ImagesCheckSettings densityMetrics(int xDpi, int yDpi) {
        return (ImagesCheckSettings) super.densityMetrics(xDpi, yDpi);
    }

    @Override
    public ImagesCheckSettings densityMetrics(int xDpi, int yDpi, Double scaleRatio) {
        return (ImagesCheckSettings) super.densityMetrics(xDpi, yDpi, scaleRatio);
    }
}