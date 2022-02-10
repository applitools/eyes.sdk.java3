package com.applitools.eyes.selenium.universal.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * OCR search settings
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OCRSearchSettingsDto {
  private List<String> patterns;
  private Boolean ignoreCase;
  private Boolean firstOnly;
  private String language;

  public List<String> getPatterns() {
    return patterns;
  }

  public void setPatterns(List<String> patterns) {
    this.patterns = patterns;
  }

  public Boolean getIgnoreCase() {
    return ignoreCase;
  }

  public void setIgnoreCase(Boolean ignoreCase) {
    this.ignoreCase = ignoreCase;
  }

  public Boolean getFirstOnly() {
    return firstOnly;
  }

  public void setFirstOnly(Boolean firstOnly) {
    this.firstOnly = firstOnly;
  }

  public String getLanguage() {
    return language;
  }

  public void setLanguage(String language) {
    this.language = language;
  }
}
