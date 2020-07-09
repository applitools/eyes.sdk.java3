package com.applitools.eyes.visualgrid.model;

public class RenderingInfo {

    private String serviceUrl = null;
    private String accessToken = null;
    private String resultsUrl = null;
    private String stitchingServiceUrl = null;
    private int maxImageHeight;
    private int maxImageArea;

    public RenderingInfo(String serviceUrl, String accessToken, String resultsUrl, String stitchingServiceUrl, int maxImageHeight, int maxImageArea) {
        this.serviceUrl = serviceUrl;
        this.accessToken = accessToken;
        this.resultsUrl = resultsUrl;
        this.stitchingServiceUrl = stitchingServiceUrl;
        this.maxImageHeight = maxImageHeight;
        this.maxImageArea = maxImageArea;
    }

    public RenderingInfo() {
    }

    public String getServiceUrl() {
        return serviceUrl;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getResultsUrl() {
        return resultsUrl;
    }

    public String getStitchingServiceUrl() {
        return stitchingServiceUrl;
    }

    public int getMaxImageHeight() {
        return maxImageHeight;
    }

    public int getMaxImageArea() {
        return maxImageArea;
    }
}
