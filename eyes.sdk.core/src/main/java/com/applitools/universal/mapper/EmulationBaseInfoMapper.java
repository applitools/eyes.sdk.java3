package com.applitools.universal.mapper;

import com.applitools.eyes.visualgrid.model.EmulationBaseInfo;
import com.applitools.eyes.visualgrid.model.EmulationDevice;
import com.applitools.universal.dto.EmulationBaseInfoDto;

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
