package com.applitools.eyes.selenium.universal.dto.response;

import java.util.List;

import com.applitools.eyes.SessionAccessibilityStatus;
import com.applitools.eyes.selenium.universal.dto.RectangleSizeDto;
import com.applitools.eyes.selenium.universal.dto.SessionUrlsDto;
import com.applitools.eyes.selenium.universal.dto.StepInfoDto;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonSetter;

/**
 * command close response to
 */
public class CommandCloseResponseDto {
  private String userTestId;
  private String id;
  private String name;
  private String secretToken;
  private String status;
  private String appName;
  private String batchName;
  private String batchId;
  private String branchName;
  private String hostOS;
  private String hostApp;
  private RectangleSizeDto hostDisplaySize;
  private String startedAt;
  private Integer duration;
  private Boolean isNew;
  private boolean isDifferent;
  private boolean isAborted;
  private SessionUrlsDto appUrls;
  private SessionUrlsDto apiUrls;
  private List<StepInfoDto> stepsInfo;
  private int steps;
  private int matches;
  private int mismatches;
  private int missing;
  private int exactMatches;
  private int strictMatches;
  private int contentMatches;
  private int layoutMatches;
  private int noneMatches;
  private String url;
  private SessionAccessibilityStatus accessibilityStatus;

  public CommandCloseResponseDto() {
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getSecretToken() {
    return secretToken;
  }

  public void setSecretToken(String secretToken) {
    this.secretToken = secretToken;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public String getAppName() {
    return appName;
  }

  public void setAppName(String appName) {
    this.appName = appName;
  }

  public String getBatchName() {
    return batchName;
  }

  public void setBatchName(String batchName) {
    this.batchName = batchName;
  }

  public String getBatchId() {
    return batchId;
  }

  public void setBatchId(String batchId) {
    this.batchId = batchId;
  }

  public String getBranchName() {
    return branchName;
  }

  public void setBranchName(String branchName) {
    this.branchName = branchName;
  }

  public String getHostOS() {
    return hostOS;
  }

  public void setHostOS(String hostOS) {
    this.hostOS = hostOS;
  }

  public String getHostApp() {
    return hostApp;
  }

  public void setHostApp(String hostApp) {
    this.hostApp = hostApp;
  }

  public RectangleSizeDto getHostDisplaySize() {
    return hostDisplaySize;
  }

  public void setHostDisplaySize(RectangleSizeDto hostDisplaySize) {
    this.hostDisplaySize = hostDisplaySize;
  }

  public String getStartedAt() {
    return startedAt;
  }

  public void setStartedAt(String startedAt) {
    this.startedAt = startedAt;
  }

  public Integer getDuration() {
    return duration;
  }

  public void setDuration(Integer duration) {
    this.duration = duration;
  }

  @JsonGetter("isNew")
  public Boolean isNew() {
    return isNew;
  }

  @JsonSetter("isNew")
  public void setNew(Boolean aNew) {
    isNew = aNew;
  }

  @JsonGetter("isDifferent")
  public Boolean getDifferent() {
    return isDifferent;
  }

  @JsonSetter("isDifferent")
  public void setDifferent(Boolean different) {
    isDifferent = different;
  }

  @JsonGetter("isAborted")
  public Boolean getAborted() {
    return isAborted;
  }

  @JsonSetter("isAborted")
  public void setAborted(Boolean aborted) {
    isAborted = aborted;
  }

  public SessionUrlsDto getAppUrls() {
    return appUrls;
  }

  public void setAppUrls(SessionUrlsDto appUrls) {
    this.appUrls = appUrls;
  }

  public SessionUrlsDto getApiUrls() {
    return apiUrls;
  }

  public void setApiUrls(SessionUrlsDto apiUrls) {
    this.apiUrls = apiUrls;
  }

  public List<StepInfoDto> getStepsInfo() {
    return stepsInfo;
  }

  public void setStepsInfo(List<StepInfoDto> stepsInfo) {
    this.stepsInfo = stepsInfo;
  }

  public Integer getSteps() {
    return steps;
  }

  public void setSteps(Integer steps) {
    this.steps = steps;
  }

  public Integer getMatches() {
    return matches;
  }

  public void setMatches(Integer matches) {
    this.matches = matches;
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

  public Integer getExactMatches() {
    return exactMatches;
  }

  public void setExactMatches(Integer exactMatches) {
    this.exactMatches = exactMatches;
  }

  public Integer getStrictMatches() {
    return strictMatches;
  }

  public void setStrictMatches(Integer strictMatches) {
    this.strictMatches = strictMatches;
  }

  public Integer getContentMatches() {
    return contentMatches;
  }

  public void setContentMatches(Integer contentMatches) {
    this.contentMatches = contentMatches;
  }

  public Integer getLayoutMatches() {
    return layoutMatches;
  }

  public void setLayoutMatches(Integer layoutMatches) {
    this.layoutMatches = layoutMatches;
  }

  public Integer getNoneMatches() {
    return noneMatches;
  }

  public void setNoneMatches(Integer noneMatches) {
    this.noneMatches = noneMatches;
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public SessionAccessibilityStatus getAccessibilityStatus() {
    return accessibilityStatus;
  }

  public void setAccessibilityStatus(SessionAccessibilityStatus accessibilityStatus) {
    this.accessibilityStatus = accessibilityStatus;
  }

  public String getUserTestId() {
    return userTestId;
  }

  public void setUserTestId(String userTestId) {
    this.userTestId = userTestId;
  }
}
