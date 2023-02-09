package com.applitools.eyes.universal;

import com.applitools.eyes.Logger;
import com.applitools.eyes.Region;
import com.applitools.eyes.SyncTaskListener;
import com.applitools.eyes.locators.TextRegion;
import com.applitools.eyes.universal.dto.*;
import com.applitools.eyes.universal.dto.response.CommandEyesGetResultsResponseDto;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.asynchttpclient.ws.WebSocket;
import org.asynchttpclient.ws.WebSocketListener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class AbstractSDKListener implements WebSocketListener {

    protected Logger logger = new Logger();
    protected ObjectMapper objectMapper;
    protected Map<String, SyncTaskListener<ResponseDto<?>>> map;
    protected WebSocket webSocket;

    protected static final Map<String, TypeReference<?>> typeReferences = new HashMap<String, TypeReference<?>>(){{
        put("Core.makeManager", new TypeReference<ResponseDto<Reference>>() {});
        put("EyesManager.openEyes", new TypeReference<ResponseDto<Reference>>() {});
        put("Eyes.check", new TypeReference<ResponseDto<List<MatchResultDto>>>() {});
        put("Core.locate", new TypeReference<ResponseDto<Map<String, List<Region>>>>() {});
        put("Eyes.getResults", new TypeReference<ResponseDto<List<CommandEyesGetResultsResponseDto>>>() {});
        put("Eyes.close", new TypeReference<ResponseDto>() {});
        put("Eyes.abort", new TypeReference<ResponseDto>() {});
        put("Eyes.locateText", new TypeReference<ResponseDto<Map<String, List<TextRegion>>>>() {});
        put("Eyes.extractText", new TypeReference<ResponseDto<List<String>>>() {});
        put("Core.getViewportSize", new TypeReference<ResponseDto<RectangleSizeDto>>() {});
        put("EyesManager.getResults", new TypeReference<ResponseDto<TestResultsSummaryDto>>() {});
        put("Debug.getHistory", new TypeReference<ResponseDto<DebugHistoryDto>>() {});
        put("Core.deleteTest", new TypeReference<ResponseDto>() {});
        put("Core.closeBatch", new TypeReference<ResponseDto>() {});
        put("Core.makeECClient", new TypeReference<ResponseDto<MakeECClientResponsePayload>>() {});
    }};

    public AbstractSDKListener() {
        map = new HashMap<>();
        objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
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

    protected void setWebSocket(WebSocket webSocket) {
        this.webSocket = webSocket;
    }

    public abstract Refer getRefer();
}
