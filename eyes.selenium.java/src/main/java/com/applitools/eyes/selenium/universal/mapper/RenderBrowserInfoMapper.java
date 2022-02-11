package com.applitools.eyes.selenium.universal.mapper;

import java.util.List;
import java.util.stream.Collectors;

import com.applitools.eyes.selenium.universal.dto.DesktopBrowserRendererDto;
import com.applitools.eyes.selenium.universal.dto.IBrowsersInfo;
import com.applitools.eyes.selenium.universal.dto.RenderBrowserInfoDto;
import com.applitools.eyes.visualgrid.model.RenderBrowserInfo;

/**
 * Render browser info mapper
 */
public class RenderBrowserInfoMapper {

  public static IBrowsersInfo toRenderBrowserInfoDto(RenderBrowserInfo renderBrowserInfo) {
    if (renderBrowserInfo == null) {
      return null;
    }

    DesktopBrowserRendererDto desktopBrowserRendererDto = new DesktopBrowserRendererDto();
    desktopBrowserRendererDto.setName(renderBrowserInfo.getBrowserType().getName());
    desktopBrowserRendererDto.setHeight(renderBrowserInfo.getDeviceSize().getHeight());
    desktopBrowserRendererDto.setWidth(renderBrowserInfo.getDeviceSize().getWidth());

    //RenderBrowserInfoDto renderBrowserInfoDto = new RenderBrowserInfoDto();
    //renderBrowserInfoDto.setViewportSize(ViewportSizeMapper.toViewportSizeDto(renderBrowserInfo.getDeviceSize()));
    //renderBrowserInfoDto.setBrowserType(renderBrowserInfo.getBrowserType().getName());
//    renderBrowserInfoDto.setPlatform(renderBrowserInfo.getPlatform());
//    renderBrowserInfoDto.setEmulationInfo(EmulationBaseInfoMapper.toEmulationBaseInfoDto(renderBrowserInfo.getEmulationInfo()));
//    renderBrowserInfoDto.setIosDeviceInfo(IosDeviceInfoMapper.toIosDeviceInfoDto(renderBrowserInfo.getIosDeviceInfo()));
//    renderBrowserInfoDto.setSizeMode(renderBrowserInfo.getSizeMode());
//    renderBrowserInfoDto.setBaselineEnvName(renderBrowserInfo.getBaselineEnvName());

    return desktopBrowserRendererDto;
  }

  public static List<IBrowsersInfo> toRenderBrowserInfoDtoList(List<RenderBrowserInfo> renderBrowserInfos) {
    if (renderBrowserInfos == null || renderBrowserInfos.isEmpty()) {
      return null;
    }

    return renderBrowserInfos.stream().map(RenderBrowserInfoMapper::toRenderBrowserInfoDto).collect(Collectors.toList());

  }
}
