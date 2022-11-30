package com.applitools.eyes.universal;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.applitools.eyes.EyesException;
import com.applitools.eyes.Logger;
import com.applitools.eyes.Region;
import com.applitools.eyes.SyncTaskListener;
import com.applitools.eyes.locators.TextRegion;
import com.applitools.eyes.logging.Stage;
import com.applitools.eyes.logging.TraceLevel;
import com.applitools.eyes.universal.dto.*;
import com.applitools.eyes.universal.dto.response.CommandCloseResponseDto;
import com.applitools.eyes.universal.server.UniversalSdkNativeLoader;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.asynchttpclient.DefaultAsyncHttpClientConfig;
import org.asynchttpclient.Dsl;
import org.asynchttpclient.ws.WebSocket;
import org.asynchttpclient.ws.WebSocketListener;
import org.asynchttpclient.ws.WebSocketUpgradeHandler;

/**
 * universal sdk connection
 */
public class USDKConnection {
  private WebSocket webSocket;
  protected Logger logger = new Logger();
  private ObjectMapper objectMapper;

  private Map<String, SyncTaskListener<ResponseDto<?>>> map;

  public USDKConnection() {
    map = new HashMap<>();
    objectMapper = new ObjectMapper();
    objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
  }

  public void init() {
   webSocket = openWebsocket();
  }

