package com.applitools.eyes.visualgrid.model;


import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class BlobData {

    public static final int MAX_RESOURCE_SIZE = 15 * 1024 * 1024;
    private String url;

    private String type;

    private String value;

    public BlobData() {
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value.getBytes(StandardCharsets.UTF_8).length > MAX_RESOURCE_SIZE ? new String(Arrays.copyOf(value.getBytes(StandardCharsets.UTF_8), MAX_RESOURCE_SIZE)) : value;
    }

    @Override
    public String toString() {
        return "BlobData{" +
                "type='" + type + '\'' +
                ", url='" + url + '\'' +
                '}';
    }
}
