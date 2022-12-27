package com.applitools.eyes.playwright.universal.mapper;

import com.applitools.eyes.WebDriverProxySettings;
import com.applitools.eyes.playwright.universal.dto.DriverTargetDto;
import com.microsoft.playwright.Page;

public class DriverMapper {
    public static DriverTargetDto toDriverTargetDto(Page page, WebDriverProxySettings wdProxy) {
        if (page == null) {
            return null;
        }

        DriverTargetDto driverTargetDto = new DriverTargetDto();
//        driverTargetDto.setSessionId(remoteDriver.getSessionId().toString());
        driverTargetDto.setServerUrl(page.url());
//        driverTargetDto.setCapabilities(remoteDriver.getCapabilities().asMap());

        return driverTargetDto;
    }
}
