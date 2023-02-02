package com.applitools.eyes.universal;

import com.applitools.eyes.EyesException;
import com.applitools.eyes.logging.Stage;
import com.applitools.eyes.logging.TraceLevel;
import com.applitools.eyes.universal.dto.LogResponseDto;
import com.applitools.eyes.universal.dto.ResponseDto;
import com.fasterxml.jackson.core.type.TypeReference;
import org.asynchttpclient.ws.WebSocket;
import org.asynchttpclient.ws.WebSocketListener;

import java.sql.Timestamp;

/**
 * Universal server socket listener
 */
public class USDKListener extends AbstractSDKListener implements WebSocketListener {

    private static volatile USDKListener instance;

    public static USDKListener getInstance() {
        if (instance == null) {
            synchronized (AbstractSDKListener.class) {
                if (instance == null) {
                    instance = new USDKListener();
                }
            }
        }
        return instance;
    }

    private USDKListener() {
        super();
    }

    @Override
    public void onOpen(WebSocket webSocket) {

    }

    @Override
    public void onClose(WebSocket webSocket, int i, String s) {

    }

    @Override
    public void onError(Throwable throwable) {

    }

    @Override
    public void onTextFrame(String payload, boolean finalFragment, int rsv) {
        try {
            ResponseDto response = objectMapper.readValue(payload, ResponseDto.class);

            switch (response.getName()) {

                case "Core.makeManager":
                case "Core.makeECClient":
                case "EyesManager.openEyes":
                case "Eyes.check":
                case "Core.locate":
                case "Eyes.close":
                case "Eyes.abort":
                case "EyesManager.closeAllEyes":
                case "Eyes.locateText":
                case "Eyes.extractText":
                case "Core.getViewportSize":
                case "EyesManager.closeManager":
                case "Debug.getHistory":
                case "Core.deleteTest":
                case "Core.closeBatch":
                    handleResponse(payload, typeReferences.get(response.getName()));
                    break;
                case "Server.log":
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
                    break;

                default:
                    throw new EyesException("Unknown server command " + response.getName());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Refer getRefer() {
        return null;
    }
}
