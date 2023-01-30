package com.applitools.eyes.universal.mapper;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

import com.applitools.eyes.*;
import com.applitools.eyes.universal.dto.SessionUrlsDto;
import com.applitools.eyes.universal.dto.StepInfoDto;
import com.applitools.eyes.universal.dto.response.CommandCloseResponseDto;

/**
 * test results mapper
 */
public class TestResultsMapper {

  public static TestResults toTestResults(CommandCloseResponseDto response, String apiKey, URI serverUrl, AbstractProxySettings proxySettings) {
    if (response == null) {
      return null;
    }

    TestResults testResults = new TestResults();

    // set apiKey, serverUrl and proxy in case the user will call .delete()
    testResults.setApiKey(apiKey);
    testResults.setServerUrl(serverUrl != null? serverUrl.toString() : null);
    testResults.setProxy(ProxyMapper.toProxyDto(proxySettings));

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
    testResults.setNew(response.isNew());
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
    testResults.setAccessibilityStatus(response.getAccessibilityStatus());
    return testResults;
  }
  

  private static StepInfo toStepInfo(StepInfoDto stepInfoDto) {
    if (stepInfoDto == null) {
      return null;
    }

    StepInfo stepInfo = new StepInfo();
    StepInfo.AppUrls appUrls = stepInfo.new AppUrls();
    appUrls.setStep(stepInfoDto.getAppUrls() == null ? null : stepInfoDto.getAppUrls().getStep());
    appUrls.setStepEditor(stepInfoDto.getAppUrls() == null ? null : stepInfoDto.getAppUrls().getStepEditor());
    stepInfo.setAppUrls(appUrls);
    StepInfo.ApiUrls apiUrls = stepInfo.new ApiUrls();
    apiUrls.setBaselineImage(stepInfoDto.getApiUrls() == null ? null : stepInfoDto.getApiUrls().getBaselineImage());
    apiUrls.setCheckpointImage(stepInfoDto.getApiUrls() == null ? null : stepInfoDto.getApiUrls().getCheckpointImage());
    apiUrls.setCheckpointImageThumbnail(stepInfoDto.getApiUrls() == null ? null : stepInfoDto.getApiUrls().getCheckpointImageThumbnail());
    apiUrls.setCurrentImage(stepInfoDto.getApiUrls() == null ? null : stepInfoDto.getApiUrls().getCurrentImage());
    apiUrls.setDiffImage(stepInfoDto.getApiUrls() == null ? null : stepInfoDto.getApiUrls().getDiffImage());
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

  public static List<TestResults> toTestResultsList(List<CommandCloseResponseDto> responseDtoList, String apiKey, URI serverUrl, AbstractProxySettings proxySettings) {
    if (responseDtoList == null || responseDtoList.isEmpty()) {
      return null;
    }

    return responseDtoList.stream()
            .map(commandCloseResponseDto -> toTestResults(commandCloseResponseDto, apiKey, serverUrl, proxySettings))
            .collect(Collectors.toList());
  }
}
