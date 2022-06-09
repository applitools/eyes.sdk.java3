package com.applitools.eyes.selenium.universal.mapper;

import java.lang.reflect.Field;

import com.applitools.eyes.EyesException;
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
  private static final String ARG$1 = "arg$1";
  private static final String REMOTE_HOST = "remoteHost";

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

  private static String getRemoteServerUrl(RemoteWebDriver webDriver) {
    String url;
    if (webDriver.getCommandExecutor() instanceof HttpCommandExecutor) {
      url = ((HttpCommandExecutor) (webDriver.getCommandExecutor())).getAddressOfRemoteServer().toString();
    } else {
      try {
        url = getRemoteServerFromWebDriverBuilder(webDriver);
       return url;
      } catch (Exception e) {
        try {
          final Class<?> remoteDriverClass = webDriver.getClass();

          final Field executorField = remoteDriverClass.getDeclaredField(EXECUTOR);
          executorField.setAccessible(true);
          Object tracedCommandExecutor = executorField.get(webDriver); // TracedCommandExecutor

          final Class<?> tracedCommandExecutorClass = tracedCommandExecutor.getClass();
          final Field delegateField = tracedCommandExecutorClass.getDeclaredField(DELEGATE); // HttpCommandExecutor
          delegateField.setAccessible(true);
          Object httpCommandExecutor = delegateField.get(tracedCommandExecutor);

          final Class<?> httpCommandExecutorClass = httpCommandExecutor.getClass();
          final Field remoteServerField = httpCommandExecutorClass.getDeclaredField(REMOTE_SERVER);
          remoteServerField.setAccessible(true);
          Object remoteServer = remoteServerField.get(httpCommandExecutor);
          url = remoteServer.toString();
        } catch (Exception e1) {
          e.printStackTrace();
          throw new EyesException("Unsupported webDriver implementation", e1);
        }
      }
    }
    return url;
  }

  private static String getRemoteServerFromWebDriverBuilder(RemoteWebDriver webDriver) throws Exception {
      final Class<?> remoteWebDriverBuilderLambda = webDriver.getCommandExecutor().getClass();
      final Field arg$1 = remoteWebDriverBuilderLambda.getDeclaredField(ARG$1);
      arg$1.setAccessible(true);
      Object remoteWebDriverBuilder = arg$1.get(webDriver.getCommandExecutor());
      final Class<?> remoteWebDriverBuilderClass = remoteWebDriverBuilder.getClass();
      final Field remoteHost = remoteWebDriverBuilderClass.getDeclaredField(REMOTE_HOST);
      remoteHost.setAccessible(true);
      Object remoteHostObject = remoteHost.get(remoteWebDriverBuilder);
      return remoteHostObject.toString();
  }

}
