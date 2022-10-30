package com.applitools.eyes.selenium.universal.mapper;

import java.lang.reflect.Field;

import com.applitools.eyes.AbstractProxySettings;
import com.applitools.eyes.EyesException;
import com.applitools.eyes.selenium.universal.dto.DriverDto;
import com.applitools.eyes.selenium.universal.dto.DriverTargetDto;
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
  private static final String CLIENT_CONFIG = "clientConfig";
  private static final String BASE_URI = "baseUri";

  public static DriverDto toDriverDto(WebDriver driver, String proxyUrl) {
    if (driver == null) {
      return null;
    }

    RemoteWebDriver remoteDriver = (RemoteWebDriver) driver;

    DriverDto driverDto = new DriverDto();
    driverDto.setSessionId(remoteDriver.getSessionId().toString());
    driverDto.setServerUrl(getRemoteServerUrl(remoteDriver));
    driverDto.setCapabilities(remoteDriver.getCapabilities().asMap());
    driverDto.setProxyUrl(proxyUrl);

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
          Class<?> remoteDriverClass = webDriver.getClass();
          while (remoteDriverClass != RemoteWebDriver.class) {
              remoteDriverClass = remoteDriverClass.getSuperclass();
          }

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
          e1.printStackTrace();
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
      if (remoteHostObject != null) {
        return remoteHostObject.toString();
      }

      final Field clientConfig = remoteWebDriverBuilderClass.getDeclaredField(CLIENT_CONFIG);
      clientConfig.setAccessible(true);
      Object clientConfigObject = clientConfig.get(remoteWebDriverBuilder);
      final Class<?> clientConfigClass = clientConfigObject.getClass();
      final Field baseUri = clientConfigClass.getDeclaredField(BASE_URI);
      baseUri.setAccessible(true);
      Object baseUriObject = baseUri.get(clientConfigObject);
      if (baseUriObject != null) {
          return baseUriObject.toString();
      }
      throw  new Exception("Cannot extract remote url from webdriver");
  }

    public static DriverTargetDto toDriverTargetDto(WebDriver driver, AbstractProxySettings proxySettings) {
        if (driver == null) {
            return null;
        }

        RemoteWebDriver remoteDriver = (RemoteWebDriver) driver;

        DriverTargetDto driverTargetDto = new DriverTargetDto();
        driverTargetDto.setSessionId(remoteDriver.getSessionId().toString());
        driverTargetDto.setServerUrl(getRemoteServerUrl(remoteDriver));
        driverTargetDto.setCapabilities(remoteDriver.getCapabilities().asMap());
        driverTargetDto.setProxy(proxySettings);

        return driverTargetDto;
    }
}
