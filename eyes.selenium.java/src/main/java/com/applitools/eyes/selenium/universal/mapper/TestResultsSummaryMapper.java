package com.applitools.eyes.selenium.universal.mapper;

import java.util.ArrayList;
import java.util.List;

import com.applitools.eyes.TestResultContainer;
import com.applitools.eyes.TestResultsSummary;
import com.applitools.eyes.selenium.BrowserType;
import com.applitools.eyes.selenium.universal.dto.BrowserInfoDto;
import com.applitools.eyes.selenium.universal.dto.TestResultContainerDto;
import com.applitools.eyes.selenium.universal.dto.TestResultsSummaryDto;
import com.applitools.eyes.visualgrid.model.EmulationBaseInfo;
import com.applitools.eyes.visualgrid.model.IosDeviceInfo;
import com.applitools.eyes.visualgrid.model.RenderBrowserInfo;

/**
 * test results summary mapper
 */
public class TestResultsSummaryMapper {

  public static TestResultsSummary fromDto(TestResultsSummaryDto dto) {
    if (dto == null) {
      return null;
    }

    List<TestResultContainerDto> containerDtoList = dto.getResults();
    List<TestResultContainer> containerList = new ArrayList<>();

    if (containerDtoList != null && !containerDtoList.isEmpty()) {
      for (TestResultContainerDto containerDto : containerDtoList) {
        // browserInfo
        BrowserInfoDto browserInfoDto = containerDto.getBrowserInfo();
        RenderBrowserInfo renderBrowserInfo = null;
        if (browserInfoDto != null) {
          if (browserInfoDto.getChromeEmulationInfo() != null) {
            EmulationBaseInfo emulationBaseInfo = RenderBrowserInfoMapper.toEmulationInfo(browserInfoDto.getChromeEmulationInfo());
            renderBrowserInfo = new RenderBrowserInfo(emulationBaseInfo);
          } else if (browserInfoDto.getIosDeviceInfo() != null) {
            IosDeviceInfo iosDeviceInfo = RenderBrowserInfoMapper.toIosDeviceInfo(browserInfoDto.getIosDeviceInfo());
            renderBrowserInfo = new RenderBrowserInfo(iosDeviceInfo);
          } else {
            renderBrowserInfo = new RenderBrowserInfo(browserInfoDto.getWidth(), browserInfoDto.getHeight(),
                BrowserType.fromName(browserInfoDto.getName()));
          }
        }
        // exception
        Exception exception = null;
        if (containerDto.getException() != null) {
          Exception stack = new Exception(containerDto.getException().getStack());
          exception = new Exception(containerDto.getException().getMessage(), stack);
        }
        TestResultContainer container = new TestResultContainer(containerDto.getTestResults(), renderBrowserInfo, exception);
        containerList.add(container);
      }
    }

    return new TestResultsSummary(containerList, dto.getPassed(), dto.getUnresolved(),
        dto.getFailed(), dto.getExceptions(), dto.getMismatches(), dto.getMissing(), dto.getMatches());
  }


}
