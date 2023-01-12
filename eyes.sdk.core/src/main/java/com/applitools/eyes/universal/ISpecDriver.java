package com.applitools.eyes.universal;

import com.applitools.eyes.universal.driver.*;
import com.applitools.eyes.universal.dto.*;

import java.util.List;
import java.util.Map;

public interface ISpecDriver {

    //region UTILITY
    default Boolean isDriver(Object driver) {
        throw new UnsupportedOperationException("isDriver is not supported");
    }

    default Boolean isContext(Reference context) {
        throw new UnsupportedOperationException("isContext is not supported");
    }

    default Boolean isElement(Reference element) {
        throw new UnsupportedOperationException("isElement is not supported");
    }

    default Boolean isSelector(Reference selector) {
        throw new UnsupportedOperationException("isSelector is not supported");
    }

    default Boolean isEqualElements() {
        throw new UnsupportedOperationException("isEqualElements is not supported");
    }
    //endregion

    //region COMMANDS
    default Reference mainContext(Reference context) {
        throw new UnsupportedOperationException("mainContext is not supported");
    }

    default Reference parentContext() {
        throw new UnsupportedOperationException("parentContext is not supported");
    }

    default Reference childContext(Reference context, Reference element) {
        throw new UnsupportedOperationException("childContext is not supported");
    }

    default Object executeScript(Reference context, String script, Object arg) {
        throw new UnsupportedOperationException("executeScript is not supported");
    }

    default Reference findElement(Reference driver, Reference selector, Reference parent) {
        throw new UnsupportedOperationException("findElement is not supported");
    }

    default Reference[] findElements() {
        throw new UnsupportedOperationException("findElements is not supported");
    }

    default void setElementText() {
        throw new UnsupportedOperationException("setElementText is not supported");
    }

    default String getElementText() {
        throw new UnsupportedOperationException("getElementText is not supported");
    }

    default void setWindowSize(Reference driver, RectangleSizeDto windowSize) {
        throw new UnsupportedOperationException("setWindowSize is not supported");
    }

    default RectangleSizeDto getWindowSize(Reference driver) {
        throw new UnsupportedOperationException("getWindowSize is not supported");
    }

    default void setViewportSize(Reference driver, RectangleSizeDto windowSize) {
        throw new UnsupportedOperationException("setViewportSize is not supported");
    }

    default RectangleSizeDto getViewportSize(Reference driver) {
        throw new UnsupportedOperationException("getViewportSize is not supported");
    }

    default List<ICookie> getCookies(Reference driver, Reference context) {
        throw new UnsupportedOperationException("getCookies is not supported");
    }

    default Object getDriverInfo(Reference driver) {
        throw new UnsupportedOperationException("getDriverInfo is not supported");
    }

    default Map<String, Object> getCapabilities(Reference driver) {
        throw new UnsupportedOperationException("getCapabilities is not supported");
    }

    default String getTitle(Reference driver) {
        throw new UnsupportedOperationException("getTitle is not supported");
    }

    default String getUrl(Reference driver) {
        throw new UnsupportedOperationException("getUrl is not supported");
    }

    default Object takeScreenshot(Reference driver) {
        throw new UnsupportedOperationException("takeScreenshot is not supported");
    }

    default void click() {
        throw new UnsupportedOperationException("click is not supported");
    }

    default void visit(Reference driver, String url) {
        throw new UnsupportedOperationException("visit is not supported");
    }
    //endregion COMMANDS
}
