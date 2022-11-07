package com.applitools.eyes.universal.mapper;

import java.util.ArrayList;
import java.util.List;

import com.applitools.eyes.*;
import com.applitools.eyes.exceptions.DiffsFoundException;
import com.applitools.eyes.exceptions.NewTestException;
import com.applitools.eyes.exceptions.TestFailedException;
import com.applitools.eyes.selenium.BrowserType;
import com.applitools.eyes.universal.dto.BrowserInfoDto;
import com.applitools.eyes.universal.dto.TestResultContainerDto;
import com.applitools.eyes.universal.dto.TestResultsSummaryDto;
import com.applitools.eyes.visualgrid.model.AndroidDeviceInfo;
import com.applitools.eyes.visualgrid.model.EmulationBaseInfo;
import com.applitools.eyes.visualgrid.model.IosDeviceInfo;
import com.applitools.eyes.visualgrid.model.RenderBrowserInfo;

/**
 * test results summary mapper
 */
public class TestResultsSummaryMapper {

  public static TestResultsSummary fromDto(TestResultsSummaryDto dto, boolean shouldThrowException) {
    if (dto == null) {
      return null;
    }

    List<TestResultContainerDto> containerDtoList = dto.getResults();
    List<TestResultContainer> containerList = new ArrayList<>();

    if (containerDtoList != null && !containerDtoList.isEmpty()) {
      for (TestResultContainerDto containerDto : containerDtoList) {
        // browserInfo
        BrowserInfoDto browserInfoDto = containerDto.getRenderer();
        RenderBrowserInfo renderBrowserInfo = null;
        if (browserInfoDto != null) {
          if (browserInfoDto.getChromeEmulationInfo() != null) {
            EmulationBaseInfo emulationBaseInfo = RenderBrowserInfoMapper.toEmulationInfo(browserInfoDto.getChromeEmulationInfo());
            renderBrowserInfo = new RenderBrowserInfo(emulationBaseInfo);
          } else if (browserInfoDto.getIosDeviceInfo() != null) {
            IosDeviceInfo iosDeviceInfo = RenderBrowserInfoMapper.toIosDeviceInfo(browserInfoDto.getIosDeviceInfo());
            renderBrowserInfo = new RenderBrowserInfo(iosDeviceInfo);
          } else if (browserInfoDto.getAndroidDeviceInfo() != null) {
            AndroidDeviceInfo androidDeviceInfo = RenderBrowserInfoMapper.toAndroidDeviceInfo(browserInfoDto.getAndroidDeviceInfo());
            renderBrowserInfo = new RenderBrowserInfo(androidDeviceInfo);
          } else {
            renderBrowserInfo = new RenderBrowserInfo(browserInfoDto.getWidth(), browserInfoDto.getHeight(),
                BrowserType.fromName(browserInfoDto.getName()));
          }
        }
        // exception
        Throwable throwable = null;
        if (containerDto.getError() != null ) {
          EyesError eyesError = containerDto.getError();
          throwable = getExceptionBasedOnReason(eyesError.getReason(), eyesError.getInfo() == null ?
                  null : eyesError.getInfo().getTestResult(), eyesError.getMessage(), eyesError.getStack());
          if (shouldThrowException) {
            throw new Error(throwable);
          }
        }
        TestResultContainer container = new TestResultContainer(containerDto.getResult(), renderBrowserInfo, throwable);
        containerList.add(container);
      }
    }

    return new TestResultsSummary(containerList, dto.getPassed(), dto.getUnresolved(),
        dto.getFailed(), dto.getExceptions(), dto.getMismatches(), dto.getMissing(), dto.getMatches());
  }

  private static Throwable getExceptionBasedOnReason(String reason, TestResults testResults, String message,
                                                     String stack) {
    String scenarioIdOrName;
    String appIdOrName;

    if (reason == null || reason.isEmpty() || reason.equals("internal")) {
      return new EyesException("Message: " +  message + ", Stack: " +  stack);
    }

    if (testResults == null) {
      scenarioIdOrName = "(no test results)";
      appIdOrName = "";
    } else {
      scenarioIdOrName = testResults.getName();
      appIdOrName = testResults.getAppName();
    }

    switch (reason) {
      case "test different" :
        return new DiffsFoundException(testResults, scenarioIdOrName, appIdOrName);
      case "test failed":
        return new TestFailedException(testResults, scenarioIdOrName, appIdOrName);
      case "test new":
        return new NewTestException(testResults, scenarioIdOrName, appIdOrName);
      default:
        throw new UnsupportedOperationException("Unsupported exception type: " + reason);
    }
  }

}
