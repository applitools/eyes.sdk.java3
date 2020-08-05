package com.applitools.eyes.visualgrid.model;

import com.applitools.utils.GeneralUtils;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

public class RGridResource {

    @JsonIgnore
    private static final int MAX_RESOURCE_SIZE = 15 * 1024 * 1024;

    @JsonIgnore
    private String url;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private final String contentType;

    @JsonIgnore
    private byte[] content = null;

    @JsonProperty("hash")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String sha256 = null;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private final Integer errorStatusCode;

    @JsonInclude
    private final String hashFormat = "sha256";

    @JsonIgnore
    private AtomicBoolean isResourceParsed = new AtomicBoolean(false);

    public static RGridResource createEmpty(String url) {
        return new RGridResource(url, "application/empty-response", new byte[]{});
    }

    public String getUrl() {
        return url;
    }

    public RGridResource(String url, String contentType, byte[] content) {
        this(url, contentType, content, null);
    }

    public RGridResource(String url, String contentType, byte[] content, Integer errorStatusCode) {
        this.contentType = contentType;
        this.url = url;
        this.errorStatusCode = errorStatusCode;
        if (content != null) {
            this.content = content.length > MAX_RESOURCE_SIZE ? Arrays.copyOf(content, MAX_RESOURCE_SIZE) : content;
            this.sha256 = GeneralUtils.getSha256hash(this.content);
        }
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

    public Integer getErrorStatusCode() {
        return errorStatusCode;
    }

    public void setIsResourceParsed(Boolean isResourceParsed) {
        this.isResourceParsed.set(isResourceParsed);
    }

    @JsonIgnore
    public boolean isResourceParsed() {
        return isResourceParsed.get();
    }

    @Override
    public String toString() {
        return "RGridResource{" + "url='" + url + '\'' + '}';
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

