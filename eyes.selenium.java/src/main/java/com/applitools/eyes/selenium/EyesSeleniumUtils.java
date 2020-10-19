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
import com.applitools.utils.GeneralUtils;
import org.openqa.selenium.*;

import java.util.List;

/**
 * We named this class EyesSeleniumUtils because there's a SeleniumUtils
 * class, and it caused collision.
 */
public class EyesSeleniumUtils {
    /**
     * #internal
     * This method gets the default root element of the page. It will be "html" or "body".
     */
    public static WebElement getDefaultRootElement(Logger logger, EyesSeleniumDriver driver) {
        EyesRemoteWebElement chosenElement;
        WebElement scrollingElement;
        try {
            scrollingElement = (WebElement) driver.executeScript("return document.scrollingElement");
        } catch (Throwable t) {
            GeneralUtils.logExceptionStackTrace(logger, t);
            scrollingElement = null;
        }

        EyesRemoteWebElement eyesScrollingElement = null;
        if (scrollingElement != null) {
            eyesScrollingElement = new EyesRemoteWebElement(logger, driver, scrollingElement);
        }

        WebElement html = driver.findElement(By.tagName("html"));
        EyesRemoteWebElement htmlElement = new EyesRemoteWebElement(logger, driver, html);

        if (eyesScrollingElement != null && eyesScrollingElement.canScrollVertically()) {
            // If document.scrollingElement exists and can scroll vertically then it's the element we are looking for
            chosenElement = eyesScrollingElement;
        } else if (!doesBodyExist(logger, driver)) {
            chosenElement = htmlElement;
        } else {
            WebElement body = driver.findElement(By.tagName("body"));;
            EyesRemoteWebElement bodyElement = new EyesRemoteWebElement(logger, driver, body);
            boolean scrollableHtml =  htmlElement.canScrollVertically();
            boolean scrollableBody = bodyElement.canScrollVertically();

            // If only one of the elements is scrollable, we return the scrollable one
            if (scrollableHtml && !scrollableBody) {
                chosenElement = htmlElement;
            } else if (!scrollableHtml && scrollableBody) {
                chosenElement = bodyElement;
            } else if (scrollingElement != null) {
                // If both of the elements are scrollable or both aren't scrollable, we choose document.scrollingElement which is always one of them
                chosenElement =  eyesScrollingElement;
            } else {
                // If document.scrollingElement, we choose html
                chosenElement = htmlElement;
            }
        }

        logger.verbose(String.format("Chosen default root element is %s", chosenElement.getTagName()));
        return chosenElement;
    }

    private static boolean doesBodyExist(Logger logger, EyesSeleniumDriver driver) {
        try {
            driver.findElement(By.tagName("body"));
            return true;
        } catch (Throwable t) {
            // Supporting web pages without the body element
            logger.log("Failed finding the body element");
            return false;
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
