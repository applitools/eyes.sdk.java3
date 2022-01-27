package com.applitools.eyes.selenium.universal.mapper;

import com.applitools.eyes.selenium.universal.dto.IosDeviceInfoDto;
import com.applitools.eyes.visualgrid.model.IosDeviceInfo;

/**
 * IosDeviceInfoMapper
 */
public class IosDeviceInfoMapper {

  public static IosDeviceInfoDto toIosDeviceInfoDto(IosDeviceInfo iosDeviceInfo) {
    if (iosDeviceInfo == null) {
      return null;
    }

    IosDeviceInfoDto iosDeviceInfoDto = new IosDeviceInfoDto();
    iosDeviceInfoDto.setDeviceName(iosDeviceInfo.getDeviceName());
    iosDeviceInfoDto.setScreenOrientation(iosDeviceInfo.getScreenOrientation().getOrientation());
    iosDeviceInfoDto.setVersion(iosDeviceInfo.getVersion().getVersion());
    iosDeviceInfoDto.setSize(ViewportSizeMapper.toViewportSizeDto(iosDeviceInfo.getSize()));
    return iosDeviceInfoDto;

  }
}
