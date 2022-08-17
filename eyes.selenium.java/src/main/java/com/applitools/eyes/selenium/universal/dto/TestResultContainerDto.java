package com.applitools.eyes.selenium.universal.dto;

import com.applitools.eyes.TestResults;
import com.applitools.eyes.selenium.EyesError;

/**
 * Test result container dto
 */
public class TestResultContainerDto {
  private TestResults testResults;
  private EyesError exception;
  private BrowserInfoDto browserInfo;
  private String userTestId;

  public TestResultContainerDto() {
  }

  public TestResultContainerDto(TestResults testResults, EyesError exception, BrowserInfoDto browserInfo) {
    this.testResults = testResults;
    this.exception = exception;
    this.browserInfo = browserInfo;
  }

  public TestResults getTestResults() {
    return testResults;
  }

  public void setTestResults(TestResults testResults) {
    this.testResults = testResults;
  }

  public EyesError getException() {
    return exception;
  }

  public void setException(EyesError exception) {
    this.exception = exception;
  }

  public BrowserInfoDto getBrowserInfo() {
    return browserInfo;
  }

  public void setBrowserInfo(BrowserInfoDto browserInfo) {
    this.browserInfo = browserInfo;
  }

  public String getUserTestId() {
    return userTestId;
  }

  public void setUserTestId(String userTestId) {
    this.userTestId = userTestId;
  }
}
