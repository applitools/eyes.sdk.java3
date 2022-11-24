package com.applitools.eyes.universal.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * @author Kanan
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ImageCropRegionDto implements ICut {
  private Integer x;
  private Integer y;
  private Integer width;
  private Integer height;

  public ImageCropRegionDto() {
  }

  public ImageCropRegionDto(Integer x, Integer y, Integer width, Integer height) {
    this.x = x;
    this.y = y;
    this.width = width;
    this.height = height;
  }

  public Integer getX() {
    return x;
  }

  public void setX(Integer x) {
    this.x = x;
  }

  public Integer getY() {
    return y;
  }

  public void setY(Integer y) {
    this.y = y;
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
}
