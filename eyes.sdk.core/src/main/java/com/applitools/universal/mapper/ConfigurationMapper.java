package com.applitools.universal.mapper;

import java.util.Arrays;
import java.util.stream.Collectors;

import com.applitools.eyes.config.Configuration;
import com.applitools.universal.dto.ConfigurationDto;

/**
 * configuration mapper
 */
public class ConfigurationMapper {

  public static ConfigurationDto toConfigurationDto(Configuration configuration) {
    if (configuration == null) {
      return null;
    }

    ConfigurationDto configurationDto = new ConfigurationDto();
    configurationDto.setBranchName(configuration.getBranchName());
    configurationDto.setParentBranchName(configuration.getParentBranchName());
    configurationDto.setBaselineBranchName(configuration.getBaselineBranchName());
    configurationDto.setAgentId(configuration.getAgentId());
    configurationDto.setEnvironmentName(configuration.getEnvironmentName());
    configurationDto.setSaveDiffs(configuration.getSaveDiffs());
    configurationDto.setSessionType(configuration.getSessionType() == null ? null : configuration.getSessionType().name());
    configurationDto.setBatch(BatchMapper.toBatchDto(configuration.getBatch()));
    configurationDto.setBaselineEnvName(configuration.getBaselineEnvName());
    configurationDto.setAppName(configuration.getAppName());
    configurationDto.setTestName(configuration.getTestName());
    configurationDto.setViewportSize(ViewportSizeMapper.toViewportSizeDto(configuration.getViewportSize()));
    configurationDto.setIgnoreDisplacements(configuration.getIgnoreDisplacements());
    configurationDto.setMatchTimeout(configuration.getMatchTimeout());
    configurationDto.setHostApp(configuration.getHostApp());
    configurationDto.setHostOS(configuration.getHostOS());
    configurationDto.setDeviceInfo(configuration.getDeviceInfo());
    configurationDto.setHostingAppInfo(configuration.getHostingAppInfo());
    configurationDto.setOsInfo(configuration.getOsInfo());

    configurationDto.setSaveNewTests(configuration.getSaveNewTests());
    configurationDto.setSaveFailedTests(configuration.getSaveFailedTests());
    configurationDto.setStitchOverlap(configuration.getStitchOverlap());
    configurationDto.setSendDom(configuration.isSendDom());
    configurationDto.setApiKey(configuration.getApiKey());
    //configurationDto.setServerUrl(configuration.getServerUrl().toString());
    configurationDto.setProxy(AbstractProxySettingsMapper.toAbstractProxySettingsDto(configuration.getProxy()));
    configurationDto.setFailureReports(configuration.getFailureReports() == null ? null : configuration.getFailureReports().getName());
    configurationDto.setAccessibilitySettings(AccessibilitySettingsMapper.toAccessibilitySettingsDto(configuration.getAccessibilityValidation()));
    configurationDto.setEnablePatterns(configuration.getEnablePatterns());
    configurationDto.setUseDom(configuration.getUseDom());
    configurationDto.setAbortIdleTestTimeout(configuration.getAbortIdleTestTimeout());
    configurationDto.setForceFullPageScreenshot(configuration.getForceFullPageScreenshot());
    configurationDto.setWaitBeforeScreenshots(configuration.getWaitBeforeScreenshots());
    configurationDto.setStitchMode(configuration.getStitchMode() == null ? null : configuration.getStitchMode().getName());
    configurationDto.setHideScrollbars(configuration.getHideScrollbars());
    configurationDto.setHideCaret(configuration.getHideCaret());
    configurationDto.setVisualGrid(configuration.isVisualGrid());
    configurationDto.setDisableBrowserFetching(configuration.isDisableBrowserFetching());
    configurationDto.setUseCookies(configuration.isUseCookies());
    configurationDto.setCaptureStatusBar(configuration.isCaptureStatusBar());
    configurationDto.setRenderingConfig(configuration.isRenderingConfig());
    configurationDto.setBrowsersInfo(RenderBrowserInfoMapper.toRenderBrowserInfoDtoList(configuration.getBrowsersInfo()));
    configurationDto
        .setFeatures(configuration.getFeatures().stream().map(Enum::name).collect(Collectors.toSet()));
    configurationDto.setVisualGridOptions(VisualGridOptionMapper.toVisualGridOptionDtoList(configuration.getVisualGridOptions()));
    configurationDto.setDefaultLayoutBreakpointsSet(configuration.isDefaultLayoutBreakpointsSet());
    configurationDto.setLayoutBreakpoints(configuration.getLayoutBreakpoints());

    return configurationDto;
  }
}