  private WebSocket openWebsocket() {

    try {

      String url = String.format("ws://localhost:%s/eyes", UniversalSdkNativeLoader.getPort());
      return Dsl.asyncHttpClient(new DefaultAsyncHttpClientConfig.Builder().setWebSocketMaxFrameSize(268435456)).prepareGet(url)
          .execute(new WebSocketUpgradeHandler.Builder().addWebSocketListener(
              new WebSocketListener() {

                @Override
                public void onOpen(WebSocket websocket) {

                }

                @Override
                public void onClose(WebSocket webSocket, int i, String s) {

                }

                @Override
                public void onTextFrame(String payload, boolean finalFragment, int rsv) {
                  if (payload.contains("Core.makeManager") || payload.contains("EyesManager.openEyes")) {
                    try {
                      ResponseDto<Reference> referenceResponseDto = objectMapper.readValue(payload, new TypeReference<ResponseDto<Reference>>() {
                      });

                      SyncTaskListener<ResponseDto<?>> syncTaskLister = map.get(referenceResponseDto.getKey());
                      syncTaskLister.onComplete(referenceResponseDto);
                      map.remove(referenceResponseDto.getKey());
                    } catch (Exception e) {
                      e.printStackTrace();
                    }
                  }
                  // "Debug.getHistory" could contain "Eyes.check" inside since it is a command of eyes.
                  // getHistory is used in proxy tests.
                  else if(payload.contains("Eyes.check") && !payload.contains("Debug.getHistory")) {
                    try {
                      ResponseDto<List<MatchResultDto>> checkResponse = objectMapper.readValue(payload, new TypeReference<ResponseDto<List<MatchResultDto>>>() {
                      });
                      SyncTaskListener<ResponseDto<?>> syncTaskLister = map.get(checkResponse.getKey());
                      syncTaskLister.onComplete(checkResponse);
                      map.remove(checkResponse.getKey());
                    } catch (Exception e) {
                      e.printStackTrace();
                    }
                  } else if (payload.contains("Core.locate")) {
                    try {
                      ResponseDto<Map<String, List<Region>>> locateResponse = objectMapper
                              .readValue(payload, new TypeReference<ResponseDto<Map<String, List<Region>>>>() {
                              });
                      SyncTaskListener<ResponseDto<?>> syncTaskLister = map.get(locateResponse.getKey());
                      syncTaskLister.onComplete(locateResponse);
                      map.remove(locateResponse.getKey());
                    } catch (Exception e) {
                      e.printStackTrace();
                    }
                  } else if (payload.contains("Eyes.close") || payload.contains("Eyes.abort") || payload.contains("EyesManager.closeAllEyes")) {
                    try {
                      ResponseDto<List<CommandCloseResponseDto>> closeResponse = objectMapper.readValue(payload,
                              new TypeReference<ResponseDto<List<CommandCloseResponseDto>>>() {
                              });
                      SyncTaskListener<ResponseDto<?>> syncTaskLister = map.get(closeResponse.getKey());
                      syncTaskLister.onComplete(closeResponse);
                      map.remove(closeResponse.getKey());
                    } catch (Exception e) {
                      e.printStackTrace();
                    }
                  } else if (payload.contains("Eyes.locateText")) {
                    try {
                      ResponseDto<Map<String, List<TextRegion>>> extractTextRegionsResponse = objectMapper
                              .readValue(payload, new TypeReference<ResponseDto<Map<String, List<TextRegion>>>>() {
                              });
                      SyncTaskListener<ResponseDto<?>> syncTaskLister = map.get(extractTextRegionsResponse.getKey());
                      syncTaskLister.onComplete(extractTextRegionsResponse);
                      map.remove(extractTextRegionsResponse.getKey());
                    } catch (Exception e) {
                      e.printStackTrace();
                    }
                  } else if (payload.contains("Eyes.extractText")) {
                    try {
                      ResponseDto<List<String>> responseDto = objectMapper.readValue(payload, new TypeReference<ResponseDto<List<String>>>() {
                      });
                      SyncTaskListener<ResponseDto<?>> syncTaskLister = map.get(responseDto.getKey());
                      syncTaskLister.onComplete(responseDto);
                      map.remove(responseDto.getKey());
                    } catch (Exception e) {
                      e.printStackTrace();
                    }
                  } else if (payload.contains("Core.getViewportSize")) {
                    try {
                      ResponseDto<RectangleSizeDto> getViewportSizeResponse = objectMapper.readValue(payload, new TypeReference<ResponseDto<RectangleSizeDto>>() {
                      });
                      SyncTaskListener<ResponseDto<?>> syncTaskLister = map.get(getViewportSizeResponse.getKey());
                      syncTaskLister.onComplete(getViewportSizeResponse);
                      map.remove(getViewportSizeResponse.getKey());
                    } catch (Exception e) {
                      e.printStackTrace();
                    }
                  } else if (payload.contains("EyesManager.closeManager")) {
                    try {
                      ResponseDto<TestResultsSummaryDto> closeManagerResponse = objectMapper.readValue(payload,
                              new TypeReference<ResponseDto<TestResultsSummaryDto>>() {
                              });
                      SyncTaskListener<ResponseDto<?>> syncTaskLister = map.get(closeManagerResponse.getKey());
                      syncTaskLister.onComplete(closeManagerResponse);
                      map.remove(closeManagerResponse.getKey());
                    } catch (Exception e) {
                      e.printStackTrace();
                    }

                  } else if (payload.contains("Debug.getHistory")) {
                    try {
                      ResponseDto<DebugHistoryDto> debugHistoryResponse = objectMapper.readValue(payload,
                              new TypeReference<ResponseDto<DebugHistoryDto>>() {
                              });

                      SyncTaskListener<ResponseDto<?>> syncTaskListener = map.get(debugHistoryResponse.getKey());
                      syncTaskListener.onComplete(debugHistoryResponse);
                      map.remove(debugHistoryResponse.getKey());
                    } catch (Exception e) {
                      e.printStackTrace();
                    }
                  } else if (payload.contains("Core.deleteTest")) {
                    try {
                      ResponseDto deleteTestResponse = objectMapper.readValue(payload,
                              new TypeReference<ResponseDto>() {
                              });

                      SyncTaskListener<ResponseDto<?>> syncTaskListener = map.get(deleteTestResponse.getKey());
                      syncTaskListener.onComplete(deleteTestResponse);
                      map.remove(deleteTestResponse.getKey());
                    } catch (Exception e) {
                      e.printStackTrace();
                    }
                  } else if (payload.contains("Core.closeBatch")) {
                    try {
                      ResponseDto closeBatchResponse = objectMapper.readValue(payload,
                              new TypeReference<ResponseDto>() {
                              });

                      SyncTaskListener<ResponseDto<?>> syncTaskListener = map.get(closeBatchResponse.getKey());
                      syncTaskListener.onComplete(closeBatchResponse);
                      map.remove(closeBatchResponse.getKey());
                    } catch (Exception e) {
                      e.printStackTrace();
                    }
                  } else if (payload.contains("Server.log")) {
                    try {
                      LogResponseDto serverLogResponse = objectMapper.readValue(payload,
                              new TypeReference<LogResponseDto>() {
                              });
                      String message = "eyes | " + new Timestamp(System.currentTimeMillis())
                              + " | [" + serverLogResponse.getPayload().getLevel() + "] | "
                              + serverLogResponse.getPayload().getMessage();
                      logger.log(TraceLevel.Debug, Stage.GENERAL, message);
                      System.out.println(message);
                    } catch (Exception e) {
                      e.printStackTrace();
                    }
                  }
                }

                @Override
                public void onError(Throwable t) {
                  throw new EyesException("Websocket communication error: " + t.getMessage(), t);
                }
              }).build()).get();

    } catch (Exception e) {
      throw new EyesException("Communication with server failed, reason: " + e.getMessage(), e);
    }

  }


  public synchronized SyncTaskListener executeCommand(Command command, boolean waitResult) throws Exception {
    if (command instanceof EventDto<?>) {
      webSocket.sendTextFrame(objectMapper.writeValueAsString(command));
      return null;
    }

    RequestDto<?> request = (RequestDto<?>) command;
    SyncTaskListener<ResponseDto<?>> syncTaskListener = new SyncTaskListener<>(logger, request.getKey());
    map.put(request.getKey(), syncTaskListener);
    webSocket.sendTextFrame(objectMapper.writeValueAsString(request));
    return syncTaskListener;
  }


}