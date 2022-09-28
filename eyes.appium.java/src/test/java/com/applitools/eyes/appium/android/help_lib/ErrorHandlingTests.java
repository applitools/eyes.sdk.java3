package com.applitools.eyes.appium.android.help_lib;

import org.testng.Assert;
import org.testng.annotations.Test;

public class ErrorHandlingTests extends HelpLibTestSetup{

    @Override
    protected MainMenuButton getMainMenuButton() {
        return null;
    }

    @Test
    public void testUnknownError() throws InterruptedException {
        sendEDTCommand("UNKNOWN;1;2;3;4;5;6");
        String value = getEDTValue();

        Assert.assertEquals(value,"error;Unknown command: UNKNOWN;1;2;3;4;5;6");
    }
    @Test
    public void testInvalidScrollable() throws InterruptedException {
        sendEDTCommand("register_scrollable;invalid_id;0;0;0;0;0");
        String value = getEDTValue();

        Assert.assertEquals(value,"error;registerScrollable view must not be NULL");
    }
}
