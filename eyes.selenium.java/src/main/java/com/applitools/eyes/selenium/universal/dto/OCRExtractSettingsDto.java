package com.applitools.eyes.selenium.universal.dto;

/**
 * OCR extract settings dto
 */
public class OCRExtractSettingsDto {
  private TRegion target;
  private String hint;
  private Float minMatch;
  private String language;

  public TRegion getTarget() {
    return target;
  }

  public void setTarget(TRegion target) {
    this.target = target;
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
