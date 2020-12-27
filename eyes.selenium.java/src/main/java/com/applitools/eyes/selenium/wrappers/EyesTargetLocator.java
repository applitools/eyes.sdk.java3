package com.applitools.eyes.selenium.wrappers;

import com.applitools.eyes.*;
import com.applitools.eyes.config.Configuration;
import com.applitools.eyes.config.Feature;
import com.applitools.eyes.positioning.PositionMemento;
import com.applitools.eyes.positioning.PositionProvider;
import com.applitools.eyes.selenium.Borders;
import com.applitools.eyes.selenium.SeleniumJavaScriptExecutor;
import com.applitools.eyes.selenium.SizeAndBorders;
import com.applitools.eyes.selenium.frames.Frame;
import com.applitools.eyes.selenium.frames.FrameChain;
import com.applitools.eyes.selenium.positioning.ScrollPositionProviderFactory;
import com.applitools.eyes.selenium.positioning.SeleniumScrollPositionProvider;
import com.applitools.utils.ArgumentGuard;
import org.openqa.selenium.*;
import org.openqa.selenium.remote.RemoteWebElement;

import java.util.List;

/**
 * Wraps a target locator so we can keep track of which frames have been
 * switched to.
 */
public class EyesTargetLocator implements WebDriver.TargetLocator {

    private final Logger logger;
    private final EyesSeleniumDriver driver;
    private SeleniumScrollPositionProvider scrollPosition;
    private final WebDriver.TargetLocator targetLocator;
    private final SeleniumJavaScriptExecutor jsExecutor;
    private final Configuration configuration;

    private PositionMemento defaultContentPositionMemento;

    /**
     * Initialized a new EyesTargetLocator object.
     * @param driver        The WebDriver from which the targetLocator was received.
     * @param targetLocator The actual TargetLocator object.
     */
    public EyesTargetLocator(EyesSeleniumDriver driver, Logger logger, WebDriver.TargetLocator targetLocator) {
        ArgumentGuard.notNull(driver, "driver");
        ArgumentGuard.notNull(targetLocator, "targetLocator");
        this.driver = driver;
        this.logger = logger;
        this.targetLocator = targetLocator;
        this.jsExecutor = new SeleniumJavaScriptExecutor(driver);
        this.configuration = driver.getEyes().getConfiguration();
    }

    /**
     * Will be called before switching into a frame.
     * @param targetFrame The element about to be switched to.
     */
    private void willSwitchToFrame(WebElement targetFrame) {

        ArgumentGuard.notNull(targetFrame, "targetFrame");

        EyesRemoteWebElement eyesFrame = (targetFrame instanceof EyesRemoteWebElement) ?
                (EyesRemoteWebElement) targetFrame : new EyesRemoteWebElement(logger, driver, targetFrame);

        Point pl = targetFrame.getLocation();
        Dimension ds = targetFrame.getSize();

        SizeAndBorders sizeAndBorders = eyesFrame.getSizeAndBorders();
        Borders borders = sizeAndBorders.getBorders();
        RectangleSize frameInnerSize = sizeAndBorders.getSize();
        Rectangle bounds = eyesFrame.getBoundingClientRect();
        Region boundsAsRegion = new Region(bounds.x, bounds.y, bounds.width, bounds.height);

        Location contentLocation = new Location(bounds.getX() + borders.getLeft(), bounds.getY() + borders.getTop());
        Location originalLocation = eyesFrame.getScrollLocation();

        Frame frame = new Frame(logger, targetFrame,
                contentLocation,
                new RectangleSize(ds.getWidth(), ds.getHeight()),
                frameInnerSize,
                originalLocation,
                boundsAsRegion,
                borders,
                this.driver);

        driver.getFrameChain().push(frame);
    }

    public WebDriver frame(int index) {
        List<WebElement> frames = driver.findElementsByCssSelector("frame, iframe");
        if (index > frames.size()) {
            throw new NoSuchFrameException(String.format("Frame index [%d] is invalid!", index));
        }

        WebElement targetFrame = frames.get(index);
        willSwitchToFrame(targetFrame);
        targetLocator.frame(index);
        return driver;
    }

