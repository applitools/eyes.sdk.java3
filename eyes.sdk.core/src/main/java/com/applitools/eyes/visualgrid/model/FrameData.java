package com.applitools.eyes.visualgrid.model;

import java.util.List;

public class FrameData
{
    private  List<CdtData> cdt; 

    private String url;

    private List<String> resourceUrls;
    
    private List<BlobData> blobs;

    private List<FrameData> frames;

    private String srcAttr;

    public FrameData() {
    }

    public List<CdtData> getCdt() {
        return cdt;
    }

    public void setCdt(List<CdtData> cdt) {
        this.cdt = cdt;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public List<String> getResourceUrls() {
        return resourceUrls;
    }

    public void setResourceUrls(List<String> resourceUrls) {
        this.resourceUrls = resourceUrls;
    }

    public List<BlobData> getBlobs() {
        return blobs;
    }

    public void setBlobs(List<BlobData> blobs) {
        this.blobs = blobs;
    }

    public List<FrameData> getFrames() {
        return frames;
    }

    public void setFrames(List<FrameData> frames) {
        this.frames = frames;
    }

    public String getSrcAttr() {
        return srcAttr;
    }

    public void setSrcAttr(String srcAttr) {
        this.srcAttr = srcAttr;
    }
}
