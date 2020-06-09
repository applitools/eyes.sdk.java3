package com.applitools.eyes.visualgrid.model;

public class RenderingInfo {

    private String serviceUrl = null;
    private String accessToken = null;
    private String resultsUrl = null;
    private String stitchingServiceUrl = null;

    public RenderingInfo(String serviceUrl, String accessToken, String resultsUrl,String stitchingServiceUrl) {
        this.serviceUrl = serviceUrl;
        this.accessToken = accessToken;
        this.resultsUrl = resultsUrl;
        this.stitchingServiceUrl = stitchingServiceUrl;
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

    public void setStitchingServiceUrl(String stitchingServiceUrl) {
        this.stitchingServiceUrl = stitchingServiceUrl;
    }
}
