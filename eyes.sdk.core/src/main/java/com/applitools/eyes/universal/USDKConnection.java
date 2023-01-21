package com.applitools.eyes.universal;

import com.applitools.eyes.EyesException;
import com.applitools.eyes.Logger;
import com.applitools.eyes.SyncTaskListener;
import com.applitools.eyes.universal.dto.Command;
import com.applitools.eyes.universal.dto.EventDto;
import com.applitools.eyes.universal.dto.RequestDto;
import com.applitools.eyes.universal.dto.ResponseDto;
import com.applitools.eyes.universal.server.UniversalSdkNativeLoader;
import org.asynchttpclient.DefaultAsyncHttpClientConfig;
import org.asynchttpclient.Dsl;
import org.asynchttpclient.ws.WebSocket;
import org.asynchttpclient.ws.WebSocketUpgradeHandler;

/**
 * universal sdk connection
 */
public class USDKConnection {
  private WebSocket webSocket;
  protected Logger logger = new Logger();
  private USDKListener listener;

  public USDKConnection(USDKListener listener) {
    this.listener = listener;
  }

  public void init() {
   webSocket = openWebsocket();
   listener.setWebSocket(webSocket);
  }

  private WebSocket openWebsocket() {

    try {

      String url = String.format("ws://localhost:%s/eyes", UniversalSdkNativeLoader.getPort());
      return Dsl.asyncHttpClient(new DefaultAsyncHttpClientConfig.Builder().setWebSocketMaxFrameSize(268435456)).prepareGet(url)
          .execute(new WebSocketUpgradeHandler.Builder().addWebSocketListener(listener).build()).get();

    } catch (Exception e) {
      throw new EyesException("Communication with server failed, reason: " + e.getMessage(), e);
    }

  }

  public synchronized SyncTaskListener executeCommand(Command command) throws Exception {
    if (command instanceof EventDto<?>) {
      webSocket.sendTextFrame(listener.objectMapper.writeValueAsString(command));
      return null;
    }

    RequestDto<?> request = (RequestDto<?>) command;
    SyncTaskListener<ResponseDto<?>> syncTaskListener = new SyncTaskListener<>(logger, request.getKey());
    listener.map.put(request.getKey(), syncTaskListener);
    webSocket.sendTextFrame(listener.objectMapper.writeValueAsString(request));
    return syncTaskListener;
  }

}
