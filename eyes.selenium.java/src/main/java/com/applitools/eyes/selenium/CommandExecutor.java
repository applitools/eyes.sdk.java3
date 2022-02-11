package com.applitools.eyes.selenium;

import java.util.List;
import java.util.Map;
import java.util.UUID;


import com.applitools.eyes.EyesException;
import com.applitools.eyes.Region;
import com.applitools.eyes.locators.TextRegion;
import com.applitools.eyes.selenium.universal.dto.CheckEyes;
import com.applitools.eyes.selenium.universal.dto.CheckSettingsDto;
import com.applitools.eyes.selenium.universal.dto.Command;
import com.applitools.eyes.selenium.universal.dto.ConfigurationDto;
import com.applitools.eyes.selenium.universal.dto.DriverDto;
import com.applitools.eyes.selenium.universal.dto.EventDto;
import com.applitools.eyes.selenium.universal.dto.ExtractTextDto;
import com.applitools.eyes.selenium.universal.dto.ExtractTextRegionsDto;
import com.applitools.eyes.selenium.universal.dto.LocateDto;
import com.applitools.eyes.selenium.universal.dto.MakeManager;
import com.applitools.eyes.selenium.universal.dto.MakeSdk;
import com.applitools.eyes.selenium.universal.dto.MatchResultDto;
import com.applitools.eyes.selenium.universal.dto.OCRExtractSettingsDto;
import com.applitools.eyes.selenium.universal.dto.OCRSearchSettingsDto;
import com.applitools.eyes.selenium.universal.dto.OpenEyes;
import com.applitools.eyes.selenium.universal.dto.RectangleSizeDto;
import com.applitools.eyes.selenium.universal.dto.RequestDto;
import com.applitools.eyes.selenium.universal.dto.ResponseDto;
import com.applitools.eyes.selenium.universal.dto.VisualLocatorSettingsDto;
import com.applitools.eyes.selenium.universal.dto.request.CommandCloseAllEyesRequestDto;
import com.applitools.eyes.selenium.universal.dto.request.CommandCloseRequestDto;
import com.applitools.eyes.selenium.universal.dto.request.CommandGetViewportSizeRequestDto;
import com.applitools.eyes.selenium.universal.dto.response.CommandCloseResponseDto;
import com.applitools.utils.GeneralUtils;

/**
 * command executor
 */
public class CommandExecutor {

