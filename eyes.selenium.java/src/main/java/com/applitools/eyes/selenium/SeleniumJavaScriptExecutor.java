package com.applitools.eyes.selenium;

import com.applitools.eyes.IEyesJsExecutor;
import com.applitools.eyes.selenium.wrappers.EyesSeleniumDriver;

public class SeleniumJavaScriptExecutor implements IEyesJsExecutor {

    private final EyesSeleniumDriver driver;

    public SeleniumJavaScriptExecutor(EyesSeleniumDriver driver) {
        this.driver = driver;
    }

    @Override
    public Object executeScript(String script, Object... args) {
        return this.driver.executeScript(script, args);
    }
}
