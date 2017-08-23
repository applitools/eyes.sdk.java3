package com.applitools.eyes.selenium;

import com.applitools.eyes.IEyesJsExecutor;
import com.applitools.eyes.selenium.wrappers.EyesWebDriver;

public class SeleniumJavaScriptExecutor implements IEyesJsExecutor {

    private final EyesWebDriver driver;

    public SeleniumJavaScriptExecutor(EyesWebDriver driver) {
        this.driver = driver;
    }

    @Override
    public Object executeScript(String script, Object... args) {
        return this.driver.executeScript(script, args);
    }
}
