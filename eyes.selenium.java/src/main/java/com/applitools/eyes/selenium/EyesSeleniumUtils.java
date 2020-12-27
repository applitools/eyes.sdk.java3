/*
 * Applitools software.
 */
package com.applitools.eyes.selenium;

import com.applitools.eyes.EyesException;
import com.applitools.eyes.Logger;
import com.applitools.eyes.UserAgent;
import com.applitools.eyes.logging.Stage;
import com.applitools.eyes.logging.TraceLevel;
import com.applitools.eyes.logging.Type;
import com.applitools.eyes.selenium.fluent.FrameLocator;
import com.applitools.eyes.selenium.fluent.IScrollRootElementContainer;
import com.applitools.eyes.selenium.frames.Frame;
import com.applitools.eyes.selenium.frames.FrameChain;
import com.applitools.eyes.selenium.wrappers.EyesRemoteWebElement;
import com.applitools.eyes.selenium.wrappers.EyesSeleniumDriver;
import com.applitools.eyes.selenium.wrappers.EyesWebDriver;
import com.applitools.utils.GeneralUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.tuple.Pair;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * We named this class EyesSeleniumUtils because there's a SeleniumUtils
 * class, and it caused collision.
 */
public class EyesSeleniumUtils {

    private static final long DOM_EXTRACTION_TIMEOUT = 5 * 60 * 1000;
    private static final String DOM_SCRIPTS_WRAPPER = "return (%s)(%s);";

    private static class TimeoutTask extends TimerTask {
        private final AtomicBoolean isCheckTimerTimedOut;
        public TimeoutTask(AtomicBoolean isCheckTimerTimedOut) {
            this.isCheckTimerTimedOut = isCheckTimerTimedOut;
        }

        @Override
        public void run() {
            isCheckTimerTimedOut.set(true);
        }

    }

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
            GeneralUtils.logExceptionStackTrace(logger, Stage.GENERAL, t);
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
            WebElement body = driver.findElement(By.tagName("body"));
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

        logger.log(new HashSet<String>(), Stage.GENERAL, Pair.of("defaultRootElement", chosenElement.getTagName()));
        return chosenElement;
    }

    private static boolean doesBodyExist(Logger logger, EyesSeleniumDriver driver) {
        try {
            driver.findElement(By.tagName("body"));
            return true;
        } catch (Throwable t) {
            GeneralUtils.logExceptionStackTrace(logger, Stage.GENERAL, t);
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

    public static String runDomScript(Logger logger, EyesWebDriver driver, UserAgent userAgent, Set<String> testIds, String domScript,
                                      Map<String, Object> domScriptArguments, String pollingScript) throws Exception {
        if (domScriptArguments == null) {
            domScriptArguments = new HashMap<>();
        }

        Map<String, Object> pollingScriptArguments = new HashMap<>();

        int chunkByteLength = userAgent.getOS().toLowerCase().contains("ios") ? 10 * 1024 * 1024 : 256 * 1024 * 1024;
        domScriptArguments.put("chunkByteLength", chunkByteLength);
        pollingScriptArguments.put("chunkByteLength", chunkByteLength);
        ObjectMapper mapper = new ObjectMapper();
        String domScriptWrapped = String.format(DOM_SCRIPTS_WRAPPER, domScript, mapper.writeValueAsString(domScriptArguments));
        String pollingScriptWrapped = String.format(DOM_SCRIPTS_WRAPPER, pollingScript, mapper.writeValueAsString(pollingScriptArguments));

        AtomicBoolean isCheckTimerTimedOut = new AtomicBoolean(false);
        Timer timer = new Timer("VG_Check_StopWatch", true);
        timer.schedule(new TimeoutTask(isCheckTimerTimedOut), DOM_EXTRACTION_TIMEOUT);
        try {
            String resultAsString = (String) driver.executeScript(domScriptWrapped);
            ScriptResponse scriptResponse = GeneralUtils.parseJsonToObject(resultAsString, ScriptResponse.class);
            ScriptResponse.Status status = scriptResponse.getStatus();

            logger.log(TraceLevel.Info, testIds, Stage.CHECK, Type.DOM_SCRIPT, Pair.of("message", "Starting dom script"));
            while (status == ScriptResponse.Status.WIP && !isCheckTimerTimedOut.get()) {
                resultAsString = (String) driver.executeScript(pollingScriptWrapped);
                scriptResponse = GeneralUtils.parseJsonToObject(resultAsString, ScriptResponse.class);
                status = scriptResponse.getStatus();
                Thread.sleep(200);
            }

            if (status == ScriptResponse.Status.ERROR) {
                throw new EyesException("DomSnapshot Error: " + scriptResponse.getError());
            }

            if (isCheckTimerTimedOut.get()) {
                throw new EyesException("Domsnapshot Timed out");
            }

            if (status == ScriptResponse.Status.SUCCESS) {
                return scriptResponse.getValue().toString();
            }

            logger.log(TraceLevel.Info, testIds, Stage.CHECK, Type.DOM_SCRIPT, Pair.of("message", "Collecting chunks"));
            StringBuilder value = new StringBuilder();
            while (status == ScriptResponse.Status.SUCCESS_CHUNKED && !scriptResponse.isDone() && !isCheckTimerTimedOut.get()) {
                value.append(GeneralUtils.parseJsonToObject(scriptResponse.getValue().toString(), String.class));
                resultAsString = (String) driver.executeScript(pollingScriptWrapped);
                scriptResponse = GeneralUtils.parseJsonToObject(resultAsString, ScriptResponse.class);
                status = scriptResponse.getStatus();
                Thread.sleep(200);
            }

            if (status == ScriptResponse.Status.ERROR) {
                throw new EyesException("DomSnapshot Error: " + scriptResponse.getError());
            }

            if (isCheckTimerTimedOut.get()) {
                throw new EyesException("Domsnapshot Timed out");
            }

            value.append(GeneralUtils.parseJsonToObject(scriptResponse.getValue().toString(), String.class));
            return value.toString();
        } finally {
            timer.cancel();
            logger.log(TraceLevel.Info, testIds, Stage.CHECK, Type.DOM_SCRIPT, Pair.of("message", "Finished dom script"));
        }
    }
}
