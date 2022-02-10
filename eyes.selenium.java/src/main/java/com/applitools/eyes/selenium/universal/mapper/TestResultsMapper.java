package com.applitools.eyes.selenium.universal.mapper;

import java.util.List;
import java.util.stream.Collectors;

import com.applitools.eyes.SessionUrls;
import com.applitools.eyes.StepInfo;
import com.applitools.eyes.TestResults;
import com.applitools.eyes.TestResultsStatus;
import com.applitools.eyes.selenium.universal.dto.SessionUrlsDto;
import com.applitools.eyes.selenium.universal.dto.StepInfoDto;
import com.applitools.eyes.selenium.universal.dto.response.CommandCloseResponseDto;

/**
 * test results mapper
 */
public class TestResultsMapper {

  public static TestResults toTestResults(CommandCloseResponseDto response) {
    if (response == null) {
      return null;
    }

    TestResults testResults = new TestResults();
    testResults.setId(response.getId());
    testResults.setName(response.getName());
    testResults.setSecretToken(response.getSecretToken());
    testResults.setStatus(toTestResultsStatus(response.getStatus()));
    testResults.setAppName(response.getAppName());
    testResults.setBatchName(response.getBatchName());
    testResults.setBatchId(response.getBatchId());
    testResults.setBranchName(response.getBranchName());
    testResults.setHostOS(response.getHostOS());
    testResults.setHostApp(response.getHostApp());
    testResults.setHostDisplaySize(RectangleSizeMapper.toRectangleSize(response.getHostDisplaySize()));
    testResults.setStartedAt(null);    // TODO: add startedAt parameter
    testResults.setDuration(response.getDuration());
    testResults.setStepsInfo(toStepInfoArray(response.getStepsInfo()));
    testResults.setNew(response.getNew());
    testResults.setDifferent(response.getDifferent());
    testResults.setAborted(response.getAborted());
    testResults.setUrl(response.getUrl());

    SessionUrlsDto respAppUrls = response.getAppUrls();
    if (respAppUrls != null) {
      SessionUrls appUrls = new SessionUrls();
      appUrls.setSession(respAppUrls.getSession());
      appUrls.setBatch(respAppUrls.getBatch());
      testResults.setAppUrls(appUrls);
    }

    SessionUrlsDto respApiUrls = response.getApiUrls();
    if (respApiUrls != null) {
      SessionUrls apiUrls = new SessionUrls();
      apiUrls.setSession(respApiUrls.getSession());
      apiUrls.setBatch(respApiUrls.getBatch());
      testResults.setApiUrls(apiUrls);
    }

    testResults.setSteps(response.getSteps());
    testResults.setMatches(response.getMatches());
    testResults.setMismatches(response.getMismatches());
    testResults.setMissing(response.getMissing());
    testResults.setExactMatches(response.getExactMatches());
    testResults.setStrictMatches(response.getStrictMatches());
    testResults.setContentMatches(response.getContentMatches());
    testResults.setLayoutMatches(response.getLayoutMatches());
    testResults.setNoneMatches(response.getNoneMatches());
    return testResults;
  }
  

  private static StepInfo toStepInfo(StepInfoDto stepInfoDto) {
    if (stepInfoDto == null) {
      return null;
    }

    StepInfo stepInfo = new StepInfo();
    StepInfo.AppUrls appUrls = stepInfo.new AppUrls();
    appUrls.setStep(stepInfoDto.getAppUrls().getStep());
    appUrls.setStepEditor(stepInfoDto.getAppUrls().getStepEditor());
    stepInfo.setAppUrls(appUrls);
    StepInfo.ApiUrls apiUrls = stepInfo.new ApiUrls();
    apiUrls.setBaselineImage(stepInfoDto.getApiUrls().getBaselineImage());
    apiUrls.setCheckpointImage(stepInfoDto.getApiUrls().getCheckpointImage());
    apiUrls.setCheckpointImageThumbnail(stepInfoDto.getApiUrls().getCheckpointImageThumbnail());
    apiUrls.setCurrentImage(stepInfoDto.getApiUrls().getCurrentImage());
    apiUrls.setDiffImage(stepInfoDto.getApiUrls().getDiffImage());
    stepInfo.setApiUrls(apiUrls);
    stepInfo.setName(stepInfoDto.getName());
    stepInfo.setIsDifferent(stepInfoDto.getDifferent());
    stepInfo.setHasBaselineImage(stepInfoDto.getHasBaselineImage());
    stepInfo.setHasCurrentImage(stepInfoDto.getHasCurrentImage());

    return stepInfo;
  }

  private static TestResultsStatus toTestResultsStatus(String status) {
    if (status == null){
      return TestResultsStatus.Disabled;
    }
    return TestResultsStatus.valueOf(status);
  }

  private static StepInfo[] toStepInfoArray(List<StepInfoDto> stepInfoDtoList) {
    if (stepInfoDtoList == null || stepInfoDtoList.isEmpty()) {
      return null;
    }

    return stepInfoDtoList
        .stream().map(TestResultsMapper::toStepInfo).toArray(StepInfo[]::new);

  }

  public static List<TestResults> toTestResultsList(List<CommandCloseResponseDto> responseDtoList) {
    if (responseDtoList == null || responseDtoList.isEmpty()) {
      return null;
    }

    return responseDtoList.stream().map(TestResultsMapper::toTestResults).collect(Collectors.toList());
  }
}
