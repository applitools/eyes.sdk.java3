package com.applitools.eyes.selenium.universal.mapper;

import com.applitools.eyes.selenium.universal.dto.EmulationBaseInfoDto;
import com.applitools.eyes.universal.mapper.ViewportSizeMapper;
import com.applitools.eyes.visualgrid.model.EmulationBaseInfo;
import com.applitools.eyes.visualgrid.model.EmulationDevice;

/**
 * Emulation base info mapper
 */
public class EmulationBaseInfoMapper {

  public static EmulationBaseInfoDto toEmulationBaseInfoDto(EmulationBaseInfo emulationBaseInfo) {
    if(emulationBaseInfo == null) {
      return null;
    }
    EmulationBaseInfoDto emulationBaseInfoDto = new EmulationBaseInfoDto();
    emulationBaseInfoDto.setScreenOrientation(emulationBaseInfo.getScreenOrientation().getOrientation());
    emulationBaseInfoDto.setSize(ViewportSizeMapper.toViewportSizeDto(emulationBaseInfo.getSize()));
    emulationBaseInfoDto.setDeviceName(emulationBaseInfo.getDeviceName());

    if (emulationBaseInfo instanceof EmulationDevice) {
      emulationBaseInfoDto.setWidth(((EmulationDevice) emulationBaseInfo).getWidth());
      emulationBaseInfoDto.setHeight(((EmulationDevice) emulationBaseInfo).getHeight());
      emulationBaseInfoDto.setDeviceScaleFactor(((EmulationDevice) emulationBaseInfo).getDeviceScaleFactor());
    }

    return emulationBaseInfoDto;

  }

}
