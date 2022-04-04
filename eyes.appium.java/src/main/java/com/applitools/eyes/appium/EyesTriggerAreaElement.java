package com.applitools.eyes.appium;

import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebElement;

public class EyesTriggerAreaElement {

    private EyesAppiumDriver driver;
    private WebElement triggerArea;


    public EyesTriggerAreaElement(EyesAppiumDriver driver) {
        this.driver = driver;
        searchForTriggerArea();
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
            searchForTriggerArea();
            return triggerArea.getText();
        }
    }

    private void searchForTriggerArea() {
        triggerArea = driver.findElementByAccessibilityId("UFG_TriggerArea");
    }
}
