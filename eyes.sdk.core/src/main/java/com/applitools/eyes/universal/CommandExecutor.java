package com.applitools.eyes.universal;

import java.util.List;
import java.util.Map;
import java.util.UUID;


import com.applitools.eyes.EyesException;
import com.applitools.eyes.Region;
import com.applitools.eyes.SyncTaskListener;
import com.applitools.eyes.TestResults;
import com.applitools.eyes.exceptions.DiffsFoundException;
import com.applitools.eyes.exceptions.NewTestException;
import com.applitools.eyes.exceptions.StaleElementReferenceException;
import com.applitools.eyes.exceptions.TestFailedException;
import com.applitools.eyes.locators.TextRegion;
import com.applitools.eyes.universal.dto.*;
import com.applitools.eyes.universal.dto.request.*;
import com.applitools.eyes.universal.dto.response.CommandCloseResponseDto;
import com.applitools.utils.GeneralUtils;

/**
 * command executor
 */
public class CommandExecutor {

  private static USDKConnection connection;
  private static volatile CommandExecutor instance;
  private static StaleElementReferenceException staleElementReferenceException;

  public static CommandExecutor getInstance(String name, String version, StaleElementReferenceException e) {
    if (instance == null) {
      synchronized (CommandExecutor.class) {
        if (instance == null) {
          instance = new CommandExecutor(name, version, e);
        }
      }
    }
    return instance;
  }

  private CommandExecutor(String name, String version, StaleElementReferenceException e) {
    connection = new USDKConnection();
    connection.init();
    staleElementReferenceException = e;
    //TODO - commands arguments probably shouldn't be null
    makeCore(name, version, GeneralUtils.getPropertyString("user.dir"), "webdriver", null);
  }

  public void makeCore(String name, String version, String cwd, String protocol, String[] commands) {
    EventDto<MakeCore> request = new EventDto<>();
    MakeCore makeCore = new MakeCore(name, version, cwd, protocol, commands);
    request.setName("Core.makeCore");
    request.setPayload(makeCore);
    checkedCommand(request, false);
  }

  // TODO - agentId is currently null because this will set the agentID incorrectly in the dashboard/logs
  public Reference coreMakeManager(String type, Integer concurrency, Integer legacyConcurrency, String agentId) {
    RequestDto<MakeManager> request = new RequestDto<>();
    request.setName("Core.makeManager");
    request.setKey(UUID.randomUUID().toString());
    request.setPayload(new MakeManager(type, concurrency, legacyConcurrency, null));
    SyncTaskListener syncTaskListener = checkedCommand(request, true);
    ResponseDto<Reference> response = (ResponseDto<Reference>) syncTaskListener.get();
    if (response != null && response.getPayload().getError() != null) {
      String message = response.getPayload().getError().getMessage();
      if (message != null && message.contains("stale element reference")) {
        staleElementReferenceException.throwException(message);
      }
      throw new EyesException(message);
    }
    return response.getPayload().getResult();
  }

  public Reference managerOpenEyes(Reference ref, ITargetDto target, OpenSettingsDto settings, ConfigurationDto config) {
    RequestDto<OpenEyes> request = new RequestDto<>();
    request.setName("EyesManager.openEyes");
    request.setKey(UUID.randomUUID().toString());
    request.setPayload(new OpenEyes(ref, target, settings, config));
    SyncTaskListener syncTaskListener = checkedCommand(request, true);
    ResponseDto<Reference> referenceResponseDto = (ResponseDto<Reference>) syncTaskListener.get();

    if (referenceResponseDto != null && referenceResponseDto.getPayload().getError() != null) {
      String message = referenceResponseDto.getPayload().getError().getMessage();
      if (message != null && message.contains("stale element reference")) {
        staleElementReferenceException.throwException(message);
      }
      throw new EyesException(message);
    }
    return referenceResponseDto.getPayload().getResult();
  }

