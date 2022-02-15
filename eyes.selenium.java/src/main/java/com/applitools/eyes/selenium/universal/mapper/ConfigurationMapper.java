package com.applitools.eyes.selenium.universal.mapper;

import com.applitools.eyes.FixedCutProvider;
import com.applitools.eyes.config.Configuration;
import com.applitools.eyes.selenium.universal.dto.ConfigurationDto;
import com.applitools.eyes.selenium.universal.dto.DebugScreenshotHandlerDto;
import com.applitools.eyes.selenium.universal.dto.ImageCropRectDto;
import com.applitools.eyes.selenium.universal.dto.ImageCropRegionDto;


/**
 * configuration mapper
 */
public class ConfigurationMapper {

  public static ConfigurationDto toConfigurationDto(Configuration config) {
    if (config == null) {
      return null;
    }
    ConfigurationDto dto = new ConfigurationDto();

    // EyesBaseConfig
    dto.setLogs(null);
    dto.setDebugScreenshots(new DebugScreenshotHandlerDto(config.getSaveDebugScreenshots(),
        config.getDebugScreenshotsPath(), config.getDebugScreenshotsPrefix()));
    dto.setAgentId(config.getAgentId());
    dto.setApiKey(config.getApiKey());
    dto.setServerUrl(config.getServerUrl() == null ? null : config.getServerUrl().toString());
    dto.setProxy(ProxyMapper.toProxyDto(config.getProxy()));
    dto.setDisabled(config.getIsDisabled());
    dto.setConnectionTimeout(null);
    dto.setRemoveSession(null);
    dto.setRemoteEvents(null);

    // EyesOpenConfig
    dto.setAppName(config.getAppName());
    dto.setTestName(config.getTestName());
    dto.setDisplayName(null);
    dto.setViewportSize(ViewportSizeMapper.toViewportSizeDto(config.getViewportSize()));
    dto.setSessionType(config.getSessionType() == null ? null : config.getSessionType().name());
    dto.setProperties(CustomPropertyMapper.toCustomPropertyDtoList(config.getProperties()));
    dto.setBatch(BatchMapper.toBatchDto(config.getBatch()));
    dto.setDefaultMatchSettings(MatchSettingsMapper.toMatchSettingsDto(config.getDefaultMatchSettings())); // TODO defaultMatchSettings?: MatchSettings<Region>
    dto.setHostApp(config.getHostApp());
    dto.setHostOS(config.getHostOS());
    dto.setHostAppInfo(null);
    dto.setHostOSInfo(null);
    dto.setDeviceInfo(null);
    dto.setBaselineEnvName(config.getBaselineEnvName());
    dto.setEnvironmentName(config.getEnvironmentName());
    dto.setBranchName(config.getBranchName());
    dto.setParentBranchName(config.getParentBranchName());
    dto.setBaselineBranchName(config.getBaselineBranchName());
    dto.setCompareWithParentBranch(null);
    dto.setIgnoreBaseline(null);
    dto.setSaveFailedTests(config.getSaveFailedTests());
    dto.setSaveNewTests(config.getSaveNewTests());
    dto.setSaveDiffs(config.getSaveDiffs());
    dto.setDontCloseBatches(null);

    // EyesCheckConfig
    dto.setSendDom(config.isSendDom());
    dto.setMatchTimeout(config.getMatchTimeout());
    dto.setForceFullPageScreenshot(config.getForceFullPageScreenshot());


    // EyesClassicConfig<TElement, TSelector>
    dto.setWaitBeforeScreenshots(config.getWaitBeforeScreenshots());
    dto.setStitchMode(config.getStitchMode() == null ? null : config.getStitchMode().getName()); // 'CSS' | 'Scroll'
    dto.setHideScrollbars(config.getHideScrollbars());
    dto.setHideCaret(config.getHideCaret());
    dto.setStitchOverlap(config.getStitchOverlap());
    dto.setScrollRootElement(null);
    FixedCutProvider cutProvider = (FixedCutProvider) config.getCutProvider();
    dto.setCut(cutProvider == null ? null : new ImageCropRectDto(cutProvider.getHeader(), cutProvider.getRight(),
        cutProvider.getFooter(), cutProvider.getLeft()));
    dto.setRotation(config.getRotation());
    dto.setScaleRatio(config.getScaleRatio());

    // EyesUFGConfig
    dto.setConcurrentSessions(null);
    dto.setBrowsersInfo(RenderBrowserInfoMapper.toRenderBrowserInfoDtoList(config.getBrowsersInfo()));
    dto.setVisualGridOptions(VisualGridOptionMapper.toVisualGridOptionDtoList(config.getVisualGridOptions()));
    Object layoutBreakpoints = config.isDefaultLayoutBreakpointsSet() != null ? config.isDefaultLayoutBreakpointsSet() : config.getLayoutBreakpoints().isEmpty() ? null : config.getLayoutBreakpoints();
    dto.setLayoutBreakpoints(layoutBreakpoints);
    dto.setDisableBrowserFetching(config.isDisableBrowserFetching());

    return dto;
  }
}
