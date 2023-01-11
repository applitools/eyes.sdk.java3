package com.applitools.eyes.playwright.universal;

import com.applitools.eyes.Region;
import com.applitools.eyes.SyncTaskListener;
import com.applitools.eyes.locators.TextRegion;
import com.applitools.eyes.logging.Stage;
import com.applitools.eyes.logging.TraceLevel;
import com.applitools.eyes.playwright.universal.driver.Element;
import com.applitools.eyes.playwright.universal.driver.SpecDriverPlaywright;
import com.applitools.eyes.playwright.universal.driver.dto.DriverCommandDto;
import com.applitools.eyes.playwright.universal.driver.dto.DriverInfoDto;
import com.applitools.eyes.universal.Reference;
import com.applitools.eyes.universal.USDKListener;
import com.applitools.eyes.universal.driver.ICookie;
import com.applitools.eyes.universal.dto.*;
import com.applitools.eyes.universal.dto.response.CommandCloseResponseDto;
import com.fasterxml.jackson.core.type.TypeReference;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.Map;

public class PSDKListener extends USDKListener {

    /**
     * spec driver
     */
    private final SpecDriverPlaywright specDriver;

    /**
     * refer
     */
    private Refer ref;

    public PSDKListener() {
        super();
        ref = new Refer();
        specDriver = new SpecDriverPlaywright(ref);
    }

    public Refer getRef() {
        return ref;
    }

    public SpecDriverPlaywright getSpecDriver() {
        return specDriver;
    }

    @Override
    public void onTextFrame(String payload, boolean finalFragment, int rsv) {

        try {
            ResponseDto response = objectMapper.readValue(payload, ResponseDto.class);

            switch(response.getName()) {
//                case "Driver.getCapabilities":
//                    ResponseDto<?> getCapabilitiesResponse = new ResponseDto<>();
//                    getCapabilitiesResponse.setName(response.getName());
//                    try {
//                        DriverCommandDto target = objectMapper.readValue(payload,
//                                new TypeReference<RequestDto<DriverCommandDto>>() {
//                                }).getPayload();
//                        Map<String, Object> capabilities = specDriver.getCapabilities(target.getDriver());
//                        getCapabilitiesResponse.setPayload(new ResponsePayload(capabilities, null));
//                    } catch (Exception e) {
//                        ErrorDto err = new ErrorDto(e.getMessage(), Arrays.toString(e.getStackTrace()),"internal",null);
//                        getCapabilitiesResponse.setPayload(new ResponsePayload<>(null, err));
//                    }
//
//                    getCapabilitiesResponse.setKey(response.getKey());
//                    webSocket.sendTextFrame(objectMapper.writeValueAsString(getCapabilitiesResponse));
//                    break;
                case "Driver.getDriverInfo":
                    ResponseDto<?> getDriverInfoResponse = new ResponseDto<>();
                    getDriverInfoResponse.setName(response.getName());
                    try {
                        DriverCommandDto target = objectMapper.readValue(payload,
                                new TypeReference<RequestDto<DriverCommandDto>>() {
                                }).getPayload();
                        DriverInfoDto driverInfo = (DriverInfoDto) specDriver.getDriverInfo(target.getDriver());
                        getDriverInfoResponse.setPayload(new ResponsePayload(driverInfo, null));
                    } catch (Exception e) {
                        ErrorDto err = new ErrorDto(e.getMessage(), Arrays.toString(e.getStackTrace()),"internal",null);
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
                        ErrorDto err = new ErrorDto(e.getMessage(), Arrays.toString(e.getStackTrace()),"internal",null);
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
                        ErrorDto err = new ErrorDto(e.getMessage(), Arrays.toString(e.getStackTrace()),"internal",null);
                        getViewportSizeResponse.setPayload(new ResponsePayload<>(null, err));
                    }

                    getViewportSizeResponse.setKey(response.getKey());
                    webSocket.sendTextFrame(objectMapper.writeValueAsString(getViewportSizeResponse));
                    break;
//                case "Driver.getWindowSize":
//                    ResponseDto<?> getWindowSize = new ResponseDto<>();
//                    getWindowSize.setName(response.getName());
//                    try {
//                        DriverCommandDto target = objectMapper.readValue(payload,
//                                new TypeReference<RequestDto<DriverCommandDto>>() {
//                                }).getPayload();
//                        RectangleSizeDto viewportSize = specDriver.getWindowSize(target.getDriver());
//                        getWindowSize.setPayload(new ResponsePayload(viewportSize, null));
//                    } catch (Exception e) {
//                        ErrorDto err = new ErrorDto(e.getMessage(), Arrays.toString(e.getStackTrace()),"internal",null);
//                        getWindowSize.setPayload(new ResponsePayload<>(null, err));
//                    }
//
//                    getWindowSize.setKey(response.getKey());
//                    webSocket.sendTextFrame(objectMapper.writeValueAsString(getWindowSize));
//                    break;
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
                        ErrorDto err = new ErrorDto(e.getMessage(), Arrays.toString(e.getStackTrace()),"internal",null);
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
                        Element element = (Element) specDriver.findElement(target.getContext(), target.getSelector(), target.getParent());
                        findElement.setPayload(new ResponsePayload(element, null));
                    } catch (Exception e) {
                        ErrorDto err = new ErrorDto(e.getMessage(), Arrays.toString(e.getStackTrace()),"internal",null);
                        findElement.setPayload(new ResponsePayload<>(null, err));
                    }

                    findElement.setKey(response.getKey());
                    webSocket.sendTextFrame(objectMapper.writeValueAsString(findElement));
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
                        ErrorDto err = new ErrorDto(e.getMessage(), Arrays.toString(e.getStackTrace()),"internal",null);
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
                        ErrorDto err = new ErrorDto(e.getMessage(), Arrays.toString(e.getStackTrace()),"internal",null);
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
                        ErrorDto err = new ErrorDto(e.getMessage(), Arrays.toString(e.getStackTrace()),"internal",null);
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
                        ErrorDto err = new ErrorDto(e.getMessage(), Arrays.toString(e.getStackTrace()),"internal",null);
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
                        ErrorDto err = new ErrorDto(e.getMessage(), Arrays.toString(e.getStackTrace()),"internal",null);
                        getCookies.setPayload(new ResponsePayload<>(null, err));
                    }

                    getCookies.setKey(response.getKey());
                    webSocket.sendTextFrame(objectMapper.writeValueAsString(getCookies));
                    break;
            }

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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
