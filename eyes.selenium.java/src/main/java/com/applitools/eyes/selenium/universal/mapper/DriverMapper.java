package com.applitools.eyes.selenium.universal.mapper;

import java.net.URL;

import com.applitools.eyes.selenium.universal.dto.DriverDto;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.HttpCommandExecutor;
import org.openqa.selenium.remote.RemoteWebDriver;

/**
 * driver mapper
 */
public class DriverMapper {

  public static DriverDto toDriverDto(WebDriver driver) {
    if (driver == null) {
      return null;
    }

    RemoteWebDriver remoteDriver = (RemoteWebDriver) driver;
    URL url = ((HttpCommandExecutor) (remoteDriver.getCommandExecutor())).getAddressOfRemoteServer();
    DriverDto driverDto = new DriverDto();
    driverDto.setSessionId(remoteDriver.getSessionId().toString());
    driverDto.setServerUrl(url.toString());
    driverDto.setCapabilities(remoteDriver.getCapabilities().asMap());
    return driverDto;
  }

}
