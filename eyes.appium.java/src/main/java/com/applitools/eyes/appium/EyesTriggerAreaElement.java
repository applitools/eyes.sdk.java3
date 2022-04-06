package com.applitools.eyes.appium;

import io.appium.java_client.MobileBy;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class EyesTriggerAreaElement {

    private EyesAppiumDriver driver;
    private int waitTimeoutSeconds;
    private int waitSleepMS;
    private String triggerAreaLabel;
    private WebElement triggerArea;


    public EyesTriggerAreaElement(EyesAppiumDriver driver, String triggerAreaLabel, int triggerAreaTimeoutSeconds,
                                  int waitSleepMS) {
        this.driver = driver;
        this.triggerAreaLabel = triggerAreaLabel;
        this.waitTimeoutSeconds = triggerAreaTimeoutSeconds;
        this.waitSleepMS = waitSleepMS;
    }

    public void click() {
        try {
            triggerArea.click();
        } catch (StaleElementReferenceException e) {
            searchForTriggerArea();
            triggerArea.click();
        }
    }

    public String getText() {
        try {
            return triggerArea.getText();
        } catch (StaleElementReferenceException e) {
            searchForTriggerArea(0,0); // Trigger area should be available.
            return triggerArea.getText();
        }
    }

    public void searchForTriggerArea(int timeoutSeconds_, int sleepMS_) {
        long startTimeMS = System.currentTimeMillis();
        long currentTimeMS = System.currentTimeMillis();
        // Search for the trigger area, but give it time, if necessary.
        do {
            try {
                triggerArea = driver.findElementByAccessibilityId(triggerAreaLabel);
                break;
            } catch (NoSuchElementException e) {
                currentTimeMS = System.currentTimeMillis();
            }
            try {
                Thread.sleep(sleepMS_);
            } catch (InterruptedException e) {
                // Weird, but nothing really to do here.
            }
        } while(((int)Math.floor((currentTimeMS - startTimeMS) / 1000.0)) < timeoutSeconds_);
    }

    public void searchForTriggerArea() {
        searchForTriggerArea(waitTimeoutSeconds, waitSleepMS);
    }
}
