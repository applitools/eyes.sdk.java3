/*
 * Applitools SDK for Selenium integration.
 */
package com.applitools.eyes.selenium.triggers;

import com.applitools.eyes.Logger;
import com.applitools.eyes.Region;
import com.applitools.eyes.selenium.wrappers.EyesRemoteWebElement;
import com.applitools.eyes.selenium.wrappers.EyesWebDriver;
import com.applitools.utils.ArgumentGuard;
import org.openqa.selenium.interactions.Keyboard;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebElement;

/**
 * A wrapper class for Selenium's Keyboard interface, so we can record keyboard
 * events.
 */
public class EyesKeyboard implements Keyboard {

    private final Logger logger;
    private final EyesWebDriver eyesDriver;
    private final Keyboard keyboard;

    public EyesKeyboard(Logger logger, EyesWebDriver eyesDriver,
                        Keyboard keyboard) {
        ArgumentGuard.notNull(logger, "logger");
        ArgumentGuard.notNull(eyesDriver, "eyesDriver");
        ArgumentGuard.notNull(keyboard, "keyboard");

        this.logger = logger;
        this.eyesDriver = eyesDriver;
        this.keyboard = keyboard;
    }

    public void sendKeys(CharSequence... charSequences) {

        Region control = Region.EMPTY;

        // We first find the active element to get the region
        WebElement activeElement = eyesDriver.switchTo().activeElement();

        if (activeElement instanceof RemoteWebElement) {
            activeElement = new EyesRemoteWebElement(logger, eyesDriver, activeElement);

            control = ((EyesRemoteWebElement)activeElement).getBounds();
        }

        for(CharSequence keys : charSequences) {
            String text = String.valueOf(keys);
            eyesDriver.getEyes().addTextTrigger(control, text);
        }

        keyboard.sendKeys(charSequences);
    }

    public void pressKey(CharSequence keyToPress) {
        keyboard.pressKey(keyToPress);
    }

    public void releaseKey(CharSequence keyToRelease) {
        keyboard.releaseKey(keyToRelease);
    }
}
