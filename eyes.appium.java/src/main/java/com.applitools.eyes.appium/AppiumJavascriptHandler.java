package com.applitools.eyes.appium;

import com.applitools.eyes.Trigger;
import com.applitools.eyes.selenium.JavascriptHandler;
import com.applitools.eyes.selenium.wrappers.EyesWebDriver;
import com.applitools.eyes.triggers.MouseTrigger;

public class AppiumJavascriptHandler implements JavascriptHandler {

    private final EyesWebDriver driver;

    public AppiumJavascriptHandler (EyesWebDriver driver) {
        this.driver = driver;
    }

    @Override
    public void handle(String script, Object[] args) {
        // Appium commands are sometimes sent as Javascript
        if (AppiumJsCommandExtractor.isAppiumJsCommand(script)) {
            Trigger trigger =
                    AppiumJsCommandExtractor.extractTrigger(driver.getElementIds(),
                            driver.manage().window().getSize(), script, args);

            if (trigger != null) {
                // TODO - Daniel, additional type of triggers
                if (trigger instanceof MouseTrigger) {
                    MouseTrigger mt = (MouseTrigger) trigger;
                    driver.getEyes().addMouseTrigger(mt.getMouseAction(), mt.getControl(), mt.getLocation());
                }
            }
        }
    }

}
