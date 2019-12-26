package com.applitools.eyes.visualgrid.model;

import com.applitools.eyes.Logger;
import com.applitools.utils.GeneralUtils;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

public class RGridResource {

    @JsonIgnore
    private static final int MAX_RESOURCE_SIZE = 15 * 1024 * 1024;

    @JsonIgnore
    private String url;

    @JsonInclude
    private final String contentType;

    @JsonIgnore
    private final byte[] content;

    @JsonProperty("hash")
    private final String sha256;

    @JsonInclude
    private final String hashFormat = "sha256";

    @JsonIgnore
    private Logger logger;

    @JsonIgnore
    private AtomicBoolean isResourceParsed = new AtomicBoolean(false);

    public String getUrl() {
        return url;
    }

    public RGridResource(String url, String contentType, byte[] content, Logger logger, String msg) {

        this.contentType = contentType;
        this.content = content.length > MAX_RESOURCE_SIZE ? Arrays.copyOf(content, MAX_RESOURCE_SIZE) : content;
        this.logger = logger;
        this.sha256 = GeneralUtils.getSha256hash(this.content);
        this.url = url;
    }

    public String getContentType() {
        return contentType;
    }


    public byte[] getContent() {
        return content;
    }

    public String getHashFormat() {
        return hashFormat;
    }

    public String getSha256() {
        return sha256;
    }

    @Override
    public String toString() {
        return "RGridResource{" + "url='" + url + '\'' + '}';
    }

    public void setIsResourceParsed(Boolean isResourceParsed) {
        this.isResourceParsed.set(isResourceParsed);
    }
    @JsonIgnore
    public boolean isResourceParsed() {
        return isResourceParsed.get();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RGridResource)) return false;
        RGridResource that = (RGridResource) o;
        return Arrays.equals(getContent(), that.getContent()) &&
                getSha256().equals(that.getSha256()) &&
                getHashFormat().equals(that.getHashFormat());
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(getSha256(), getHashFormat());
        result = 31 * result + Arrays.hashCode(getContent());
        return result;
    }
}

