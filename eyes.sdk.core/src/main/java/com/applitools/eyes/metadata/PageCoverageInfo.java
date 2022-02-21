package com.applitools.eyes.metadata;

import com.applitools.eyes.Location;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * page coverage info
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "pageId",
    "width",
    "height",
    "imagePositionInPage"
})
public class PageCoverageInfo {

  @JsonProperty("pageId")
  private String pageId;

  @JsonProperty("width")
  private Long width;

  @JsonProperty("height")
  private Long height;

  @JsonProperty("imagePositionInPage")
  private Location location;

  @JsonProperty("pageId")
  public String getPageId() {
    return pageId;
  }

  @JsonProperty("pageId")
  public void setPageId(String pageId) {
    this.pageId = pageId;
  }

  @JsonProperty("width")
  public Long getWidth() {
    return width;
  }

  @JsonProperty("width")
  public void setWidth(Long width) {
    this.width = width;
  }

  @JsonProperty("height")
  public Long getHeight() {
    return height;
  }

  @JsonProperty("height")
  public void setHeight(Long height) {
    this.height = height;
  }

  @JsonProperty("imagePositionInPage")
  public Location getLocation() {
    return location;
  }

  @JsonProperty("imagePositionInPage")
  public void setLocation(Location location) {
    this.location = location;
  }

}
