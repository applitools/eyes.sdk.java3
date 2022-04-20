package com.applitools.eyes.selenium;

import java.util.List;
import java.util.Map;
import java.util.UUID;


import com.applitools.eyes.EyesException;
import com.applitools.eyes.Region;
import com.applitools.eyes.SyncTaskListener;
import com.applitools.eyes.locators.TextRegion;
import com.applitools.eyes.selenium.universal.dto.*;
import com.applitools.eyes.selenium.universal.dto.request.*;
import com.applitools.eyes.selenium.universal.dto.response.CommandCloseResponseDto;
import com.applitools.utils.GeneralUtils;
import org.openqa.selenium.StaleElementReferenceException;

/**
 * command executor
 */
public class CommandExecutor {

  private static USDKConnection connection;
  private static volatile CommandExecutor instance;

  public static CommandExecutor getInstance(String name, String version) {
    if (instance == null) {
      synchronized (CommandExecutor.class) {
        if (instance == null) {
          instance = new CommandExecutor(name, version);
        }
      }
    }
    return instance;
  }

  private CommandExecutor(String name, String version) {
    connection = new USDKConnection();
    connection.init();
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
    SyncTaskListener syncTaskListener = checkedCommand(request, true);
    ResponseDto<Reference> response = (ResponseDto<Reference>) syncTaskListener.get();
    if (response != null && response.getPayload().getError() != null) {
      String message = response.getPayload().getError().getMessage();
      if (message != null && message.contains("stale element reference")) {
        throw new StaleElementReferenceException(message);
      }
      throw new EyesException(message);
    }
    return response.getPayload().getResult();
  }

  public Reference managerOpenEyes(Reference ref, DriverDto driverDto, ConfigurationDto config) {
    RequestDto<OpenEyes> request = new RequestDto<>();
    request.setName("EyesManager.openEyes");
    request.setKey(UUID.randomUUID().toString());
    request.setPayload(new OpenEyes(ref, driverDto, config));
    SyncTaskListener syncTaskListener = checkedCommand(request, true);
    ResponseDto<Reference> referenceResponseDto = (ResponseDto<Reference>) syncTaskListener.get();

    if (referenceResponseDto != null && referenceResponseDto.getPayload().getError() != null) {
      String message = referenceResponseDto.getPayload().getError().getMessage();
      if (message != null && message.contains("stale element reference")) {
        throw new StaleElementReferenceException(message);
      }
      throw new EyesException(message);
    }
    return referenceResponseDto.getPayload().getResult();
  }

  public void eyesCheck(Reference eyesRef, CheckSettingsDto settings, ConfigurationDto config) {
    RequestDto<CheckEyes> request = new RequestDto<>();
    request.setName("Eyes.check");
    request.setKey(UUID.randomUUID().toString());
    request.setPayload(new CheckEyes(eyesRef, settings, config));
    SyncTaskListener syncTaskListener = checkedCommand(request, true);
    ResponseDto<MatchResultDto> responseDto = (ResponseDto<MatchResultDto>) syncTaskListener.get();
    if (responseDto != null && responseDto.getPayload().getError() != null) {
      String message = responseDto.getPayload().getError().getMessage();
      if (message != null && message.contains("stale element reference")) {
        throw new StaleElementReferenceException(message);
      }
      throw new EyesException(message);
    }
  }

  public Map<String, List<Region>> locate(Reference eyesRef, VisualLocatorSettingsDto locatorSettingsDto, ConfigurationDto config) {
    RequestDto<LocateDto> request = new RequestDto<>();
    request.setName("Eyes.locate");
    request.setKey(UUID.randomUUID().toString());
    request.setPayload(new LocateDto(eyesRef, locatorSettingsDto, config));
    SyncTaskListener syncTaskListener = checkedCommand(request, true);
    ResponseDto<Map<String, List<Region>>> locateResponse = (ResponseDto<Map<String, List<Region>>>) syncTaskListener.get();
    if (locateResponse != null && locateResponse.getPayload().getError() != null) {
      String message = locateResponse.getPayload().getError().getMessage();
      if (message != null && message.contains("stale element reference")) {
        throw new StaleElementReferenceException(message);
      }
      throw new EyesException(message);
    }
    return locateResponse.getPayload().getResult();
  }

  public Map<String, List<TextRegion>> extractTextRegions(Reference eyesRef, OCRSearchSettingsDto searchSettingsDto, ConfigurationDto config) {
    RequestDto<ExtractTextRegionsDto> request = new RequestDto<>();
    request.setName("Eyes.extractTextRegions");
    request.setKey(UUID.randomUUID().toString());
    request.setPayload(new ExtractTextRegionsDto(eyesRef, searchSettingsDto, config));
    SyncTaskListener syncTaskListener = checkedCommand(request, true);
    ResponseDto<Map<String, List<TextRegion>>> extractTextRegionsResponse = (ResponseDto<Map<String, List<TextRegion>>>) syncTaskListener.get();
    if (extractTextRegionsResponse != null && extractTextRegionsResponse.getPayload().getError() != null) {
      String message = extractTextRegionsResponse.getPayload().getError().getMessage();
      if (message != null && message.contains("stale element reference")) {
        throw new StaleElementReferenceException(message);
      }
      throw new EyesException(message);
    }
    return extractTextRegionsResponse.getPayload().getResult();

  }

