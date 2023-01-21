package com.applitools.eyes.universal;

import com.applitools.eyes.Logger;
import com.applitools.eyes.Region;
import com.applitools.eyes.SyncTaskListener;
import com.applitools.eyes.locators.TextRegion;
import com.applitools.eyes.logging.Stage;
import com.applitools.eyes.logging.TraceLevel;
import com.applitools.eyes.universal.dto.*;
import com.applitools.eyes.universal.dto.response.CommandCloseResponseDto;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.asynchttpclient.ws.WebSocket;
import org.asynchttpclient.ws.WebSocketListener;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Universal server socket listener
 */
public class USDKListener implements WebSocketListener {
    protected Logger logger = new Logger();
    protected ObjectMapper objectMapper;
    protected Map<String, SyncTaskListener<ResponseDto<?>>> map;
    protected WebSocket webSocket;

    protected static final Map<String, TypeReference<?>> typeReferences = new HashMap<String, TypeReference<?>>(){{
        put("Core.makeManager", new TypeReference<ResponseDto<Reference>>() {});
        put("EyesManager.openEyes", new TypeReference<ResponseDto<Reference>>() {});
        put("Eyes.check", new TypeReference<ResponseDto<List<MatchResultDto>>>() {});
        put("Core.locate", new TypeReference<ResponseDto<Map<String, List<Region>>>>() {});
        put("Eyes.close", new TypeReference<ResponseDto<List<CommandCloseResponseDto>>>() {});
        put("Eyes.abort", new TypeReference<ResponseDto<List<CommandCloseResponseDto>>>() {});
        put("EyesManager.closeAllEyes", new TypeReference<ResponseDto<List<CommandCloseResponseDto>>>() {});
        put("Eyes.locateText", new TypeReference<ResponseDto<Map<String, List<TextRegion>>>>() {});
        put("Eyes.extractText", new TypeReference<ResponseDto<List<String>>>() {});
        put("Core.getViewportSize", new TypeReference<ResponseDto<RectangleSizeDto>>() {});
        put("EyesManager.closeManager", new TypeReference<ResponseDto<TestResultsSummaryDto>>() {});
        put("Debug.getHistory", new TypeReference<ResponseDto<DebugHistoryDto>>() {});
        put("Core.deleteTest", new TypeReference<ResponseDto>() {});
        put("Core.closeBatch", new TypeReference<ResponseDto>() {});
    }};

    public USDKListener() {
        map = new HashMap<>();
        objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
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

    public void setWebSocket(WebSocket webSocket) {
        this.webSocket = webSocket;
    }

    protected void handleResponse(String payload, TypeReference<?> typeReference) {
        try {
            ResponseDto<?> responseDto = (ResponseDto<?>) objectMapper.readValue(payload, typeReference);

            SyncTaskListener<ResponseDto<?>> syncTaskListener = map.get(responseDto.getKey());
            syncTaskListener.onComplete(responseDto);
            map.remove(responseDto.getKey());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Refer getRefer() {
        return null;
    }
}
