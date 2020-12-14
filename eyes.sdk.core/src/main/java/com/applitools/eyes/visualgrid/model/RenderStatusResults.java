package com.applitools.eyes.visualgrid.model;

import com.applitools.eyes.Location;
import com.applitools.eyes.RectangleSize;

import java.util.List;

public class RenderStatusResults {

    private String renderId = null;

    private RenderStatus status = null;

    private String domLocation = null;

    private String userAgent = null;

    private String imageLocation = null;

    private String os = null;

    private String error = null;

    private List<VGRegion> selectorRegions = null;

    private RectangleSize deviceSize = null;

    private RectangleSize visualViewport = null;

    private Location imagePositionInActiveFrame = null;

    public RenderStatus getStatus() {
        return status;
    }

    public void setStatus(RenderStatus status) {
        this.status = status;
    }

    public String getDomLocation() {
        return domLocation;
    }

    public void setDomLocation(String domLocation) {
        this.domLocation = domLocation;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public String getImageLocation() {
        return imageLocation;
    }

    public void setImageLocation(String imageLocation) {
        this.imageLocation = imageLocation;
    }

    public String getOs() {
        return os;
    }

    public void setOs(String os) {
        this.os = os;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public List<VGRegion> getSelectorRegions() {
        return selectorRegions;
    }

    public void setSelectorRegions(List<VGRegion> selectorRegions) {
        this.selectorRegions = selectorRegions;
    }

    public RectangleSize getDeviceSize() {
        return deviceSize;
    }

    public void setDeviceSize(RectangleSize deviceSize) {
        this.deviceSize = deviceSize;
    }

    public RectangleSize getVisualViewport() {
        return visualViewport;
    }

    public void setVisualViewport(RectangleSize visualViewport) {
        this.visualViewport = visualViewport;
    }

    public String getRenderId() {
        return renderId;
    }

    public void setRenderId(String renderId) {
        this.renderId = renderId;
    }

    public Location getImagePositionInActiveFrame() {
        return imagePositionInActiveFrame;
    }

    @Override
    public String toString() {
        return "RenderStatusResults{" +
                "status=" + status +
                ", domLocation='" + domLocation + '\'' +
                ", userAgent='" + userAgent + '\'' +
                ", imageLocation='" + imageLocation + '\'' +
                ", os='" + os + '\'' +
                ", error='" + error + '\'' +
                ", selectorRegions=" + selectorRegions +
                ", deviceSize=" + deviceSize +
                ", visualViewport=" + visualViewport +
                '}';
    }

    public boolean isEmpty() {
        return (selectorRegions == null || selectorRegions.isEmpty()) &&
                status == null &&
                imageLocation == null &&
                error == null &&
                os == null &&
                userAgent == null &&
                deviceSize == null &&
                selectorRegions == null;

    }
}
