package com.applitools.eyes.selenium.universal.mapper;

import java.util.List;
import java.util.stream.Collectors;

import com.applitools.eyes.SessionUrls;
import com.applitools.eyes.StepInfo;
import com.applitools.eyes.TestResults;
import com.applitools.eyes.TestResultsStatus;
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
    testResults.setStatus(TestResultsStatus.valueOf(response.getStatus()));
    testResults.setAppName(response.getAppName());
    testResults.setBatchName(response.getBatchName());
    testResults.setBatchId(response.getBatchId());
    testResults.setBranchName(response.getBranchName());
    testResults.setHostOS(response.getHostOS());
    testResults.setHostApp(response.getHostApp());
    testResults.setHostDisplaySize(RectangleSizeMapper.toRectangleSize(response.getHostDisplaySize()));
    testResults.setStartedAt(null);
    testResults.setDuration(response.getDuration());
    testResults.setNew(response.getNew());
    testResults.setDifferent(response.getDifferent());
    testResults.setAborted(response.getAborted());
    SessionUrls appUrls = new SessionUrls();
    appUrls.setSession(response.getAppUrls().getSession());
    appUrls.setBatch(response.getAppUrls().getBatch());
    testResults.setAppUrls(appUrls);
    SessionUrls apiUrls = new SessionUrls();
    apiUrls.setSession(response.getApiUrls().getSession());
    apiUrls.setBatch(response.getApiUrls().getBatch());
    testResults.setApiUrls(apiUrls);
    testResults.setStepsInfo(toStepInfoArray(response.getStepsInfo()));
    testResults.setSteps(response.getSteps());
    testResults.setMatches(response.getMatches());
    testResults.setMismatches(response.getMismatches());
    testResults.setMissing(response.getMissing());
    testResults.setExactMatches(response.getExactMatches());
    testResults.setStrictMatches(response.getStrictMatches());
    testResults.setContentMatches(response.getContentMatches());
    testResults.setLayoutMatches(response.getLayoutMatches());
    testResults.setNoneMatches(response.getNoneMatches());
    testResults.setUrl(response.getUrl());
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

  private static StepInfo[] toStepInfoArray(List<StepInfoDto> stepInfoDtoList) {
    if (stepInfoDtoList == null || stepInfoDtoList.isEmpty()) {
      return null;
    }

    return stepInfoDtoList
        .stream().map(TestResultsMapper::toStepInfo).toArray(StepInfo[]::new);

  }
}
