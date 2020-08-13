package com.applitools.eyes.selenium;

import com.applitools.eyes.Location;
import com.applitools.eyes.Logger;
import com.applitools.eyes.Region;
import com.applitools.eyes.UserAgent;
import com.applitools.eyes.positioning.PositionProvider;
import com.applitools.eyes.selenium.fluent.FrameLocator;
import com.applitools.eyes.selenium.fluent.ISeleniumCheckTarget;
import com.applitools.eyes.selenium.fluent.ISeleniumFrameCheckTarget;
import com.applitools.eyes.selenium.frames.Frame;
import com.applitools.eyes.selenium.frames.FrameChain;
import com.applitools.eyes.selenium.wrappers.EyesRemoteWebElement;
import com.applitools.eyes.selenium.wrappers.EyesSeleniumDriver;
import com.applitools.eyes.selenium.wrappers.EyesTargetLocator;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.applitools.eyes.config.Configuration;

public class PageState {
    private final EyesSeleniumDriver driver;
    private final Logger logger;
    private List<FrameState> frameStates;
    private FrameChain originalFrameChain;
    private final StitchMode stitchMode;
    private final UserAgent userAgent;

    private String originalOverflow;
    private String originalHtmlOverflow;
    private String originalBodyOverflow;

    public PageState(Logger logger, EyesSeleniumDriver driver, StitchMode stitchMode, UserAgent userAgent) {
        this.logger = logger;
        this.driver = driver;
        this.stitchMode = stitchMode;
        this.userAgent = userAgent;
    }

    public void preparePage(ISeleniumCheckTarget seleniumCheckTarget, Configuration config, WebElement scrollRootElement) {
        frameStates = new ArrayList<>();
        originalFrameChain = driver.getFrameChain().clone();

        if (seleniumCheckTarget.getTargetElement() != null ||
                seleniumCheckTarget.getTargetSelector() != null ||
                seleniumCheckTarget.getFrameChain().size() > 0) {
            prepareParentFrames();
        }

        if (!EyesDriverUtils.isMobileDevice(driver)) {
            saveCurrentFrameState(frameStates, scrollRootElement);
            tryHideScrollbarsInFrame(config, scrollRootElement);
            int switchedToFrameCount = switchToTargetFrame(seleniumCheckTarget, config, frameStates, scrollRootElement);
            logger.verbose("switchedToFrameCount: " + switchedToFrameCount);
        }
    }

    private void prepareParentFrames() {
        if (originalFrameChain.size() == 0) {
            return;
        }

        EyesTargetLocator switchTo = (EyesTargetLocator) driver.switchTo();
        FrameChain fc = originalFrameChain.clone();
        while (fc.size() > 0) {
            switchTo.parentFrame();
            Frame currentFrame = fc.pop();
            WebElement rootElement = EyesSeleniumUtils.getCurrentFrameScrollRootElement(logger, driver, null);
            saveCurrentFrameState(frameStates, rootElement);
            maximizeTargetFrameInCurrentFrame(currentFrame.getReference(), rootElement);
        }
        Collections.reverse(frameStates);
        switchTo.frames(originalFrameChain);
    }

    public void restorePageState(Configuration config, WebElement scrollRootElement) {
        if (EyesDriverUtils.isMobileDevice(driver)) {
            return;
        }

        Collections.reverse(frameStates);
        for(FrameState state : frameStates) {
            state.restore();
        }
        ((EyesTargetLocator) driver.switchTo()).frames(originalFrameChain);
        if (!EyesDriverUtils.isMobileDevice(driver)) {
            tryRevertScrollbarsInFrame(config, scrollRootElement);
        }
    }

    private int switchToTargetFrame(ISeleniumCheckTarget checkTarget, Configuration config,
                                   List<FrameState> frameStates, WebElement userDefinedSRE) {
        List<FrameLocator> frameChain = checkTarget.getFrameChain();
        for(FrameLocator frameLocator : frameChain)
        {
            WebElement frameElement = EyesSeleniumUtils.findFrameByFrameCheckTarget(frameLocator, driver);
            maximizeTargetFrameInCurrentFrame(frameElement, userDefinedSRE);
            switchToFrame(frameElement, frameLocator, config, frameStates);
        }
        return frameChain.size();
    }

    private void maximizeTargetFrameInCurrentFrame(WebElement frameElement, WebElement scrollRootElement) {
        WebElement currentFrameSRE = EyesSeleniumUtils.getCurrentFrameScrollRootElement(logger, driver, scrollRootElement);

        PositionProvider positionProvider = SeleniumEyes.getPositionProviderForScrollRootElement(logger, driver,
                stitchMode, userAgent, currentFrameSRE);

        Region frameRect = EyesRemoteWebElement.getClientBoundsWithoutBorders(frameElement, driver, logger);
        if (stitchMode == StitchMode.SCROLL) {
            Location pageScrollPosition = positionProvider.getCurrentPosition();
            frameRect = frameRect.offset(pageScrollPosition.getX(), pageScrollPosition.getY());
        }
        positionProvider.setPosition(frameRect.getLocation());
    }

    private void switchToFrame(WebElement frameElement,
                               ISeleniumFrameCheckTarget frameTarget, Configuration config, List<FrameState> frameStates) {
        WebDriver.TargetLocator switchTo = driver.switchTo();

        switchTo.frame(frameElement);
        WebElement rootElement = SeleniumEyes.getScrollRootElementFromSREContainer(logger, frameTarget, driver);
        Frame frame = driver.getFrameChain().peek();
        frame.setScrollRootElement(rootElement);
        saveCurrentFrameState(frameStates, rootElement);
        tryHideScrollbarsInFrame(config, rootElement);
        frame.setScrollRootElementInnerBounds(EyesRemoteWebElement.getClientBoundsWithoutBorders(rootElement, driver, logger));
    }

    private void tryHideScrollbarsInFrame(Configuration config, WebElement rootElement) {
        updateScrollbars(config, rootElement, true);
    }

    private void tryRevertScrollbarsInFrame(Configuration config, WebElement rootElement) {
        updateScrollbars(config, rootElement, false);
    }

    private void updateScrollbars(Configuration configuration, WebElement rootElement, boolean shouldHide) {
        if (!configuration.getHideScrollbars()) {
            return;
        }

        String hiddenValue = "hidden";

        if (!rootElement.equals(EyesSeleniumUtils.getDefaultRootElement(logger, driver))) {
            originalOverflow = EyesDriverUtils.setOverflow(driver, shouldHide ? hiddenValue : originalOverflow, rootElement);
            return;
        }

        WebElement html = driver.findElement(By.tagName("html"));
        WebElement body = driver.findElement(By.tagName("body"));
        originalHtmlOverflow = EyesDriverUtils.setOverflow(driver, shouldHide ? hiddenValue : originalHtmlOverflow, html);
        originalBodyOverflow = EyesDriverUtils.setOverflow(driver, shouldHide ? hiddenValue : originalBodyOverflow, body);
    }

    private void saveCurrentFrameState(List<FrameState> frameStates, WebElement rootElement) {
        FrameState frameState = FrameState.getCurrentFrameState(driver, rootElement);
        frameStates.add(frameState);
    }
}
