package com.applitools.eyes.selenium.universal.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * region dto
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RegionDto extends TRegion {
  private Integer x;
  private Integer y;
  private Integer left;
  private Integer top;
  private Integer width;
  private Integer height;
  private String coordinatesType;

  public Integer getLeft() {
    return left;
  }

  public void setLeft(Integer left) {
    this.left = left;
  }

  public Integer getTop() {
    return top;
  }

  public void setTop(Integer top) {
    this.top = top;
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

  public String getCoordinatesType() {
    return coordinatesType;
  }

  public void setCoordinatesType(String coordinatesType) {
    this.coordinatesType = coordinatesType;
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
}
