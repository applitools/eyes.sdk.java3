package com.applitools.eyes.visualgrid.model;

import com.applitools.eyes.Logger;
import com.applitools.utils.GeneralUtils;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.ArrayUtils;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RGridDom {

    public static final String CONTENT_TYPE = "x-applitools-html/cdt";

    @JsonIgnore
    private List domNodes = null;

    @JsonIgnore
    private Map<String, RGridResource> resources = new HashMap<>();

    @JsonIgnore
    private String sha256;

    @JsonInclude
    private String hashFormat = "sha256";

    @JsonIgnore
    private String url;
    @JsonIgnore
    private Logger logger;

    @JsonIgnore
    private String msg;
    @JsonIgnore
    private RGridResource gridResource;

    public RGridDom() {
    }

    public RGridDom(List domNodes, Map<String, RGridResource> resources, String url, Logger logger, String msg) {
        this.domNodes = domNodes;
        this.resources = resources;
        this.url = url;
        this.logger = logger;
        this.msg = msg;
    }

    public void addResource(RGridResource resource) {
        this.resources.put(resource.getUrl(), resource);
    }

    public List getDomNodes() {
        return domNodes;
    }

    public void setDomNodes(List domNodes) {
        this.domNodes = domNodes;
    }

    public Map<String, RGridResource> getResources() {
        return resources;
    }

    public void setResources(Map<String, RGridResource> resources) {
        this.resources = resources;
    }

    @JsonProperty("hash")
    public String getSha256() throws JsonProcessingException {
        if (this.sha256 == null) {
            sha256 = GeneralUtils.getSha256hash((getStringObjectMap().getBytes(StandardCharsets.UTF_8)));
        }
        return sha256;
    }

    private String getStringObjectMap() throws JsonProcessingException {

        Map<String, Object> map = new HashMap<>();
        map.put("domNodes", this.domNodes);
        map.put("resources", this.resources);

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY, true);

        return objectMapper.writeValueAsString(map);
    }

    public String getHashFormat() {
        return hashFormat;
    }

    public RGridResource asResource() throws JsonProcessingException {
       if (gridResource == null) {
           gridResource =  new RGridResource(this.url, CONTENT_TYPE, getStringObjectMap().getBytes(StandardCharsets.UTF_8), logger, "RGridDom - "+msg);
       }
        return gridResource;
    }

    public void addResources(ArrayList<RGridResource> frameResourceUrlsMapping) {
        for (RGridResource gridResource : frameResourceUrlsMapping) {
            this.resources.put(gridResource.getUrl(), gridResource);
        }
    }

    public void setUrl(String url) {
        this.url = url;
    }


    public String getUrl() {
        return url;
    }
}
