package com.applitools.universal.mapper;

import java.util.List;
import java.util.stream.Collectors;

import com.applitools.eyes.visualgrid.model.RenderBrowserInfo;
import com.applitools.universal.dto.RenderBrowserInfoDto;

/**
 * Render browser info mapper
 */
public class RenderBrowserInfoMapper {

  public static RenderBrowserInfoDto toRenderBrowserInfoDto(RenderBrowserInfo renderBrowserInfo) {
    if (renderBrowserInfo == null) {
      return null;
    }

    RenderBrowserInfoDto renderBrowserInfoDto = new RenderBrowserInfoDto();
    renderBrowserInfoDto.setViewportSize(ViewportSizeMapper.toViewportSizeDto(renderBrowserInfo.getDeviceSize()));
    renderBrowserInfoDto.setBrowserType(renderBrowserInfo.getBrowserType().getName());
    renderBrowserInfoDto.setPlatform(renderBrowserInfo.getPlatform());
    renderBrowserInfoDto.setEmulationInfo(EmulationBaseInfoMapper.toEmulationBaseInfoDto(renderBrowserInfo.getEmulationInfo()));
    renderBrowserInfoDto.setIosDeviceInfo(IosDeviceInfoMapper.toIosDeviceInfoDto(renderBrowserInfo.getIosDeviceInfo()));
    renderBrowserInfoDto.setSizeMode(renderBrowserInfo.getSizeMode());
    renderBrowserInfoDto.setBaselineEnvName(renderBrowserInfo.getBaselineEnvName());

    return renderBrowserInfoDto;

  }

  public static List<RenderBrowserInfoDto> toRenderBrowserInfoDtoList(List<RenderBrowserInfo> renderBrowserInfos) {
    if (renderBrowserInfos == null || renderBrowserInfos.isEmpty()) {
      return null;
    }

    return renderBrowserInfos.stream().map(RenderBrowserInfoMapper::toRenderBrowserInfoDto).collect(Collectors.toList());

  }
}
