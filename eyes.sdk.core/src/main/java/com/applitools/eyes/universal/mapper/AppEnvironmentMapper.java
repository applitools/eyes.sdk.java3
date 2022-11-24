package com.applitools.eyes.universal.mapper;

import com.applitools.eyes.RectangleSize;
import com.applitools.eyes.universal.dto.AppEnvironmentDto;

/**
 * Environment mapper.
 */
public class AppEnvironmentMapper {

    public static AppEnvironmentDto toAppEnvironmentMapper(String os, String hostingApp, RectangleSize viewportSize,
                                                           String deviceName, String osInfo, String hostingAppInfo) {
        AppEnvironmentDto environmentDto = new AppEnvironmentDto();

        environmentDto.setOs(os);
        environmentDto.setOsInfo(osInfo);
        environmentDto.setHostingApp(hostingApp);
        environmentDto.setHostingAppInfo(hostingAppInfo);
        environmentDto.setDeviceName(deviceName);
        environmentDto.setViewportSize(ViewportSizeMapper.toViewportSizeDto(viewportSize));

        return environmentDto;
    }
}
