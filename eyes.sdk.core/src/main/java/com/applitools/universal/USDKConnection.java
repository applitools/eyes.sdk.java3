package com.applitools.universal;

import java.util.HashMap;
import java.util.Map;

import com.applitools.eyes.Logger;
import com.applitools.eyes.SyncTaskListener;
import com.applitools.universal.dto.Command;
import com.applitools.universal.dto.EventDto;
import com.applitools.universal.dto.MatchResultDto;
import com.applitools.universal.dto.RequestDto;
import com.applitools.universal.dto.ResponseDto;
import com.google.gson.Gson;
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
  private SyncTaskListener<ResponseDto> syncTaskListener;

  private Map<String, ResponseDto> map;

  public USDKConnection() {
    webSocket = openWebsocket();
    map = new HashMap<>();
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
//                  if (payload.contains("Eyes.check")) {
//                    ResponseDto<MatchResultDto> responseDto = new Gson().fromJson(payload, ResponseDto.class);
//                    map.put(responseDto.getKey(), responseDto);
//                    syncTaskListener.onComplete(responseDto);
//                    return;
//                  }
                  System.out.println("PAYLOAD: " + payload);
                  ResponseDto response = new Gson().fromJson(payload, ResponseDto.class);
                  map.put(response.getKey(), response);
                  syncTaskListener.onComplete(response);
                }

                @Override
                public void onError(Throwable t) {
                }
              }).build()).get();

    } catch (Exception e) {
      throw new UnsupportedOperationException();
    }

  }

  public ResponseDto executeCommand(Command command, boolean waitResult) {
    Gson gson = new Gson();
    if (command instanceof EventDto<?>) {
      webSocket.sendTextFrame(gson.toJson(command));
      return null;
    }

    RequestDto<?> request = (RequestDto<?>) command;

    map.put(request.getKey(), null);

    webSocket.sendTextFrame(gson.toJson(request));
    syncTaskListener = new SyncTaskListener<>(logger, request.getKey());
    if (waitResult) syncTaskListener.get();
    return map.get(request.getKey());
  }

}
