package com.applitools.eyes.selenium.universal.mapper;

import java.lang.reflect.Field;

import com.applitools.eyes.selenium.universal.dto.DriverDto;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.HttpCommandExecutor;
import org.openqa.selenium.remote.RemoteWebDriver;

/**
 * driver mapper
 */
public class DriverMapper {
  private static final String EXECUTOR = "executor";
  private static final String DELEGATE = "delegate";
  private static final String REMOTE_SERVER = "remoteServer";

  public static DriverDto toDriverDto(WebDriver driver) {
    if (driver == null) {
      return null;
    }

    RemoteWebDriver remoteDriver = (RemoteWebDriver) driver;

    DriverDto driverDto = new DriverDto();
    driverDto.setSessionId(remoteDriver.getSessionId().toString());
    driverDto.setServerUrl(getRemoteServerUrl(remoteDriver));
    driverDto.setCapabilities(remoteDriver.getCapabilities().asMap());

    return driverDto;

  }


  private static String getRemoteServerUrl(RemoteWebDriver remoteWebDriver) {
    String url = null;
    if (remoteWebDriver.getCommandExecutor() instanceof HttpCommandExecutor) {
      url = ((HttpCommandExecutor) (remoteWebDriver.getCommandExecutor())).getAddressOfRemoteServer().toString();
    } else {
      try {
        final Class<?> remoteDriverClass = remoteWebDriver.getClass();
        final Field executorField = remoteDriverClass.getDeclaredField(EXECUTOR);
        executorField.setAccessible(true);
        Object tracedCommandExecutor = executorField.get(remoteWebDriver); // TracedCommandExecutor

        final Class<?> tracedCommandExecutorClass = tracedCommandExecutor.getClass();
        final Field delegateField = tracedCommandExecutorClass.getDeclaredField(DELEGATE); // HttpCommandExecutor
        delegateField.setAccessible(true);
        Object httpCommandExecutor = delegateField.get(tracedCommandExecutor);

        final Class<?> httpCommandExecutorClass = httpCommandExecutor.getClass();
        final Field remoteServerField = httpCommandExecutorClass.getDeclaredField(REMOTE_SERVER);
        remoteServerField.setAccessible(true);
        Object remoteServer = remoteServerField.get(httpCommandExecutor);
        url = remoteServer.toString();
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
    return url;
  }


  private static String getRemoteServerUrl(RemoteWebDriver remoteWebDriver) {
    String url = null;
    if (remoteWebDriver.getCommandExecutor() instanceof HttpCommandExecutor) {
      url = ((HttpCommandExecutor) (remoteWebDriver.getCommandExecutor())).getAddressOfRemoteServer().toString();
    } else {
      try {
        final Class<?> remoteDriverClass = remoteWebDriver.getClass();
        final Field executorField = remoteDriverClass.getDeclaredField(EXECUTOR);
        executorField.setAccessible(true);
        Object tracedCommandExecutor = executorField.get(remoteWebDriver); // TracedCommandExecutor

        final Class<?> tracedCommandExecutorClass = tracedCommandExecutor.getClass();
        final Field delegateField = tracedCommandExecutorClass.getDeclaredField(DELEGATE); // HttpCommandExecutor
        delegateField.setAccessible(true);
        Object httpCommandExecutor = delegateField.get(tracedCommandExecutor);

        final Class<?> httpCommandExecutorClass = httpCommandExecutor.getClass();
        final Field remoteServerField = httpCommandExecutorClass.getDeclaredField(REMOTE_SERVER);
        remoteServerField.setAccessible(true);
        Object remoteServer = remoteServerField.get(httpCommandExecutor);
        url = remoteServer.toString();
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
    return url;
  }

}
