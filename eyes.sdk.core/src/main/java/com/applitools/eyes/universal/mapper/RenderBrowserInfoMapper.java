package com.applitools.eyes.universal.mapper;

import java.util.List;
import java.util.stream.Collectors;

import com.applitools.eyes.universal.dto.*;
import com.applitools.eyes.visualgrid.model.AndroidDeviceInfo;
import com.applitools.eyes.visualgrid.model.AndroidDeviceName;
import com.applitools.eyes.visualgrid.model.ChromeEmulationInfo;
import com.applitools.eyes.visualgrid.model.DeviceAndroidVersion;
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
    } else if (renderBrowserInfo.getAndroidDeviceInfo() != null) {
      AndroidDeviceInfo androidDeviceInfo = renderBrowserInfo.getAndroidDeviceInfo();
      AndroidDeviceRendererDto androidDeviceRenderer = new AndroidDeviceRendererDto();
      AndroidDeviceInfoDto androidDevice = new AndroidDeviceInfoDto();
      androidDevice.setDeviceName(androidDeviceInfo.getDeviceName());
      androidDevice.setScreenOrientation(androidDeviceInfo.getScreenOrientation() == null ? null :
          androidDeviceInfo.getScreenOrientation().getOrientation());
      androidDevice.setVersion(androidDeviceInfo.getVersion() == null ? null : androidDeviceInfo.getVersion().getVersion());
      androidDeviceRenderer.setAndroidDeviceInfo(androidDevice);
      return androidDeviceRenderer;
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
    DeviceName deviceName = DeviceName.fromName(dto.getDeviceName());
    ScreenOrientation screenOrientation = ScreenOrientation.fromOrientation(dto.getScreenOrientation());
    return new ChromeEmulationInfo(deviceName, screenOrientation);
  }

  public static IosDeviceInfo toIosDeviceInfo(IosDeviceInfoDto dto) {
    if (dto == null) {
      return null;
    }
    IosDeviceName iosDeviceName = IosDeviceName.fromName(dto.getDeviceName());
    ScreenOrientation screenOrientation = ScreenOrientation.fromOrientation(dto.getScreenOrientation());
    IosVersion iosVersion = IosVersion.fromVersion(dto.getVersion());
    return new IosDeviceInfo(iosDeviceName, screenOrientation, iosVersion);
  }

  public static AndroidDeviceInfo toAndroidDeviceInfo(AndroidDeviceInfoDto dto) {
    if (dto == null) {
      return null;
    }

    AndroidDeviceName androidDeviceName = AndroidDeviceName.fromName(dto.getDeviceName());
    ScreenOrientation screenOrientation = ScreenOrientation.fromOrientation(dto.getScreenOrientation());
    DeviceAndroidVersion version = DeviceAndroidVersion.fromVersion(dto.getVersion());
    return new AndroidDeviceInfo(androidDeviceName, screenOrientation, version);
  }

}
