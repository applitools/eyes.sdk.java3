package com.applitools.eyes.universal.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * @author Kanan
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ImageCropRectDto implements ICut {
  private Integer top;
  private Integer right;
  private Integer bottom;
  private Integer left;

  public ImageCropRectDto() {
  }

  public ImageCropRectDto(Integer top, Integer right, Integer bottom, Integer left) {
    this.top = top;
    this.right = right;
    this.bottom = bottom;
    this.left = left;
  }

  public Integer getTop() {
    return top;
  }

  public void setTop(Integer top) {
    this.top = top;
  }

  public Integer getRight() {
    return right;
  }

  public void setRight(Integer right) {
    this.right = right;
  }

  public Integer getBottom() {
    return bottom;
  }

  public void setBottom(Integer bottom) {
    this.bottom = bottom;
  }

  public Integer getLeft() {
    return left;
  }

  public void setLeft(Integer left) {
    this.left = left;
  }
}
