package com.applitools.eyes.selenium.universal.dto;

/**
 * Those historically were in config
 */
public class NormalizationDto {

    /**
     * the cut
     */
    private ICut cut;

    /**
     * the rotation
     */
    private Integer rotation;

    /**
     * the scale ratio
     */
    private Double scaleRatio;

    public ICut getCut() {
        return cut;
    }

    public void setCut(ICut cut) {
        this.cut = cut;
    }

    public Integer getRotation() {
        return rotation;
    }

    public void setRotation(Integer rotation) {
        this.rotation = rotation;
    }

    public Double getScaleRatio() {
        return scaleRatio;
    }

    public void setScaleRatio(Double scaleRatio) {
        this.scaleRatio = scaleRatio;
    }
}
