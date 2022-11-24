package com.applitools.eyes.universal.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * OCR extract settings dto
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OCRExtractSettingsDto {
  private TRegion region;
  private String hint;
  private Float minMatch;
  private String language;

  public TRegion getRegion() {
    return region;
  }

  public void setRegion(TRegion region) {
    this.region = region;
  }

  public String getHint() {
    return hint;
  }

  public void setHint(String hint) {
    this.hint = hint;
  }

  public Float getMinMatch() {
    return minMatch;
  }

  public void setMinMatch(Float minMatch) {
    this.minMatch = minMatch;
  }

  public String getLanguage() {
    return language;
  }

  public void setLanguage(String language) {
    this.language = language;
  }
}