  public void eyesCheck(Reference eyesRef, ITargetDto target, CheckSettingsDto settings, ConfigurationDto config) {
    RequestDto<CheckEyes> request = new RequestDto<>();
    request.setName("Eyes.check");
    request.setKey(UUID.randomUUID().toString());
    request.setPayload(new CheckEyes(eyesRef, target, settings, config));
    SyncTaskListener syncTaskListener = checkedCommand(request, true);
    ResponseDto<MatchResultDto> responseDto = (ResponseDto<MatchResultDto>) syncTaskListener.get();
    if (responseDto != null && responseDto.getPayload().getError() != null) {
      String message = responseDto.getPayload().getError().getMessage();
      if (message != null && message.contains("stale element reference")) {
        staleElementReferenceException.throwException(message);
      }
      throw new EyesException(message);
    }
  }

  public Map<String, List<Region>> locate(ITargetDto target, VisualLocatorSettingsDto locatorSettingsDto, ConfigurationDto config) {
    RequestDto<LocateDto> request = new RequestDto<>();
    request.setName("Core.locate");
    request.setKey(UUID.randomUUID().toString());
    request.setPayload(new LocateDto(target, locatorSettingsDto, config));
    SyncTaskListener syncTaskListener = checkedCommand(request, true);
    ResponseDto<Map<String, List<Region>>> locateResponse = (ResponseDto<Map<String, List<Region>>>) syncTaskListener.get();
    if (locateResponse != null && locateResponse.getPayload().getError() != null) {
      String message = locateResponse.getPayload().getError().getMessage();
      if (message != null && message.contains("stale element reference")) {
        staleElementReferenceException.throwException(message);
      }
      throw new EyesException(message);
    }
    return locateResponse.getPayload().getResult();
  }

  // former extractTextRegions
  public Map<String, List<TextRegion>> locateText(Reference eyesRef, ITargetDto target, OCRSearchSettingsDto searchSettingsDto, ConfigurationDto config) {
    RequestDto<LocateTextDto> request = new RequestDto<>();
    request.setName("Eyes.locateText");
    request.setKey(UUID.randomUUID().toString());
    request.setPayload(new LocateTextDto(eyesRef, target, searchSettingsDto, config));
    SyncTaskListener syncTaskListener = checkedCommand(request, true);
    ResponseDto<Map<String, List<TextRegion>>> locateTextResponse = (ResponseDto<Map<String, List<TextRegion>>>) syncTaskListener.get();
    if (locateTextResponse != null && locateTextResponse.getPayload().getError() != null) {
      String message = locateTextResponse.getPayload().getError().getMessage();
      if (message != null && message.contains("stale element reference")) {
        staleElementReferenceException.throwException(message);
      }
      throw new EyesException(message);
    }
    return locateTextResponse.getPayload().getResult();

  }

  public List<String> extractText(Reference eyesRef, ITargetDto target, List<OCRExtractSettingsDto> extractSettingsDtoList, ConfigurationDto config) {
    RequestDto<ExtractTextDto> request = new RequestDto<>();
    request.setName("Eyes.extractText");
    request.setKey(UUID.randomUUID().toString());
    request.setPayload(new ExtractTextDto(eyesRef, target, extractSettingsDtoList, config));
    SyncTaskListener syncTaskListener = checkedCommand(request, true);
    ResponseDto<List<String>> responseDto = (ResponseDto<List<String>>) syncTaskListener.get();
    if (responseDto != null && responseDto.getPayload().getError() != null) {
      String message = responseDto.getPayload().getError().getMessage();
      if (message != null && message.contains("stale element reference")) {
        staleElementReferenceException.throwException(message);
      }
      throw new EyesException(message);
    }
    return responseDto.getPayload().getResult();
  }

  public List<CommandCloseResponseDto> eyesCheckAndClose(Reference eyesRef, ITargetDto target, CheckSettingsDto checkSettings, CloseSettingsDto closeSettings, ConfigurationDto config) {
    RequestDto<CommandCheckAndCloseRequestDto> request = new RequestDto<>();
    request.setName("Eyes.checkAndClose");
    request.setKey(UUID.randomUUID().toString());
    request.setPayload(new CommandCheckAndCloseRequestDto(eyesRef, target, checkSettings, closeSettings, config));
    SyncTaskListener syncTaskListener = checkedCommand(request, true);
    ResponseDto<List<CommandCloseResponseDto>> closeResponse = (ResponseDto<List<CommandCloseResponseDto>>) syncTaskListener.get();
    if (closeResponse != null && closeResponse.getPayload().getError() != null) {
      String message = closeResponse.getPayload().getError().getMessage();
      if (message != null && message.contains("stale element reference")) {
        staleElementReferenceException.throwException(message);
      }
      throw new EyesException(message);
    }

    return closeResponse.getPayload().getResult();
  }