  private static USDKConnection connection;

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
    ResponseDto<Reference> response = (ResponseDto<Reference>) checkedCommand(request, true);
    System.out.println("RESP_MAKE_MANAGER: " + response);
    if (response != null && response.getPayload().getError() != null) {
      throw new EyesException(response.getPayload().getError().getMessage());
    }
    return response.getPayload().getResult();
  }

  public Reference managerOpenEyes(Reference ref, DriverDto driverDto, ConfigurationDto config) {
    RequestDto<OpenEyes> request = new RequestDto<>();
    request.setName("EyesManager.openEyes");
    request.setKey(UUID.randomUUID().toString());
    request.setPayload(new OpenEyes(ref, driverDto, config));
    ResponseDto<Reference> response = (ResponseDto<Reference>) checkedCommand(request, true);
    System.out.println("RESP_OPEN: " + response);
    if (response != null && response.getPayload().getError() != null) {
      throw new EyesException(response.getPayload().getError().getMessage());
    }
    return response.getPayload().getResult();
  }

  public void eyesCheck(Reference eyesRef, CheckSettingsDto settings, ConfigurationDto config) {
    RequestDto<CheckEyes> request = new RequestDto<>();
    request.setName("Eyes.check");
    request.setKey(UUID.randomUUID().toString());
    request.setPayload(new CheckEyes(eyesRef, settings, config));
    ResponseDto<MatchResultDto> responseDto = (ResponseDto<MatchResultDto>) checkedCommand(request, true);
    System.out.println("RESP_CHECK: " + responseDto);
    if (responseDto != null && responseDto.getPayload().getError() != null) {
      throw new EyesException(responseDto.getPayload().getError().getMessage());
    }
  }

  public Map<String, List<Region>> locate(Reference eyesRef, VisualLocatorSettingsDto locatorSettingsDto, ConfigurationDto config) {
    RequestDto<LocateDto> request = new RequestDto<>();
    request.setName("Eyes.locate");
    request.setKey(UUID.randomUUID().toString());
    request.setPayload(new LocateDto(eyesRef, locatorSettingsDto, config));
    ResponseDto<Map<String, List<Region>>> locateResponse = (ResponseDto<Map<String, List<Region>>>)checkedCommand(request, true);
    System.out.println("RESP_LOCATE: " + locateResponse);
    if (locateResponse != null && locateResponse.getPayload().getError() != null) {
      throw new EyesException(locateResponse.getPayload().getError().getMessage());
    }
    return locateResponse.getPayload().getResult();
  }

  public Map<String, List<TextRegion>> extractTextRegions(Reference eyesRef, OCRSearchSettingsDto searchSettingsDto, ConfigurationDto config) {
    RequestDto<ExtractTextRegionsDto> request = new RequestDto<>();
    request.setName("Eyes.extractTextRegions");
    request.setKey(UUID.randomUUID().toString());
    request.setPayload(new ExtractTextRegionsDto(eyesRef, searchSettingsDto, config));
    ResponseDto<Map<String, List<TextRegion>>> extractTextRegionsResponse =
        (ResponseDto<Map<String, List<TextRegion>>>) checkedCommand(request, true);
    System.out.println("RESP_EXTRACT_TEXT_REGIONS: " + extractTextRegionsResponse);
    if (extractTextRegionsResponse != null && extractTextRegionsResponse.getPayload().getError() != null) {
      throw new EyesException(extractTextRegionsResponse.getPayload().getError().getMessage());
    }
    return extractTextRegionsResponse.getPayload().getResult();

  }

  public List<String> extractText(Reference eyesRef, List<OCRExtractSettingsDto> extractSettingsDtoList, ConfigurationDto config) {
    RequestDto<ExtractTextDto> request = new RequestDto<>();
    request.setName("Eyes.extractText");
    request.setKey(UUID.randomUUID().toString());
    request.setPayload(new ExtractTextDto(eyesRef, extractSettingsDtoList, config));
    ResponseDto<List<String>> responseDto = (ResponseDto<List<String>>) checkedCommand(request, true);
    System.out.println("RESP_EXTRACT_TEXT: " + responseDto);
    if (responseDto != null && responseDto.getPayload().getError() != null) {
      throw new EyesException(responseDto.getPayload().getError().getMessage());
    }
    return responseDto.getPayload().getResult();
  }

  public List<CommandCloseResponseDto> close(Reference eyesRef, boolean waitResult) {
    RequestDto<CommandCloseRequestDto> request = new RequestDto<>();
    request.setName("Eyes.close");
    request.setKey(UUID.randomUUID().toString());
    request.setPayload(new CommandCloseRequestDto(eyesRef));
    ResponseDto<List<CommandCloseResponseDto>> closeResponse = (ResponseDto<List<CommandCloseResponseDto>>) checkedCommand(request, waitResult);
    System.out.println("RESP_CLOSE: " + closeResponse);
    if (closeResponse != null && closeResponse.getPayload().getError() != null) {
      throw new EyesException(closeResponse.getPayload().getError().getMessage());
    }
    return closeResponse.getPayload().getResult();
  }

  public List<CommandCloseResponseDto> abort(Reference eyesRef, boolean waitResult) {
    RequestDto<CommandCloseRequestDto> request = new RequestDto<>();
    request.setName("Eyes.abort");
    request.setKey(UUID.randomUUID().toString());
    request.setPayload(new CommandCloseRequestDto(eyesRef));
    ResponseDto<List<CommandCloseResponseDto>> closeResponse = (ResponseDto<List<CommandCloseResponseDto>>) checkedCommand(request, waitResult);
    System.out.println("RESP_ABORT: " + closeResponse);
    if (closeResponse != null && closeResponse.getPayload().getError() != null) {
      throw new EyesException(closeResponse.getPayload().getError().getMessage());
    }
    return closeResponse.getPayload().getResult();
  }

  public static RectangleSizeDto getViewportSize(DriverDto driver) {
    RequestDto<CommandGetViewportSizeRequestDto> request = new RequestDto<>();
    request.setName("Core.getViewportSize");
    request.setKey(UUID.randomUUID().toString());
    request.setPayload(new CommandGetViewportSizeRequestDto(driver));
    ResponseDto<RectangleSizeDto> getViewportSizeResponse = (ResponseDto<RectangleSizeDto>) checkedCommand(request, true);
    return getViewportSizeResponse.getPayload().getResult();
  }

  public List<CommandCloseResponseDto> closeAllEyes(Reference managerRef) {
    RequestDto<CommandCloseAllEyesRequestDto> request = new RequestDto<>();
    request.setName("EyesManager.closeAllEyes");
    request.setKey(UUID.randomUUID().toString());
    request.setPayload(new CommandCloseAllEyesRequestDto(managerRef));
    ResponseDto<List<CommandCloseResponseDto>> closeResponse = (ResponseDto<List<CommandCloseResponseDto>>) checkedCommand(request, true);
    System.out.println("RESP_CLOSE_ALL_EYES: " + closeResponse);
    if (closeResponse != null && closeResponse.getPayload().getError() != null) {
      throw new EyesException(closeResponse.getPayload().getError().getMessage());
    }
    return closeResponse.getPayload().getResult();
  }


  public static ResponseDto<?> checkedCommand(Command command, boolean waitResult) {
    try {
      return connection.executeCommand(command, waitResult);
      // handle an error here
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }

}
