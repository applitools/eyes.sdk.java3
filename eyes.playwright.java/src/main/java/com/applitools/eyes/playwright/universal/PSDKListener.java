package com.applitools.eyes.playwright.universal;

import com.applitools.eyes.EyesException;
import com.applitools.eyes.logging.Stage;
import com.applitools.eyes.logging.TraceLevel;
import com.applitools.eyes.playwright.universal.driver.SpecDriverPlaywright;
import com.applitools.eyes.playwright.universal.driver.dto.DriverCommandDto;
import com.applitools.eyes.playwright.universal.driver.dto.DriverInfoDto;
import com.applitools.eyes.playwright.universal.dto.Context;
import com.applitools.eyes.playwright.universal.dto.Element;
import com.applitools.eyes.universal.AbstractSDKListener;
import com.applitools.eyes.universal.Reference;
import com.applitools.eyes.universal.driver.ICookie;
import com.applitools.eyes.universal.dto.*;
import com.fasterxml.jackson.core.type.TypeReference;
import org.asynchttpclient.ws.WebSocket;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

public class PSDKListener extends AbstractSDKListener {

    private static volatile PSDKListener instance;

    /**
     * refer
     */
    private final Refer ref = new Refer();

    /**
     * spec driver
     */
    private final SpecDriverPlaywright specDriver;

    public static PSDKListener getInstance() {
        if (instance == null) {
            synchronized (AbstractSDKListener.class) {
                if (instance == null) {
                    instance = new PSDKListener();
                }
            }
        }
        return instance;
    }

    private PSDKListener() {
        super();
        specDriver = new SpecDriverPlaywright(ref);
    }