  public List<CommandCloseResponseDto> close(Reference eyesRef, CloseSettingsDto closeSettings, ConfigurationDto config, boolean waitResult) {
    RequestDto<CommandCloseRequestDto> request = new RequestDto<>();
    request.setName("Eyes.close");
    request.setKey(UUID.randomUUID().toString());
    request.setPayload(new CommandCloseRequestDto(eyesRef, closeSettings, config));
    SyncTaskListener syncTaskListener = checkedCommand(request, waitResult);
    if (!waitResult) {
      return null;
    }
    ResponseDto<List<CommandCloseResponseDto>> closeResponse = (ResponseDto<List<CommandCloseResponseDto>>) syncTaskListener.get();
    if (closeResponse != null && closeResponse.getPayload() != null && closeResponse.getPayload().getError() != null) {
      String message = closeResponse.getPayload().getError().getMessage();
      if (message != null && message.contains("stale element reference")) {
        staleElementReferenceException.throwException(message);
      }
      throw new EyesException(message);
    }

    return closeResponse.getPayload().getResult();
  }

  public List<CommandCloseResponseDto> abort(Reference eyesRef, boolean waitResult) {
    RequestDto<CommandAbortRequestDto> request = new RequestDto<>();
    request.setName("Eyes.abort");
    request.setKey(UUID.randomUUID().toString());
    request.setPayload(new CommandAbortRequestDto(eyesRef));
    SyncTaskListener syncTaskListener = checkedCommand(request, waitResult);
    if (!waitResult) {
      return null;
    }
    ResponseDto<List<CommandCloseResponseDto>> closeResponse = (ResponseDto<List<CommandCloseResponseDto>>) syncTaskListener.get();
    if (closeResponse != null && closeResponse.getPayload().getError() != null) {
      String message = closeResponse.getPayload().getError().getMessage();
      if (message != null && message.contains("stale element reference")) {
        staleElementReferenceException.throwException(message);
      }
      throw new EyesException(message);
    }
    return closeResponse.getPayload().getResult();
  }

  public static RectangleSizeDto getViewportSize(ITargetDto driver) {
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
        staleElementReferenceException.throwException(message);
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
    if (closeResponse != null && closeResponse.getPayload() != null) {
        if (closeResponse.getPayload().getError() != null) {
          ErrorDto error = closeResponse.getPayload().getError();
          String message = error.getMessage();
          if (message != null && message.contains("stale element reference")) {
            staleElementReferenceException.throwException(message);
          } else if (error.getReason() != null) {
            throwExceptionBasedOnReason(error.getReason(), error.getInfo() == null ?
                    null : error.getInfo().getResult());
          } else {
            throw new EyesException("Message: " +  error.getMessage() + ", Stack: " +  error.getStack());
          }
        } else {
          return closeResponse.getPayload().getResult();
        }
    }
    return null;
  }


  public static SyncTaskListener checkedCommand(Command command, boolean waitResult) {
    try {
      return connection.executeCommand(command, waitResult);
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }

  private static void throwExceptionBasedOnReason(String reason, TestResults testResults) {
    String scenarioIdOrName;
    String appIdOrName;

    if (reason == null || reason.isEmpty() || reason.equals("internal")) {
      return;
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
        throw new DiffsFoundException(testResults, scenarioIdOrName, appIdOrName);
      case "test failed":
        throw new TestFailedException(testResults, scenarioIdOrName, appIdOrName);
      case "test new":
        throw new NewTestException(testResults, scenarioIdOrName, appIdOrName);
      default:
        throw new UnsupportedOperationException("Unsupported exception type: " + reason);
    }
  }
}
