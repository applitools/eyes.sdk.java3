package com.applitools.eyes.selenium;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.applitools.eyes.EyesException;
import com.applitools.eyes.Logger;
import com.applitools.eyes.Region;
import com.applitools.eyes.SyncTaskListener;
import com.applitools.eyes.locators.TextRegion;
import com.applitools.eyes.selenium.universal.dto.Command;
import com.applitools.eyes.selenium.universal.dto.EventDto;
import com.applitools.eyes.selenium.universal.dto.MatchResultDto;
import com.applitools.eyes.selenium.universal.dto.RectangleSizeDto;
import com.applitools.eyes.selenium.universal.dto.RegionDto;
import com.applitools.eyes.selenium.universal.dto.RequestDto;
import com.applitools.eyes.selenium.universal.dto.ResponseDto;
import com.applitools.eyes.selenium.universal.dto.response.CommandCloseResponseDto;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
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
  private SyncTaskListener<ResponseDto<?>> syncTaskListener;
  private ObjectMapper objectMapper;

  private Map<String, ResponseDto<?>> map;

  public USDKConnection() {
   // webSocket = openWebsocket();
    map = new HashMap<>();
    objectMapper = new ObjectMapper();
    objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
  }

  public void init() {
   webSocket = openWebsocket();
  }

  private WebSocket openWebsocket() {

    try {
      return Dsl.asyncHttpClient().prepareGet("ws://localhost:21077/eyes")
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
                  System.out.println("JSON_RESPONSE: " + payload);
                  if (payload.contains("Core.makeManager") || payload.contains("EyesManager.openEyes")) {
                    try {
                      ResponseDto<Reference> referenceResponseDto = objectMapper.readValue(payload, new TypeReference<ResponseDto<Reference>>() {});
                      map.put(referenceResponseDto.getKey(), referenceResponseDto);
                      syncTaskListener.onComplete(referenceResponseDto);
                    } catch (Exception e) {
                      e.printStackTrace();
                    }
                  } else if(payload.contains("Eyes.check")) {
                    try {
                      ResponseDto<MatchResultDto> checkResponse = objectMapper.readValue(payload, new TypeReference<ResponseDto<MatchResultDto>>() {});
                      map.put(checkResponse.getKey(), checkResponse);
                      syncTaskListener.onComplete(checkResponse);
                    } catch (Exception e) {
                      e.printStackTrace();
                    }
                  } else if (payload.contains("Eyes.locate")) {
                    try {
                      ResponseDto<Map<String, List<Region>>> locateResponse = objectMapper
                          .readValue(payload, new TypeReference<ResponseDto<Map<String, List<Region>>>>() {});
                      map.put(locateResponse.getKey(), locateResponse);
                      syncTaskListener.onComplete(locateResponse);
                    } catch (Exception e) {
                      e.printStackTrace();
                    }
                  } else if (payload.contains("Eyes.close") || payload.contains("Eyes.abort") || payload.contains("EyesManager.closeAllEyes")) {
                    try {
                      ResponseDto<List<CommandCloseResponseDto>> closeResponse = objectMapper.readValue(payload,
                          new TypeReference<ResponseDto<List<CommandCloseResponseDto>>>() {});
                      map.put(closeResponse.getKey(), closeResponse);
                      syncTaskListener.onComplete(closeResponse);
                    } catch (Exception e) {
                      e.printStackTrace();
                    }
                  } else if (payload.contains("Eyes.extractTextRegions")) {
                    try {
                      ResponseDto<Map<String, List<TextRegion>>> extractTextRegionsResponse = objectMapper
                          .readValue(payload, new TypeReference<ResponseDto<Map<String, List<TextRegion>>>>() {});
                      map.put(extractTextRegionsResponse.getKey(), extractTextRegionsResponse);
                      syncTaskListener.onComplete(extractTextRegionsResponse);
                    } catch (Exception e) {
                      e.printStackTrace();
                    }
                  } else if (payload.contains("Eyes.extractText")) {
                    try {
                      ResponseDto<List<String>> responseDto = objectMapper.readValue(payload, new TypeReference<ResponseDto<List<String>>>() {});
                      map.put(responseDto.getKey(), responseDto);
                      syncTaskListener.onComplete(responseDto);
                    } catch (Exception e) {
                      e.printStackTrace();
                    }
                  } else if (payload.contains("Core.getViewportSize")) {
                    try {
                      ResponseDto<RectangleSizeDto> getViewportSizeResponse = objectMapper.readValue(payload, new TypeReference<ResponseDto<RectangleSizeDto>>() {
                      });
                      map.put(getViewportSizeResponse.getKey(), getViewportSizeResponse);
                      syncTaskListener.onComplete(getViewportSizeResponse);
                    } catch (Exception e) {
                      e.printStackTrace();
                    }
                  }
                }

                @Override
                public void onError(Throwable t) {
                }
              }).build()).get();

    } catch (Exception e) {
      throw new UnsupportedOperationException();
    }

  }


  public synchronized ResponseDto<?> executeCommand(Command command, boolean waitResult) throws Exception {
    if (command instanceof EventDto<?>) {
      System.out.println("MAKE_SDK: " + command);
      webSocket.sendTextFrame(objectMapper.writeValueAsString(command));
      return null;
    }

    RequestDto<?> request = (RequestDto<?>) command;

    map.put(request.getKey(), null);

    System.out.println("REQUEST: " + objectMapper.writeValueAsString(request));
    webSocket.sendTextFrame(objectMapper.writeValueAsString(request));
    syncTaskListener = new SyncTaskListener<>(logger, request.getKey());
    if (waitResult) syncTaskListener.get();
    return map.get(request.getKey());
  }

}
