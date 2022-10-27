package com.applitools.eyes.images.universal.mapper;

import com.applitools.eyes.exceptions.StaleElementReferenceException;

public class ImageStaleElementReferenceException extends StaleElementReferenceException {

    @Override
    public void throwException(String message) {
        throw new RuntimeException(message);
    }
}