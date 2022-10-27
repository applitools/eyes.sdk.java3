package com.applitools.eyes.universal.dto;

import java.util.List;

/**
 * Test results summary dto
 */
public class TestResultsSummaryDto {
  private List<TestResultContainerDto> result;
  private Integer passed;
  private Integer unresolved;
  private Integer failed;
  private Integer exceptions;
  private Integer mismatches;
  private Integer missing;
  private Integer matches;

  public TestResultsSummaryDto() {
  }

  public List<TestResultContainerDto> getResult() {
    return result;
  }

  public void setResult(List<TestResultContainerDto> result) {
    this.result = result;
  }

  public Integer getPassed() {
    return passed;
  }

  public void setPassed(Integer passed) {
    this.passed = passed;
  }

  public Integer getUnresolved() {
    return unresolved;
  }

  public void setUnresolved(Integer unresolved) {
    this.unresolved = unresolved;
  }

  public Integer getFailed() {
    return failed;
  }

  public void setFailed(Integer failed) {
    this.failed = failed;
  }

  public Integer getExceptions() {
    return exceptions;
  }

  public void setExceptions(Integer exceptions) {
    this.exceptions = exceptions;
  }

  public Integer getMismatches() {
    return mismatches;
  }

  public void setMismatches(Integer mismatches) {
    this.mismatches = mismatches;
  }

  public Integer getMissing() {
    return missing;
  }

  public void setMissing(Integer missing) {
    this.missing = missing;
  }

  public Integer getMatches() {
    return matches;
  }

  public void setMatches(Integer matches) {
    this.matches = matches;
  }
}
