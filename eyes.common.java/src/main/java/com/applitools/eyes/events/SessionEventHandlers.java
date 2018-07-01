package com.applitools.eyes.events;

import com.applitools.eyes.RectangleSize;
import com.applitools.eyes.TestResults;

import java.util.ArrayList;
import java.util.List;

public class SessionEventHandlers implements ISessionEventHandler {
    private List<ISessionEventHandler> eventHandlers = new ArrayList<>();

    public void addEventHandler(ISessionEventHandler handler){
        if (handler == this) { return; }
        eventHandlers.add(handler);
    }

    public void removeEventHandler(ISessionEventHandler handler) {
        if (handler == this) { return; }
        eventHandlers.remove(handler);
    }

    public void clearEventHandlers() {
        eventHandlers.clear();
    }

    @Override
    public void initStarted() {
        for (ISessionEventHandler currentHandler : eventHandlers) currentHandler.initStarted();
    }

    @Override
    public void initEnded() {
        for (ISessionEventHandler currentHandler : eventHandlers) currentHandler.initEnded();
    }

    @Override
    public void setSizeWillStart(RectangleSize sizeToSet) {
        for (ISessionEventHandler currentHandler : eventHandlers) currentHandler.setSizeWillStart(sizeToSet);
    }

    @Override
    public void setSizeEnded() {
        for (ISessionEventHandler currentHandler : eventHandlers) currentHandler.setSizeEnded();
    }

    @Override
    public void testStarted(String autSessionId) {
        for (ISessionEventHandler currentHandler : eventHandlers) currentHandler.testStarted(autSessionId);
    }

    @Override
    public void testEnded(String autSessionId, TestResults testResults) {
        for (ISessionEventHandler currentHandler : eventHandlers) currentHandler.testEnded(autSessionId, testResults);
    }

    @Override
    public void validationWillStart(String autSessionId, ValidationInfo validationInfo) {
        for (ISessionEventHandler currentHandler : eventHandlers) currentHandler.validationWillStart(autSessionId, validationInfo);
    }

    @Override
    public void validationEnded(String autSessionId, String validationId, ValidationResult validationResult) {
        for (ISessionEventHandler currentHandler : eventHandlers) currentHandler.validationEnded(autSessionId, validationId, validationResult);
    }

}
