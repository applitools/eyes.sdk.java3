package com.applitools.eyes.events;

import com.applitools.eyes.RectangleSize;
import com.applitools.eyes.TestResults;

public interface ISessionEventHandler {

    /**
     * Called when the model gathering for creating a session phase had started.
     */
    void initStarted();

    /**
     * Called when the model gathering phase had ended.
     */
    void initEnded();

    /**
     * Called when setting the size of the application window is about to start.
     * @param sizeToSet the size of the window.
     */
    void setSizeWillStart(RectangleSize sizeToSet);

    /**
     * Called 'set size' operation has ended (either failed/success).
     */
    void setSizeEnded();

    /**
     * Called after a session had started.
     * @param autSessionId The AUT session ID.
     */
    void testStarted(String autSessionId);

    /**
     * Called after a session had ended.
     * @param autSessionId The AUT session ID.
     * @param testResults  The test results.
     */
    void testEnded(String autSessionId, TestResults testResults);

    /**
     * Called before a new validation will be started.
     * @param autSessionId   The AUT session ID.
     * @param validationInfo The validation parameters.
     */
    void validationWillStart(String autSessionId, ValidationInfo validationInfo);

    /**
     * Called when a validation had ended.
     * @param autSessionId     The AUT session ID.
     * @param validationId     The ID of the validation which had ended.
     * @param validationResult The validation results.
     */
    void validationEnded(String autSessionId, String validationId, ValidationResult validationResult);
}