    @Override
    public Refer getRefer() {
        return ref;
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

            switch(response.getName()) {
                case "Driver.getDriverInfo":
                    ResponseDto<?> getDriverInfoResponse = new ResponseDto<>();
                    getDriverInfoResponse.setName(response.getName());
                    try {
                        DriverCommandDto target = objectMapper.readValue(payload,
                                new TypeReference<RequestDto<DriverCommandDto>>() {
                                }).getPayload();
                        DriverInfoDto driverInfo = specDriver.getDriverInfo(target.getDriver());
                        getDriverInfoResponse.setPayload(new ResponsePayload(driverInfo, null));
                    } catch (Exception e) {
                        ErrorDto err = new ErrorDto(e.getMessage(), Arrays.toString(e.getStackTrace()),"spec-driver",null);
                        getDriverInfoResponse.setPayload(new ResponsePayload<>(null, err));
                    }

                    getDriverInfoResponse.setKey(response.getKey());
                    webSocket.sendTextFrame(objectMapper.writeValueAsString(getDriverInfoResponse));
                    break;
                case "Driver.executeScript":
                    ResponseDto<?> executeScriptResponse = new ResponseDto<>();
                    executeScriptResponse.setName(response.getName());
                    try {
                        DriverCommandDto dto = objectMapper.readValue(payload,
                                new TypeReference<RequestDto<DriverCommandDto>>() {
                                }).getPayload();
                        Object handleObject = specDriver.executeScript(dto.getContext(), dto.getScript(), dto.getArg());
                        executeScriptResponse.setPayload(new ResponsePayload(handleObject, null));
                    } catch (Exception e) {
                        ErrorDto err = new ErrorDto(e.getMessage(), Arrays.toString(e.getStackTrace()),"spec-driver",null);
                        executeScriptResponse.setPayload(new ResponsePayload<>(null, err));
                    }

                    executeScriptResponse.setKey(response.getKey());
                    webSocket.sendTextFrame(objectMapper.writeValueAsString(executeScriptResponse));
                    break;
                case "Driver.getViewportSize":
                    ResponseDto<?> getViewportSizeResponse = new ResponseDto<>();
                    getViewportSizeResponse.setName(response.getName());
                    try {
                        DriverCommandDto target = objectMapper.readValue(payload,
                                new TypeReference<RequestDto<DriverCommandDto>>() {
                                }).getPayload();
                        RectangleSizeDto viewportSize = specDriver.getViewportSize(target.getDriver());
                        getViewportSizeResponse.setPayload(new ResponsePayload(viewportSize, null));
                    } catch (Exception e) {
                        ErrorDto err = new ErrorDto(e.getMessage(), Arrays.toString(e.getStackTrace()),"spec-driver",null);
                        getViewportSizeResponse.setPayload(new ResponsePayload<>(null, err));
                    }

                    getViewportSizeResponse.setKey(response.getKey());
                    webSocket.sendTextFrame(objectMapper.writeValueAsString(getViewportSizeResponse));
                    break;
                case "Driver.setViewportSize":
                    ResponseDto<?> setViewportSize = new ResponseDto<>();
                    setViewportSize.setName(response.getName());
                    try {
                        DriverCommandDto target = objectMapper.readValue(payload,
                                new TypeReference<RequestDto<DriverCommandDto>>() {
                                }).getPayload();
                        specDriver.setViewportSize(target.getDriver(), target.getSize());
                        setViewportSize.setPayload(new ResponsePayload("complete", null));
                    } catch (Exception e) {
                        ErrorDto err = new ErrorDto(e.getMessage(), Arrays.toString(e.getStackTrace()),"spec-driver",null);
                        setViewportSize.setPayload(new ResponsePayload<>(null, err));
                    }

                    setViewportSize.setKey(response.getKey());
                    webSocket.sendTextFrame(objectMapper.writeValueAsString(setViewportSize));
                    break;
                case "Driver.findElement":
                    ResponseDto<?> findElement = new ResponseDto<>();
                    findElement.setName(response.getName());
                    try {
                        DriverCommandDto target = objectMapper.readValue(payload,
                                new TypeReference<RequestDto<DriverCommandDto>>() {
                                }).getPayload();
                        Element element = specDriver.findElement(target.getContext(), target.getSelector(), target.getParent());
                        findElement.setPayload(new ResponsePayload(element, null));
                    } catch (Exception e) {
                        ErrorDto err = new ErrorDto(e.getMessage(), Arrays.toString(e.getStackTrace()),"spec-driver",null);
                        findElement.setPayload(new ResponsePayload<>(null, err));
                    }

                    findElement.setKey(response.getKey());
                    webSocket.sendTextFrame(objectMapper.writeValueAsString(findElement));
                    break;
                case "Driver.findElements":
                    ResponseDto<?> findElements = new ResponseDto<>();
                    findElements.setName(response.getName());
                    try {
                        DriverCommandDto target = objectMapper.readValue(payload,
                                new TypeReference<RequestDto<DriverCommandDto>>() {
                                }).getPayload();
                        List<Reference> elements = specDriver.findElements(target.getContext(), target.getSelector(), target.getParent());
                        findElements.setPayload(new ResponsePayload(elements, null));
                    } catch (Exception e) {
                        ErrorDto err = new ErrorDto(e.getMessage(), Arrays.toString(e.getStackTrace()),"spec-driver",null);
                        findElements.setPayload(new ResponsePayload<>(null, err));
                    }

                    findElements.setKey(response.getKey());
                    webSocket.sendTextFrame(objectMapper.writeValueAsString(findElements));
                    break;
                case "Driver.takeScreenshot":
                    ResponseDto<?> takeScreenshot = new ResponseDto<>();
                    takeScreenshot.setName(response.getName());
                    try {
                        DriverCommandDto target = objectMapper.readValue(payload,
                                new TypeReference<RequestDto<DriverCommandDto>>() {
                                }).getPayload();
                        byte[] screenshot = (byte[]) specDriver.takeScreenshot(target.getDriver());
                        takeScreenshot.setPayload(new ResponsePayload(Base64.getEncoder().encodeToString(screenshot), null));
                    } catch (Exception e) {
                        ErrorDto err = new ErrorDto(e.getMessage(), Arrays.toString(e.getStackTrace()),"spec-driver",null);
                        takeScreenshot.setPayload(new ResponsePayload<>(null, err));
                    }

                    takeScreenshot.setKey(response.getKey());
                    webSocket.sendTextFrame(objectMapper.writeValueAsString(takeScreenshot));
                    break;
                case "Driver.getTitle":
                    ResponseDto<?> getTitle = new ResponseDto<>();
                    getTitle.setName(response.getName());
                    try {
                        DriverCommandDto target = objectMapper.readValue(payload,
                                new TypeReference<RequestDto<DriverCommandDto>>() {
                                }).getPayload();
                        String title = specDriver.getTitle(target.getDriver());
                        getTitle.setPayload(new ResponsePayload(title, null));
                    } catch (Exception e) {
                        ErrorDto err = new ErrorDto(e.getMessage(), Arrays.toString(e.getStackTrace()),"spec-driver",null);
                        getTitle.setPayload(new ResponsePayload<>(null, err));
                    }

                    getTitle.setKey(response.getKey());
                    webSocket.sendTextFrame(objectMapper.writeValueAsString(getTitle));
                    break;
                case "Driver.getUrl":
                    ResponseDto<?> getUrl = new ResponseDto<>();
                    getUrl.setName(response.getName());
                    try {
                        DriverCommandDto target = objectMapper.readValue(payload,
                                new TypeReference<RequestDto<DriverCommandDto>>() {
                                }).getPayload();
                        String url = specDriver.getUrl(target.getDriver());
                        getUrl.setPayload(new ResponsePayload(url, null));
                    } catch (Exception e) {
                        ErrorDto err = new ErrorDto(e.getMessage(), Arrays.toString(e.getStackTrace()),"spec-driver",null);
                        getUrl.setPayload(new ResponsePayload<>(null, err));
                    }

                    getUrl.setKey(response.getKey());
                    webSocket.sendTextFrame(objectMapper.writeValueAsString(getUrl));
                    break;
                case "Driver.visit":
                    ResponseDto<?> visit = new ResponseDto<>();
                    visit.setName(response.getName());
                    try {
                        DriverCommandDto target = objectMapper.readValue(payload,
                                new TypeReference<RequestDto<DriverCommandDto>>() {
                                }).getPayload();
                        specDriver.visit(target.getDriver(), target.getUrl());
                        visit.setPayload(new ResponsePayload("complete", null));
                    } catch (Exception e) {
                        ErrorDto err = new ErrorDto(e.getMessage(), Arrays.toString(e.getStackTrace()),"spec-driver",null);
                        visit.setPayload(new ResponsePayload<>(null, err));
                    }

                    visit.setKey(response.getKey());
                    webSocket.sendTextFrame(objectMapper.writeValueAsString(visit));
                    break;
                case "Driver.getCookies":
                    ResponseDto<?> getCookies = new ResponseDto<>();
                    getCookies.setName(response.getName());
                    try {
                        DriverCommandDto target = objectMapper.readValue(payload,
                                new TypeReference<RequestDto<DriverCommandDto>>() {
                                }).getPayload();
                        List<ICookie> cookies = specDriver.getCookies(target.getDriver(), target.getContext());
                        getCookies.setPayload(new ResponsePayload(cookies.toString(), null));
                    } catch (Exception e) {
                        ErrorDto err = new ErrorDto(e.getMessage(), Arrays.toString(e.getStackTrace()),"spec-driver",null);
                        getCookies.setPayload(new ResponsePayload<>(null, err));
                    }

                    getCookies.setKey(response.getKey());
                    webSocket.sendTextFrame(objectMapper.writeValueAsString(getCookies));
                    break;
                case "Driver.childContext":
                    ResponseDto<?> childContext = new ResponseDto<>();
                    childContext.setName(response.getName());
                    try {
                        DriverCommandDto target = objectMapper.readValue(payload,
                                new TypeReference<RequestDto<DriverCommandDto>>() {
                                }).getPayload();
                        Context context = specDriver.childContext(target.getContext(), target.getElement());
                        childContext.setPayload(new ResponsePayload(context, null));
                    } catch (Exception e) {
                        ErrorDto err = new ErrorDto(e.getMessage(), Arrays.toString(e.getStackTrace()),"spec-driver",null);
                        childContext.setPayload(new ResponsePayload<>(null, err));
                    }

                    childContext.setKey(response.getKey());
                    webSocket.sendTextFrame(objectMapper.writeValueAsString(childContext));
                    break;
                case "Driver.mainContext":
                    ResponseDto<?> mainContext = new ResponseDto<>();
                    mainContext.setName(response.getName());
                    try {
                        DriverCommandDto target = objectMapper.readValue(payload,
                                new TypeReference<RequestDto<DriverCommandDto>>() {
                                }).getPayload();
                        Context context = specDriver.mainContext(target.getContext());
                        mainContext.setPayload(new ResponsePayload(context, null));
                    } catch (Exception e) {
                        ErrorDto err = new ErrorDto(e.getMessage(), Arrays.toString(e.getStackTrace()),"spec-driver",null);
                        mainContext.setPayload(new ResponsePayload<>(null, err));
                    }

                    mainContext.setKey(response.getKey());
                    webSocket.sendTextFrame(objectMapper.writeValueAsString(mainContext));
                    break;
                case "Driver.parentContext":
                    ResponseDto<?> parentContext = new ResponseDto<>();
                    parentContext.setName(response.getName());
                    try {
                        DriverCommandDto target = objectMapper.readValue(payload,
                                new TypeReference<RequestDto<DriverCommandDto>>() {
                                }).getPayload();
                        Context context = specDriver.parentContext(target.getContext());
                        parentContext.setPayload(new ResponsePayload(context, null));
                    } catch (Exception e) {
                        ErrorDto err = new ErrorDto(e.getMessage(), Arrays.toString(e.getStackTrace()),"spec-driver",null);
                        parentContext.setPayload(new ResponsePayload<>(null, err));
                    }

                    parentContext.setKey(response.getKey());
                    webSocket.sendTextFrame(objectMapper.writeValueAsString(parentContext));
                    break;

                case "Core.makeManager":
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
}
