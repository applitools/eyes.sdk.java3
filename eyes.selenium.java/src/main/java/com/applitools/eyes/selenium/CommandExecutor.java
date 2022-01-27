package com.applitools.eyes.selenium;

import java.util.UUID;


import com.applitools.eyes.selenium.universal.dto.CheckEyes;
import com.applitools.eyes.selenium.universal.dto.CheckSettingsDto;
import com.applitools.eyes.selenium.universal.dto.Command;
import com.applitools.eyes.selenium.universal.dto.ConfigurationDto;
import com.applitools.eyes.selenium.universal.dto.DriverDto;
import com.applitools.eyes.selenium.universal.dto.EventDto;
import com.applitools.eyes.selenium.universal.dto.ExtractTextRegionsDto;
import com.applitools.eyes.selenium.universal.dto.LocateDto;
import com.applitools.eyes.selenium.universal.dto.MakeManager;
import com.applitools.eyes.selenium.universal.dto.MakeSdk;
import com.applitools.eyes.selenium.universal.dto.OCRSearchSettingsDto;
import com.applitools.eyes.selenium.universal.dto.OpenEyes;
import com.applitools.eyes.selenium.universal.dto.RequestDto;
import com.applitools.eyes.selenium.universal.dto.ResponseDto;
import com.applitools.eyes.selenium.universal.dto.VisualLocatorSettingsDto;
import com.applitools.utils.GeneralUtils;

/**
 * command executor
 */
public class CommandExecutor {

  private USDKConnection connection;

  public CommandExecutor(String name, String version) {
    connection = new USDKConnection();
    makeSdk(name, version, GeneralUtils.getPropertyString("user.dir"), "webdriver");
  }

  public void makeSdk(String name, String version, String cwd, String protocol) {
    EventDto<MakeSdk> request = new EventDto<>();
    MakeSdk makeSdk = new MakeSdk(name, version, cwd, protocol);
    request.setName("Core.makeSDK");
    request.setPayload(makeSdk);
    checkedCommand(request, false);
  }

  public Reference coreMakeManager(String type, Integer concurrency, Boolean isLegacy) {
    RequestDto<MakeManager> request = new RequestDto<>();
    request.setName("Core.makeManager");
    request.setKey(UUID.randomUUID().toString());
    request.setPayload(new MakeManager(type, concurrency, isLegacy));
    ResponseDto response = checkedCommand(request, true);
    return response.getPayload().getResult();
  }

  public Reference managerOpenEyes(Reference ref, DriverDto driverDto, ConfigurationDto config) {
    RequestDto<OpenEyes> request = new RequestDto<>();
    request.setName("EyesManager.openEyes");
    request.setKey(UUID.randomUUID().toString());
    request.setPayload(new OpenEyes(ref, driverDto, config));
    ResponseDto response = checkedCommand(request, true);
    return response.getPayload().getResult();
  }

  public void eyesCheck(Reference eyesRef, CheckSettingsDto settings, ConfigurationDto config) {
    RequestDto<CheckEyes> request = new RequestDto<>();
    request.setName("Eyes.check");
    request.setKey(UUID.randomUUID().toString());
    request.setPayload(new CheckEyes(eyesRef, settings, config));
    ResponseDto responseDto = checkedCommand(request, true);
  }

  public void locate(Reference eyesRef, VisualLocatorSettingsDto locatorSettingsDto, ConfigurationDto config) {
    RequestDto<LocateDto> request = new RequestDto<>();
    request.setName("Eyes.locate");
    request.setKey(UUID.randomUUID().toString());
    request.setPayload(new LocateDto(eyesRef, locatorSettingsDto, config));
    checkedCommand(request, true);
    // TODO handle response
  }

  public void extractTextRegions(Reference eyesRef, OCRSearchSettingsDto searchSettingsDto, ConfigurationDto config) {
    RequestDto<ExtractTextRegionsDto> request = new RequestDto<>();
    request.setName("Eyes.extractTextRegions");
    request.setKey(UUID.randomUUID().toString());
    request.setPayload(new ExtractTextRegionsDto(eyesRef, searchSettingsDto, config));
    checkedCommand(request, true);
    // TODO handle response
  }

  public ResponseDto checkedCommand(Command command, boolean waitResult) {
    return connection.executeCommand(command, waitResult);
  }

}
