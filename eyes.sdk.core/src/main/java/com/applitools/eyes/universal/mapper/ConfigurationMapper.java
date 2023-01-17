package com.applitools.eyes.universal.mapper;

import com.applitools.eyes.CutProvider;
import com.applitools.eyes.FixedCutProvider;
import com.applitools.eyes.config.Configuration;
import com.applitools.eyes.config.ContentInset;
import com.applitools.eyes.universal.dto.*;


/**
 * configuration mapper
 */
public class ConfigurationMapper {

  public static ConfigurationDto toConfigurationDto(Configuration config, Boolean dontCloseBatches) {
    if (config == null) {
      return null;
    }
    ConfigurationDto dto = new ConfigurationDto();

    dto.setOpen(new OpenSettingsDto());
    dto.setScreenshot(new CheckSettingsDto());
    dto.setCheck(new CheckSettingsDto());
    dto.setClose(new CloseSettingsDto());

    // EyesBaseConfig
    dto.setDebugScreenshots(toDebugScreenshots(config.getSaveDebugScreenshots(),
            config.getDebugScreenshotsPath(), config.getDebugScreenshotsPrefix()));
    dto.setAgentId(config.getAgentId());
    dto.setApiKey(config.getApiKey());
    dto.setServerUrl(config.getServerUrl() == null ? null : config.getServerUrl().toString());
    dto.setProxy(ProxyMapper.toProxyDto(config.getProxy()));
    dto.setAutProxy(ProxyMapper.toAutProxyDto(config.getAutProxy()));
//    dto.setDisabled(config.getIsDisabled()); //removed
    dto.setConnectionTimeout(null);
    dto.setRemoveSession(null);
//    dto.setRemoteEvents(null);

    // EyesOpenConfig
    dto.setAppName(config.getAppName());
    dto.setTestName(config.getTestName());
    dto.setDisplayName(null);
    dto.setViewportSize(ViewportSizeMapper.toViewportSizeDto(config.getViewportSize()));
    dto.setSessionType(config.getSessionType() == null ? null : config.getSessionType().name());
    dto.setProperties(CustomPropertyMapper.toCustomPropertyDtoList(config.getProperties()));
    dto.setBatch(BatchMapper.toBatchDto(config.getBatch()));
    dto.setDefaultMatchSettings(MatchSettingsMapper.toMatchSettingsDto(config.getDefaultMatchSettings()));
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
    dto.setDontCloseBatches(dontCloseBatches);

    // EyesCheckConfig
    dto.setSendDom(config.isSendDom());
    dto.setMatchTimeout(config.getMatchTimeout());
    dto.setForceFullPageScreenshot(config.getForceFullPageScreenshot());


    // EyesClassicConfig<TElement, TSelector>
//    dto.setWaitBeforeScreenshots(config.getWaitBeforeScreenshots());
    dto.setStitchMode(config.getStitchMode() == null ? null : config.getStitchMode().getName());
    dto.setHideScrollBars(config.getHideScrollbars());
    dto.setHideCaret(config.getHideCaret());
    ImageCropRectDto overlap = new ImageCropRectDto();
    overlap.setBottom(config.getStitchOverlap());
    dto.setStitchOverlap(config.getStitchOverlap() == null ? null : overlap);
    dto.setScrollRootElement(null);
    dto.setCut(toImageCropRect(config.getCutProvider(), config.getContentInset()));

    dto.setRotation(config.getRotation());
    dto.setScaleRatio(config.getScaleRatio());
    dto.setWaitBeforeCapture(config.getWaitBeforeCapture());

    // EyesUFGConfig
//    dto.setConcurrentSessions(null);
    dto.setBrowsersInfo(RenderBrowserInfoMapper.toRenderBrowserInfoDtoList(config.getBrowsersInfo()));
    dto.setVisualGridOptions(VisualGridOptionMapper.toVisualGridOptionDtoList(config.getVisualGridOptions()));
    Object layoutBreakpoints = null;
    if (config.getLayoutBreakpoints().isEmpty()) {
      if (config.isDefaultLayoutBreakpointsSet() != null) {
        layoutBreakpoints = config.isDefaultLayoutBreakpointsSet();
      }
    } else {
      layoutBreakpoints = config.getLayoutBreakpoints();
    }

    dto.setLayoutBreakpoints(layoutBreakpoints);
    dto.setDisableBrowserFetching(config.isDisableBrowserFetching());
//    dto.setUseCeilForViewportSize(config.getUseCeilForViewportSize());

    return dto;
  }

  private static DebugScreenshotHandlerDto toDebugScreenshots(Boolean saveDebugScreenshots, String debugScreenshotsPath, String debugScreenshotsPrefix) {
    if (saveDebugScreenshots == null && debugScreenshotsPath == null && debugScreenshotsPrefix == null) {
      return null;
    }

    return new DebugScreenshotHandlerDto(saveDebugScreenshots, debugScreenshotsPath,debugScreenshotsPrefix);
  }

  private static ImageCropRectDto toImageCropRect(CutProvider cutProvider, ContentInset contentInset) {
    ImageCropRectDto imageCropRectDto = null;

    if (cutProvider != null) {
      FixedCutProvider fixed = (FixedCutProvider) cutProvider;
      imageCropRectDto = new ImageCropRectDto(fixed.getHeader(), fixed.getRight(), fixed.getFooter(), fixed.getLeft());
    } else if (contentInset != null) {
      imageCropRectDto =  new ImageCropRectDto(contentInset.getTop(), contentInset.getRight(), contentInset.getBottom(), contentInset.getLeft());
    }

    return imageCropRectDto;
  }
}
