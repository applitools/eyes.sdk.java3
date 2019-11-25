package com.applitools.eyes.visualgrid.model;

import com.applitools.eyes.visualgrid.services.VisualGridTask;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class  RenderRequest {
    @JsonInclude
    private String renderId;

    @JsonIgnore
    private final VisualGridTask visualGridTask;

    @JsonInclude
    private String agentId = "eyes.selenium.visualgrid.java/3.158.8";

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String webhook;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String url;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private RGridDom dom;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Map<String, RGridResource> resources;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private RenderInfo renderInfo;

    @JsonIgnore
    private String platform;

    @JsonIgnore
    private String browserName;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Object scriptHooks;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<VisualGridSelector> selectorsToFindRegionsFor;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private boolean sendDom;

    public RenderRequest(String webHook, String url, RGridDom dom, Map<String, RGridResource> resources, RenderInfo renderInfo, String platform, String browserName, Object scriptHooks, List<VisualGridSelector> selectorsToFindRegionsFor, boolean sendDom, VisualGridTask visualGridTask) {
        this.webhook = webHook;
        this.url = url;
        this.dom = dom;
        this.resources = resources;
        this.renderInfo = renderInfo;
        this.platform = platform;
        this.browserName = browserName;
        this.scriptHooks = scriptHooks;
        this.selectorsToFindRegionsFor = selectorsToFindRegionsFor;
        this.sendDom = sendDom;
        this.visualGridTask = visualGridTask;

    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public RGridDom getDom() {
        return dom;
    }

    public void setDom(RGridDom dom) {
        this.dom = dom;
    }

    public Map<String, RGridResource> getResources() {
        return resources;
    }

    public void setResources(Map<String, RGridResource> resources) {
        this.resources = resources;
    }

    public RenderInfo getRenderInfo() {
        return renderInfo;
    }

    public void setRenderInfo(RenderInfo renderInfo) {
        this.renderInfo = renderInfo;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getBrowserName() {
        return browserName;
    }

    public void setBrowserName(String browserName) {
        this.browserName = browserName;
    }

    public Object getScriptHooks() {
        return scriptHooks;
    }

    public void setScriptHooks(Object scriptHooks) {
        this.scriptHooks = scriptHooks;
    }

    public List<VisualGridSelector> getSelectorsToFindRegionsFor() {
        return selectorsToFindRegionsFor;
    }

    public void setSelectorsToFindRegionsFor(List<VisualGridSelector> selectorsToFindRegionsFor) {
        this.selectorsToFindRegionsFor = selectorsToFindRegionsFor;
    }

    public boolean isSendDom() {
        return sendDom;
    }

    public void setSendDom(boolean sendDom) {
        this.sendDom = sendDom;
    }

    @JsonProperty("browser")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public Map<String,String> getBrowser(){
        Map<String,String> map = new HashMap<>();
        map.put("name", this.browserName);
        map.put("platform", this.platform);
        return map;
    }

    public VisualGridTask getVisualGridTask() {
        return visualGridTask;
    }

    public String getWebhook() {
        return webhook;
    }

    public String getAgentId() {
        return agentId;
    }

    public String getRenderId() {
        return renderId;
    }

    public void setRenderId(String renderId) {
        this.renderId = renderId;
    }

    @Override
    public String toString() {
        return "RenderRequest{" +
                "renderId='" + renderId + '\'' +
                ", visualGridTask=" + visualGridTask +
                ", agentId='" + agentId + '\'' +
                ", webhook='" + webhook + '\'' +
                ", url='" + url + '\'' +
                ", dom=" + dom +
                ", resources=" + resources +
                ", renderInfo=" + renderInfo +
                ", platform='" + platform + '\'' +
                ", browserName='" + browserName + '\'' +
                ", scriptHooks=" + scriptHooks +
                ", selectorsToFindRegionsFor=" + selectorsToFindRegionsFor +
                ", sendDom=" + sendDom +
                '}';
    }
}