  public List<String> extractText(Reference eyesRef, List<OCRExtractSettingsDto> extractSettingsDtoList, ConfigurationDto config) {
    RequestDto<ExtractTextDto> request = new RequestDto<>();
    request.setName("Eyes.extractText");
    request.setKey(UUID.randomUUID().toString());
    request.setPayload(new ExtractTextDto(eyesRef, extractSettingsDtoList, config));
    SyncTaskListener syncTaskListener = checkedCommand(request, true);
    ResponseDto<List<String>> responseDto = (ResponseDto<List<String>>) syncTaskListener.get();
    if (responseDto != null && responseDto.getPayload().getError() != null) {
      String message = responseDto.getPayload().getError().getMessage();
      if (message != null && message.contains("stale element reference")) {
        throw new StaleElementReferenceException(message);
      }
      throw new EyesException(message);
    }
    return responseDto.getPayload().getResult();
  }

  public List<CommandCloseResponseDto> close(Reference eyesRef, boolean waitResult) {
    RequestDto<CommandCloseRequestDto> request = new RequestDto<>();
    request.setName("Eyes.close");
    request.setKey(UUID.randomUUID().toString());
    request.setPayload(new CommandCloseRequestDto(eyesRef));
    SyncTaskListener syncTaskListener = checkedCommand(request, waitResult);
    if (!waitResult) {
      return null;
    }
    ResponseDto<List<CommandCloseResponseDto>> closeResponse = (ResponseDto<List<CommandCloseResponseDto>>) syncTaskListener.get();
    if (closeResponse != null && closeResponse.getPayload() != null && closeResponse.getPayload().getError() != null) {
      String message = closeResponse.getPayload().getError().getMessage();
      if (message != null && message.contains("stale element reference")) {
        throw new StaleElementReferenceException(message);
      }
      throw new EyesException(message);
    }

    return closeResponse.getPayload().getResult();
  }

  public List<CommandCloseResponseDto> abort(Reference eyesRef, boolean waitResult) {
    RequestDto<CommandCloseRequestDto> request = new RequestDto<>();
    request.setName("Eyes.abort");
    request.setKey(UUID.randomUUID().toString());
    request.setPayload(new CommandCloseRequestDto(eyesRef));
    SyncTaskListener syncTaskListener = checkedCommand(request, waitResult);
    if (!waitResult) {
      return null;
    }
    ResponseDto<List<CommandCloseResponseDto>> closeResponse = (ResponseDto<List<CommandCloseResponseDto>>) syncTaskListener.get();
    if (closeResponse != null && closeResponse.getPayload().getError() != null) {
      String message = closeResponse.getPayload().getError().getMessage();
      if (message != null && message.contains("stale element reference")) {
        throw new StaleElementReferenceException(message);
      }
      throw new EyesException(message);
    }
    return closeResponse.getPayload().getResult();
  }

  public static RectangleSizeDto getViewportSize(DriverDto driver) {
    RequestDto<CommandGetViewportSizeRequestDto> request = new RequestDto<>();
    request.setName("Core.getViewportSize");
    request.setKey(UUID.randomUUID().toString());
    request.setPayload(new CommandGetViewportSizeRequestDto(driver));
    SyncTaskListener syncTaskListener = checkedCommand(request, true);
    ResponseDto<RectangleSizeDto> getViewportSizeResponse = (ResponseDto<RectangleSizeDto>) syncTaskListener.get();
    return getViewportSizeResponse.getPayload().getResult();
  }

  public List<CommandCloseResponseDto> closeAllEyes(Reference managerRef) {
    RequestDto<CommandCloseAllEyesRequestDto> request = new RequestDto<>();
    request.setName("EyesManager.closeAllEyes");
    request.setKey(UUID.randomUUID().toString());
    request.setPayload(new CommandCloseAllEyesRequestDto(managerRef));
    SyncTaskListener syncTaskListener = checkedCommand(request, true);
    ResponseDto<List<CommandCloseResponseDto>> closeResponse = (ResponseDto<List<CommandCloseResponseDto>>) syncTaskListener.get();
    if (closeResponse != null && closeResponse.getPayload().getError() != null) {
      String message = closeResponse.getPayload().getError().getMessage();
      if (message != null && message.contains("stale element reference")) {
        throw new StaleElementReferenceException(message);
      }
      throw new EyesException(message);
    }
    return closeResponse.getPayload().getResult();
  }

  public TestResultsSummaryDto closeManager(Reference managerRef, Boolean throwError) {
    RequestDto<CommandCloseManagerRequestDto> request = new RequestDto<>();
    request.setName("EyesManager.closeManager");
    request.setKey(UUID.randomUUID().toString());
    request.setPayload(new CommandCloseManagerRequestDto(managerRef, throwError));
    SyncTaskListener syncTaskListener = checkedCommand(request, true);
    ResponseDto<TestResultsSummaryDto> closeResponse = (ResponseDto<TestResultsSummaryDto>) syncTaskListener.get();
    if (closeResponse != null && closeResponse.getPayload().getError() != null) {
      String message = closeResponse.getPayload().getError().getMessage();
      if (message != null && message.contains("stale element reference")) {
        throw new StaleElementReferenceException(message);
      }
      throw new EyesException(message);
    }
    return closeResponse.getPayload().getResult();
  }

  public void closeBatches(String batchIds, String serverUrl, String apiKey, ProxyDto proxy) {
    CloseBatchesDto closeBatches = new CloseBatchesDto(batchIds, serverUrl, apiKey, proxy);
    RequestDto<CommandCloseBatchesRequestDto> request = new RequestDto<>();
    request.setName("Core.closeBatches");
    request.setKey(UUID.randomUUID().toString());
    request.setPayload(new CommandCloseBatchesRequestDto(closeBatches));
    SyncTaskListener syncTaskListener = checkedCommand(request, true);

  }


  public static SyncTaskListener checkedCommand(Command command, boolean waitResult) {
    try {
      return connection.executeCommand(command, waitResult);
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }

}
