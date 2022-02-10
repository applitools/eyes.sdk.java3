package com.applitools.eyes.selenium.universal.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * rectangle size dto
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RectangleSizeDto {
  private Integer width;
  private Integer height;

  public RectangleSizeDto() {
  }

  public Integer getWidth() {
    return width;
  }

  public void setWidth(Integer width) {
    this.width = width;
  }

  public Integer getHeight() {
    return height;
  }

  public void setHeight(Integer height) {
    this.height = height;
  }

  @Override
  public String toString() {
    return "RectangleSizeDto{" +
        "width=" + width +
        ", height=" + height +
        '}';
  }
}
