package com.applitools.eyes.selenium.universal.mapper;

import java.util.List;
import java.util.stream.Collectors;

import com.applitools.eyes.selenium.universal.dto.ChromeEmulationDeviceRendererDto;
import com.applitools.eyes.selenium.universal.dto.ChromeEmulationInfoDto;
import com.applitools.eyes.selenium.universal.dto.DesktopBrowserRendererDto;
import com.applitools.eyes.selenium.universal.dto.IBrowsersInfo;
import com.applitools.eyes.selenium.universal.dto.IOSDeviceRendererDto;
import com.applitools.eyes.selenium.universal.dto.IosDeviceInfoDto;
import com.applitools.eyes.visualgrid.model.ChromeEmulationInfo;
import com.applitools.eyes.visualgrid.model.DeviceName;
import com.applitools.eyes.visualgrid.model.IosDeviceInfo;
import com.applitools.eyes.visualgrid.model.IosDeviceName;
import com.applitools.eyes.visualgrid.model.IosVersion;
import com.applitools.eyes.visualgrid.model.RenderBrowserInfo;
import com.applitools.eyes.visualgrid.model.ScreenOrientation;

/**
 * Render browser info mapper
 */
public class RenderBrowserInfoMapper {

  public static IBrowsersInfo toRenderBrowserInfoDto(RenderBrowserInfo renderBrowserInfo) {
    if (renderBrowserInfo == null) {
      return null;
    }

    if (renderBrowserInfo.getEmulationInfo() instanceof ChromeEmulationInfo) {
      ChromeEmulationInfo chromeEmulationInfo = (ChromeEmulationInfo) renderBrowserInfo.getEmulationInfo();
      ChromeEmulationDeviceRendererDto chromeRenderer = new ChromeEmulationDeviceRendererDto();
      ChromeEmulationInfoDto chromeEmulation = new ChromeEmulationInfoDto();
      chromeEmulation.setDeviceName(chromeEmulationInfo.getDeviceName());
      chromeEmulation.setScreenOrientation(chromeEmulationInfo.getScreenOrientation().getOrientation());
      chromeRenderer.setChromeEmulationInfo(chromeEmulation);
      return chromeRenderer;
    } else if (renderBrowserInfo.getIosDeviceInfo() != null) {
      IosDeviceInfo iosDeviceInfo = renderBrowserInfo.getIosDeviceInfo();
      IOSDeviceRendererDto iosRenderer = new IOSDeviceRendererDto();
      IosDeviceInfoDto iosDevice = new IosDeviceInfoDto();
      iosDevice.setDeviceName(iosDeviceInfo.getDeviceName());
      iosDevice.setScreenOrientation(iosDeviceInfo.getScreenOrientation() == null ? null : iosDeviceInfo.getScreenOrientation().getOrientation());
      iosDevice.setVersion(iosDeviceInfo.getVersion() == null ? null : iosDeviceInfo.getVersion().getVersion());
      iosRenderer.setIosDeviceInfo(iosDevice);
      return iosRenderer;
    }

    DesktopBrowserRendererDto desktopBrowserRendererDto = new DesktopBrowserRendererDto();
    desktopBrowserRendererDto.setName(renderBrowserInfo.getBrowserType().getName());
    desktopBrowserRendererDto.setHeight(renderBrowserInfo.getDeviceSize().getHeight());
    desktopBrowserRendererDto.setWidth(renderBrowserInfo.getDeviceSize().getWidth());

    return desktopBrowserRendererDto;

  }

  public static List<IBrowsersInfo> toRenderBrowserInfoDtoList(List<RenderBrowserInfo> renderBrowserInfos) {
    if (renderBrowserInfos == null || renderBrowserInfos.isEmpty()) {
      return null;
    }

    return renderBrowserInfos.stream().map(RenderBrowserInfoMapper::toRenderBrowserInfoDto).collect(Collectors.toList());
  }

  public static ChromeEmulationInfo toEmulationInfo(ChromeEmulationInfoDto dto) {
    if (dto == null) {
      return null;
    }
    DeviceName deviceName = DeviceName.valueOf(dto.getDeviceName());
    ScreenOrientation screenOrientation = ScreenOrientation.valueOf(dto.getScreenOrientation());
    return new ChromeEmulationInfo(deviceName, screenOrientation);
  }

  public static IosDeviceInfo toIosDeviceInfo(IosDeviceInfoDto dto) {
    if (dto == null) {
      return null;
    }
    IosDeviceName iosDeviceName = IosDeviceName.valueOf(dto.getDeviceName());
    ScreenOrientation screenOrientation = ScreenOrientation.valueOf(dto.getScreenOrientation());
    IosVersion iosVersion = IosVersion.valueOf(dto.getVersion());
    return new IosDeviceInfo(iosDeviceName, screenOrientation, iosVersion);
  }

}
