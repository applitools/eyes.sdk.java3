package com.applitools.eyes.visualgrid.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RunningRender {

    private String renderId = null;

    private String jobId = null;

    private RenderStatus renderStatus = null;

    private List<String> needMoreResources = null;

    private boolean needMoreDom = false;

    public RunningRender() {
    }

    public RunningRender(String renderId, String jobId, RenderStatus renderStatus, List<String> needMoreResources, boolean needMoreDom) {
        this.renderId = renderId;
        this.jobId = jobId;
        this.renderStatus = renderStatus;
        this.needMoreResources = needMoreResources;
        this.needMoreDom = needMoreDom;


    }

    public String getRenderId() {
        return renderId;
    }

    public void setRenderId(String renderId) {
        this.renderId = renderId;
    }

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public RenderStatus getRenderStatus() {
        return renderStatus;
    }

    public void setRenderStatus(RenderStatus renderStatus) {
        this.renderStatus = renderStatus;
    }

    public List<String> getNeedMoreResources() {
        return needMoreResources;
    }

    public void setNeedMoreResources(List<String> needMoreResources) {
        this.needMoreResources = needMoreResources;
    }

    public boolean isNeedMoreDom() {
        return needMoreDom;
    }

    public void setNeedMoreDom(boolean needMoreDom) {
        this.needMoreDom = needMoreDom;
    }

    @Override
    public String toString() {
        return "RunningRender{" +
                "renderId='" + renderId + '\'' +
                ", jobId='" + jobId + '\'' +
                ", renderStatus=" + renderStatus +
                ", needMoreResources=" + needMoreResources +
                ", needMoreDom=" + needMoreDom +
                '}';
    }


}
