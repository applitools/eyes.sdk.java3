package com.applitools.eyes.universal.dto;

import com.applitools.eyes.EyesError;
import com.applitools.eyes.TestResults;
import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Test result container dto
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TestResultContainerDto {
  private TestResults result;
  private EyesError error;
  private BrowserInfoDto renderer;
  private String userTestId;

  public TestResultContainerDto() {
  }

  public TestResultContainerDto(TestResults results, EyesError error, BrowserInfoDto renderer, String userTestId) {
    this.result = results;
    this.error = error;
    this.renderer = renderer;
    this.userTestId = userTestId;
  }

  public TestResults getResults() {
    return result;
  }

  public void setResults(TestResults results) {
    this.result = results;
  }

  public EyesError getError() {
    return error;
  }

  public void setError(EyesError error) {
    this.error = error;
  }

  public BrowserInfoDto getRenderer() {
    return renderer;
  }

  public void setRenderer(BrowserInfoDto renderer) {
    this.renderer = renderer;
  }

  public String getUserTestId() {
    return userTestId;
  }

  public void setUserTestId(String userTestId) {
    this.userTestId = userTestId;
  }
}
