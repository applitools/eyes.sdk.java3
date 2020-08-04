/*
 * Applitools software.
 */
package com.applitools.eyes.selenium;

import com.applitools.eyes.*;
import com.applitools.eyes.selenium.fluent.FrameLocator;
import com.applitools.eyes.selenium.fluent.IScrollRootElementContainer;
import com.applitools.eyes.selenium.frames.Frame;
import com.applitools.eyes.selenium.frames.FrameChain;
import com.applitools.eyes.selenium.wrappers.EyesRemoteWebElement;
import com.applitools.eyes.selenium.wrappers.EyesSeleniumDriver;
import org.openqa.selenium.*;

import java.util.List;

/**
 * We named this class EyesSeleniumUtils because there's a SeleniumUtils
 * class, and it caused collision.
 */
@SuppressWarnings("WeakerAccess")
public class EyesSeleniumUtils {


    /**
     * #internal
     * This method gets the default root element of the page. It will be "html" unless "body" element is higher.
     */
    public static WebElement getDefaultRootElement(Logger logger, EyesSeleniumDriver driver) {
        WebElement html = driver.findElement(By.tagName("html"));
        WebElement body = driver.findElement(By.tagName("body"));
        EyesRemoteWebElement htmlElement = new EyesRemoteWebElement(logger, driver, html);
        EyesRemoteWebElement bodyElement = new EyesRemoteWebElement(logger, driver, body);
        if (htmlElement.getBoundingClientRect().height < bodyElement.getBoundingClientRect().height) {
            return bodyElement;
        } else {
            return htmlElement;
        }
    }

    /**
     * #internal
     */
    public static WebElement getScrollRootElement(Logger logger, EyesSeleniumDriver driver, IScrollRootElementContainer scrollRootElementContainer) {
        if (EyesDriverUtils.isMobileDevice(driver)) {
            return null;
        }

        if (scrollRootElementContainer == null) {
            return EyesSeleniumUtils.getDefaultRootElement(logger, driver);
        }

        WebElement scrollRootElement = scrollRootElementContainer.getScrollRootElement();
        if (scrollRootElement != null) {
            return scrollRootElement;
        }

        By scrollRootSelector = scrollRootElementContainer.getScrollRootSelector();
        if (scrollRootSelector != null) {
            return driver.findElement(scrollRootSelector);
        }

        logger.log("Warning: Got an empty scroll root element container");
        return EyesSeleniumUtils.getDefaultRootElement(logger, driver);
    }

    public static WebElement findFrameByFrameCheckTarget(FrameLocator frameTarget, EyesSeleniumDriver driver) {
        if (frameTarget.getFrameIndex() != null) {
            return driver.findElement(By.xpath("IFRAME[" + frameTarget.getFrameIndex() + "]"));
        }

        String nameOrId = frameTarget.getFrameNameOrId();
        if (nameOrId != null) {
            List<WebElement> byId = driver.findElements(By.id(nameOrId));
            if (byId.size() > 0) {
                return byId.get(0);
            }
            return driver.findElement(By.name(nameOrId));
        }

        WebElement reference = frameTarget.getFrameReference();
        if (reference != null) {
            return reference;
        }

        By selector = frameTarget.getFrameSelector();
        if (selector != null) {
            return driver.findElement(selector);
        }

        return null;
    }

    public static WebElement getCurrentFrameScrollRootElement(Logger logger, EyesSeleniumDriver driver, WebElement userDefinedSRE) {

        WebElement scrollRootElement = tryGetCurrentFrameScrollRootElement(driver);
        if (scrollRootElement == null)
        {
            scrollRootElement = userDefinedSRE != null ? userDefinedSRE : getDefaultRootElement(logger, driver);
        }
        return scrollRootElement;
    }

    public static WebElement tryGetCurrentFrameScrollRootElement(EyesSeleniumDriver driver)
    {
        FrameChain fc = driver.getFrameChain().clone();
        Frame currentFrame = fc.peek();
        WebElement scrollRootElement = null;
        if (currentFrame != null)
        {
            scrollRootElement = currentFrame.getScrollRootElement();
        }

        return scrollRootElement;
    }
}