    public WebDriver frame(String nameOrId) {
        List<WebElement> frames = driver.findElementsByName(nameOrId);
        if (frames.size() == 0) {
            // If there are no frames by that name, we'll try the id
            frames = driver.findElementsById(nameOrId);
            if (frames.size() == 0) {
                // No such frame, bummer
                throw new NoSuchFrameException(String.format(
                        "No frame with name or id '%s' exists!", nameOrId));
            }
        }
        willSwitchToFrame(frames.get(0));
        targetLocator.frame(nameOrId);
        return driver;
    }

    public WebDriver frame(WebElement frameElement) {
        willSwitchToFrame(frameElement);
        targetLocator.frame(frameElement);
        return driver;
    }

    public WebDriver parentFrame() {
        if (driver.getFrameChain().size() != 0) {
            driver.getFrameChain().pop();
            parentFrame(logger, targetLocator, driver.getFrameChain());
        }

        return driver;
    }

    public static void parentFrame(Logger logger, WebDriver.TargetLocator targetLocator, FrameChain frameChainToParent) {
        try {
            targetLocator.parentFrame();
        } catch (Exception WebDriverException) {
            targetLocator.defaultContent();
            for (Frame frame : frameChainToParent) {
                targetLocator.frame(frame.getReference());
            }
        }
    }


    /**
     * Switches into every frame in the frame chain. This is used as way to
     * switch into nested frames (while considering scroll) in a single call.
     * @param frameChain The path to the frame to switch to.
     * @return The WebDriver with the switched context.
     */
    public WebDriver framesDoScroll(FrameChain frameChain) {
        this.defaultContent();
        PositionProvider scrollProvider = ScrollPositionProviderFactory.getScrollPositionProvider(driver.getUserAgent(), logger, jsExecutor, driver.getEyes().getCurrentFrameScrollRootElement());
        defaultContentPositionMemento = scrollProvider.getState();
        for (Frame frame : frameChain) {
            Location frameLocation = frame.getLocation();
            scrollProvider.setPosition(frameLocation);
            this.frame(frame.getReference());
            Frame newFrame = driver.getFrameChain().peek();
            newFrame.setScrollRootElement(frame.getScrollRootElement());
        }

        return driver;
    }

    /**
     * Switches into every frame in the frame chain. This is used as way to
     * switch into nested frames (while considering scroll) in a single call.
     * @param frameChain The path to the frame to switch to.
     * @return The WebDriver with the switched context.
     */
    public WebDriver frames(FrameChain frameChain) {
        this.defaultContent();
        for (Frame frame : frameChain) {
            this.frame(frame.getReference());
            Frame newFrame = driver.getFrameChain().peek();
            newFrame.setScrollRootElement(frame.getScrollRootElement());
        }
        return driver;
    }

    /**
     * Switches into every frame in the list. This is used as way to
     * switch into nested frames in a single call.
     * @param framesPath The path to the frame to check. This is a list of
     *                   frame names/IDs (where each frame is nested in the
     *                   previous frame).
     * @return The WebDriver with the switched context.
     */
    public WebDriver frames(String[] framesPath) {
        for (String frameNameOrId : framesPath) {
            targetLocator.frame(frameNameOrId);
        }
        return driver;
    }

    public WebDriver window(String nameOrHandle) {
        driver.getFrameChain().clear();
        targetLocator.window(nameOrHandle);
        return driver;
    }

    public WebDriver defaultContent() {
        if (driver.getFrameChain().size() != 0) {
            driver.getFrameChain().clear();
            targetLocator.defaultContent();
        } else if (!configuration.isFeatureActivated(Feature.NO_SWITCH_WITHOUT_FRAME_CHAIN)) {
            targetLocator.defaultContent();
        }

        return driver;
    }

    public WebElement activeElement() {
        WebElement element = targetLocator.activeElement();
        if (!(element instanceof RemoteWebElement)) {
            throw new EyesException("Not a remote web element!");
        }
        return new EyesRemoteWebElement(logger, driver, element);
    }

    public Alert alert() {
        Alert result = targetLocator.alert();
        return result;
    }

    public void resetScroll() {
        if (this.driver.getEyes() != null) {
            this.scrollPosition = ScrollPositionProviderFactory.getScrollPositionProvider(driver.getUserAgent(), logger, jsExecutor, driver.getEyes().getCurrentFrameScrollRootElement());
        }
        if (defaultContentPositionMemento != null) {
            scrollPosition.restoreState(defaultContentPositionMemento);
        }
    }
}
