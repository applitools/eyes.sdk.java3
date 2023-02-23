package com.applitools.eyes;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class DensityMetrics {

    private Double scaleRatio;
    @JsonProperty("xdpi")
    private Integer xDpi;
    @JsonProperty("ydpi")
    private Integer yDpi;

    public DensityMetrics(int xDpi, int yDpi) {
        this.xDpi = xDpi;
        this.yDpi = yDpi;
    }

    public Double getScaleRatio() {
        return scaleRatio;
    }

    public DensityMetrics scaleRatio(Double scaleRatio) {
        this.scaleRatio = scaleRatio;
        return this;
    }

    public Integer getXdpi() {
        return xDpi;
    }

    public DensityMetrics xDpi(Integer xDpi) {
        this.xDpi = xDpi;
        return this;
    }

    public Integer getYdpi() {
        return yDpi;
    }

    public DensityMetrics yDpi(Integer yDpi) {
        this.yDpi = yDpi;
        return this;
    }
}
