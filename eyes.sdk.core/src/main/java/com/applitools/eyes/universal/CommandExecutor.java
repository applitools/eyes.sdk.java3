package com.applitools.eyes.universal;

import java.util.List;
import java.util.Map;
import java.util.UUID;


import com.applitools.eyes.*;
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

  public static CommandExecutor getInstance(String name, String version, String protocol, String[] commands,
                                            USDKListener listener, StaleElementReferenceException e) {
    if (instance == null) {
      synchronized (CommandExecutor.class) {
        if (instance == null) {
          staleElementReferenceException = e;
          instance = new CommandExecutor(name, version, protocol, commands, listener);
        }
      }
    }
    return instance;
  }

  private CommandExecutor(String name, String version, String protocol, String[] commands, USDKListener listener) {
    connection = new USDKConnection(listener);
    connection.init();
    makeCore(name, version, GeneralUtils.getPropertyString("user.dir"), protocol, commands);
  }

  public void makeCore(String name, String version, String cwd, String protocol, String[] commands) {
    EventDto<MakeCore> request = new EventDto<>();
    MakeCore makeCore = new MakeCore(name, version, cwd, protocol, commands);
    request.setName("Core.makeCore");
    request.setPayload(makeCore);
    checkedCommand(request);
  }

  // TODO - agentId is currently null because this will set the agentID incorrectly in the dashboard/logs
  public Reference coreMakeManager(String type, Integer concurrency, Integer legacyConcurrency, String agentId) {
    RequestDto<MakeManager> request = new RequestDto<>();
    request.setName("Core.makeManager");
    request.setKey(UUID.randomUUID().toString());
    request.setPayload(new MakeManager(type, concurrency, legacyConcurrency, null));
    SyncTaskListener syncTaskListener = checkedCommand(request);

    ResponseDto<Reference> makeManagerResponse = (ResponseDto<Reference>) syncTaskListener.get();
    if (makeManagerResponse != null && makeManagerResponse.getPayload().getError() != null) {
      String message = makeManagerResponse.getPayload().getError().getMessage();
      if (message != null && message.contains("stale element reference")) {
        staleElementReferenceException.throwException(message);
      }
      throw new EyesException(message);
    }
    return makeManagerResponse.getPayload().getResult();
  }

  public Reference managerOpenEyes(Reference ref, ITargetDto target, OpenSettingsDto settings, ConfigurationDto config) {
    RequestDto<OpenEyes> request = new RequestDto<>();
    request.setName("EyesManager.openEyes");
    request.setKey(UUID.randomUUID().toString());
    request.setPayload(new OpenEyes(ref, target, settings, config));

    SyncTaskListener syncTaskListener = checkedCommand(request);
    ResponseDto<Reference> referenceResponseDto = (ResponseDto<Reference>) syncTaskListener.get();

    if (referenceResponseDto != null && referenceResponseDto.getPayload().getError() != null) {
      String message = referenceResponseDto.getPayload().getError().getMessage();
      if (message != null && message.contains("stale element reference")) {
        staleElementReferenceException.throwException(message);
      }
      ErrorDto error = referenceResponseDto.getPayload().getError();
      if (error.getReason() != null && error.getReason().equals("spec-driver")) {
        throw new EyesException("Message: " +  error.getMessage() + ", Stack: " +  error.getStack());
      }
      throw new EyesException(message);
    }
    return referenceResponseDto.getPayload().getResult();
  }

  public void eyesCheck(Reference eyesRef, ITargetDto target, CheckSettingsDto settings, ConfigurationDto config) {
    RequestDto<CheckEyes> request = new RequestDto<>();
    request.setName("Eyes.check");
    request.setKey(UUID.randomUUID().toString());
    request.setPayload(new CheckEyes(eyesRef, target, settings, config, settings.getType()));
    SyncTaskListener syncTaskListener = checkedCommand(request);

    ResponseDto<MatchResultDto> checkResponse = (ResponseDto<MatchResultDto>) syncTaskListener.get();
    if (checkResponse != null && checkResponse.getPayload().getError() != null) {
      String message = checkResponse.getPayload().getError().getMessage();
      if (message != null && message.contains("stale element reference")) {
        staleElementReferenceException.throwException(message);
      }
      ErrorDto error = checkResponse.getPayload().getError();
      if (error.getReason() != null && error.getReason().equals("spec-driver")) {
        throw new EyesException("Message: " +  error.getMessage() + ", Stack: " +  error.getStack());
      }
      throw new EyesException(message);
    }
  }

  public Map<String, List<Region>> locate(ITargetDto target, VisualLocatorSettingsDto locatorSettingsDto, ConfigurationDto config) {
    RequestDto<LocateDto> request = new RequestDto<>();
    request.setName("Core.locate");
    request.setKey(UUID.randomUUID().toString());
    request.setPayload(new LocateDto(target, locatorSettingsDto, config));
    SyncTaskListener syncTaskListener = checkedCommand(request);

    ResponseDto<Map<String, List<Region>>> locateResponse = (ResponseDto<Map<String, List<Region>>>) syncTaskListener.get();
    if (locateResponse != null && locateResponse.getPayload().getError() != null) {
      String message = locateResponse.getPayload().getError().getMessage();
      if (message != null && message.contains("stale element reference")) {
        staleElementReferenceException.throwException(message);
      }
      ErrorDto error = locateResponse.getPayload().getError();
      if (error.getReason() != null && error.getReason().equals("spec-driver")) {
        throw new EyesException("Message: " +  error.getMessage() + ", Stack: " +  error.getStack());
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
    SyncTaskListener syncTaskListener = checkedCommand(request);

    ResponseDto<Map<String, List<TextRegion>>> locateTextResponse = (ResponseDto<Map<String, List<TextRegion>>>) syncTaskListener.get();
    if (locateTextResponse != null && locateTextResponse.getPayload().getError() != null) {
      String message = locateTextResponse.getPayload().getError().getMessage();
      if (message != null && message.contains("stale element reference")) {
        staleElementReferenceException.throwException(message);
      }
      ErrorDto error = locateTextResponse.getPayload().getError();
      if (error.getReason() != null && error.getReason().equals("spec-driver")) {
        throw new EyesException("Message: " +  error.getMessage() + ", Stack: " +  error.getStack());
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
    SyncTaskListener syncTaskListener = checkedCommand(request);

    ResponseDto<List<String>> extractTextResponse = (ResponseDto<List<String>>) syncTaskListener.get();
    if (extractTextResponse != null && extractTextResponse.getPayload().getError() != null) {
      String message = extractTextResponse.getPayload().getError().getMessage();
      if (message != null && message.contains("stale element reference")) {
        staleElementReferenceException.throwException(message);
      }
      ErrorDto error = extractTextResponse.getPayload().getError();
      if (error.getReason() != null && error.getReason().equals("spec-driver")) {
        throw new EyesException("Message: " +  error.getMessage() + ", Stack: " +  error.getStack());
      }
      throw new EyesException(message);
    }
    return extractTextResponse.getPayload().getResult();
  }

  public List<CommandCloseResponseDto> eyesCheckAndClose(Reference eyesRef, ITargetDto target, CheckSettingsDto checkSettings, CloseSettingsDto closeSettings, ConfigurationDto config) {
    RequestDto<CommandCheckAndCloseRequestDto> request = new RequestDto<>();
    request.setName("Eyes.checkAndClose");
    request.setKey(UUID.randomUUID().toString());
    request.setPayload(new CommandCheckAndCloseRequestDto(eyesRef, target, checkSettings, closeSettings, config));
    SyncTaskListener syncTaskListener = checkedCommand(request);

    ResponseDto<List<CommandCloseResponseDto>> closeResponse = (ResponseDto<List<CommandCloseResponseDto>>) syncTaskListener.get();
    if (closeResponse != null && closeResponse.getPayload().getError() != null) {
      String message = closeResponse.getPayload().getError().getMessage();
      if (message != null && message.contains("stale element reference")) {
        staleElementReferenceException.throwException(message);
      }
      ErrorDto error = closeResponse.getPayload().getError();
      throw new EyesException("Message: " +  error.getMessage() + ", Stack: " +  error.getStack());
    }

    return closeResponse.getPayload().getResult();
  }

  public List<CommandCloseResponseDto> close(Reference eyesRef, CloseSettingsDto closeSettings, ConfigurationDto config, boolean waitResult) {
    RequestDto<CommandCloseRequestDto> request = new RequestDto<>();
    request.setName("Eyes.close");
    request.setKey(UUID.randomUUID().toString());
    request.setPayload(new CommandCloseRequestDto(eyesRef, closeSettings, config));
    SyncTaskListener syncTaskListener = checkedCommand(request);
    if (!waitResult) {
      return null;
    }

    ResponseDto<List<CommandCloseResponseDto>> closeResponse = (ResponseDto<List<CommandCloseResponseDto>>) syncTaskListener.get();
    if (closeResponse != null && closeResponse.getPayload() != null && closeResponse.getPayload().getError() != null) {
      String message = closeResponse.getPayload().getError().getMessage();
      if (message != null && message.contains("stale element reference")) {
        staleElementReferenceException.throwException(message);
      }
      ErrorDto error = closeResponse.getPayload().getError();
      if (error.getReason() != null && error.getReason().equals("spec-driver")) {
        throw new EyesException("Message: " +  error.getMessage() + ", Stack: " +  error.getStack());
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
    SyncTaskListener syncTaskListener = checkedCommand(request);
    if (!waitResult) {
      return null;
    }

    ResponseDto<List<CommandCloseResponseDto>> abortResponse = (ResponseDto<List<CommandCloseResponseDto>>) syncTaskListener.get();
    if (abortResponse != null && abortResponse.getPayload().getError() != null) {
      String message = abortResponse.getPayload().getError().getMessage();
      if (message != null && message.contains("stale element reference")) {
        staleElementReferenceException.throwException(message);
      }
      throw new EyesException(message);
    }
    return abortResponse.getPayload().getResult();
  }

  public List<CommandCloseResponseDto> closeAllEyes(Reference managerRef) {
    RequestDto<CommandCloseAllEyesRequestDto> request = new RequestDto<>();
    request.setName("EyesManager.closeAllEyes");
    request.setKey(UUID.randomUUID().toString());
    request.setPayload(new CommandCloseAllEyesRequestDto(managerRef));
    SyncTaskListener syncTaskListener = checkedCommand(request);

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
    SyncTaskListener syncTaskListener = checkedCommand(request);

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
            throw new EyesException(message);
          }
        } else {
          return closeResponse.getPayload().getResult();
        }
    }
    return null;
  }

  public static RectangleSizeDto getViewportSize(ITargetDto driver) {
    RequestDto<CommandGetViewportSizeRequestDto> request = new RequestDto<>();
    request.setName("Core.getViewportSize");
    request.setKey(UUID.randomUUID().toString());
    request.setPayload(new CommandGetViewportSizeRequestDto(driver));
    SyncTaskListener syncTaskListener = checkedCommand(request);

    ResponseDto<RectangleSizeDto> getViewportSizeResponse = (ResponseDto<RectangleSizeDto>) syncTaskListener.get();
    return getViewportSizeResponse.getPayload().getResult();
  }

  public static DebugHistoryDto getDebugHistory() {
    RequestDto request = new RequestDto<>();
    request.setName("Debug.getHistory");
    request.setKey(UUID.randomUUID().toString());
    SyncTaskListener syncTaskListener = checkedCommand(request);

    ResponseDto<DebugHistoryDto> getDebugHistory = (ResponseDto<DebugHistoryDto>) syncTaskListener.get();
    return getDebugHistory.getPayload().getResult();
  }

  public static void deleteTest(DeleteTestSettingsDto settings) {
    RequestDto<CommandDeleteTestRequestDto> request = new RequestDto<>();
    request.setName("Core.deleteTest");
    request.setKey(UUID.randomUUID().toString());
    request.setPayload(new CommandDeleteTestRequestDto(settings));
    SyncTaskListener syncTaskListener = checkedCommand(request);

    ResponseDto deleteTestResponse = (ResponseDto) syncTaskListener.get();
    if (deleteTestResponse != null && deleteTestResponse.getPayload().getError() != null) {
      String message = deleteTestResponse.getPayload().getError().getMessage();
      throw new EyesException(message);
    }
  }

  public static void closeBatch(List<CloseBatchSettingsDto> settings) {
    RequestDto<CommandCloseBatchRequestDto> request = new RequestDto<>();
    request.setName("Core.closeBatch");
    request.setKey(UUID.randomUUID().toString());
    request.setPayload(new CommandCloseBatchRequestDto(settings));
    SyncTaskListener syncTaskListener = checkedCommand(request);

    ResponseDto closeBatchResponse = (ResponseDto) syncTaskListener.get();
    if (closeBatchResponse != null && closeBatchResponse.getPayload().getError() != null) {
      String message = closeBatchResponse.getPayload().getError().getMessage();
      throw new EyesException(message);
    }
  }

  public static SyncTaskListener checkedCommand(Command command) {
    try {
      return connection.executeCommand(command);
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
