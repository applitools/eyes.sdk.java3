/*
 * Applitools SDK for Selenium integration.
 */
package com.applitools.eyes.selenium.wrappers;

import com.applitools.eyes.*;
import com.applitools.eyes.positioning.PositionMemento;
import com.applitools.eyes.positioning.PositionProvider;
import com.applitools.eyes.selenium.Borders;
import com.applitools.eyes.selenium.SeleniumJavaScriptExecutor;
import com.applitools.eyes.selenium.SizeAndBorders;
import com.applitools.eyes.selenium.frames.Frame;
import com.applitools.eyes.selenium.frames.FrameChain;
import com.applitools.eyes.selenium.positioning.SeleniumScrollPositionProvider;
import com.applitools.eyes.selenium.positioning.ScrollPositionProviderFactory;
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

    private PositionMemento defaultContentPositionMemento;

    /**
     * Initialized a new EyesTargetLocator object.
     * @param driver        The WebDriver from which the targetLocator was received.
     * @param targetLocator The actual TargetLocator object.
     */
    public EyesTargetLocator(EyesSeleniumDriver driver, Logger logger,
                             WebDriver.TargetLocator targetLocator) {
        ArgumentGuard.notNull(driver, "driver");
        ArgumentGuard.notNull(targetLocator, "targetLocator");
        this.driver = driver;
        this.logger = logger;
        this.targetLocator = targetLocator;
        this.jsExecutor = new SeleniumJavaScriptExecutor(driver);
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
        logger.verbose(String.format("(%d)", index));
        // Finding the target element so and reporting it using onWillSwitch.
        logger.verbose("Getting frames list...");
        List<WebElement> frames = driver.findElementsByCssSelector("frame, iframe");
        if (index > frames.size()) {
            throw new NoSuchFrameException(String.format("Frame index [%d] is invalid!", index));
        }
        logger.verbose("Done! getting the specific frame...");
        WebElement targetFrame = frames.get(index);
        logger.verbose("Done! Making preparations...");
        willSwitchToFrame(targetFrame);
        logger.verbose("Done! Switching to frame...");
        targetLocator.frame(index);
        logger.verbose("Done!");
        return driver;
    }

    public WebDriver frame(String nameOrId) {
        logger.verbose(String.format("('%s')", nameOrId));
        // Finding the target element so we can report it.
        // We use find elements(plural) to avoid exception when the element
        // is not found.
        logger.verbose("Getting frames by name...");
        List<WebElement> frames = driver.findElementsByName(nameOrId);
        if (frames.size() == 0) {
            logger.verbose("No frames Found! Trying by id...");
            // If there are no frames by that name, we'll try the id
            frames = driver.findElementsById(nameOrId);
            if (frames.size() == 0) {
                // No such frame, bummer
                throw new NoSuchFrameException(String.format(
                        "No frame with name or id '%s' exists!", nameOrId));
            }
        }
        logger.verbose("Done! Making preparations...");
        willSwitchToFrame(frames.get(0));
        logger.verbose("Done! Switching to frame " + nameOrId + "...");
        targetLocator.frame(nameOrId);
        logger.verbose("Done!");
        return driver;
    }

    public WebDriver frame(WebElement frameElement) {
        logger.verbose("Making preparations...");
        willSwitchToFrame(frameElement);
        logger.verbose("Done! Switching to frame...");
        targetLocator.frame(frameElement);
        logger.verbose("Done!");
        return driver;
    }

    public WebDriver parentFrame() {
        logger.verbose("enter");
        if (driver.getFrameChain().size() != 0) {
            logger.verbose("Making preparations...");
            driver.getFrameChain().pop();
            logger.verbose("Done! Switching to parent frame...");
            parentFrame(logger, targetLocator, driver.getFrameChain());
        }
        logger.verbose("Done!");
        return driver;
    }

    public static void parentFrame(Logger logger, WebDriver.TargetLocator targetLocator, FrameChain frameChainToParent) {
        logger.verbose("enter (static)");
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
    @SuppressWarnings("UnusedReturnValue")
    public WebDriver framesDoScroll(FrameChain frameChain) {
        logger.verbose("enter");
        this.defaultContent();
        PositionProvider scrollProvider = ScrollPositionProviderFactory.getScrollPositionProvider(driver.getUserAgent(), logger, jsExecutor, driver.getEyes().getCurrentFrameScrollRootElement());
        defaultContentPositionMemento = scrollProvider.getState();
        for (Frame frame : frameChain) {
            logger.verbose("Scrolling by parent scroll position...");
            Location frameLocation = frame.getLocation();
            scrollProvider.setPosition(frameLocation);
            logger.verbose("Done! Switching to frame...");
            this.frame(frame.getReference());
            Frame newFrame = driver.getFrameChain().peek();
            newFrame.setScrollRootElement(frame.getScrollRootElement());
            logger.verbose("Done!");
        }

        logger.verbose("Done switching into nested frames!");
        return driver;
    }

    /**
     * Switches into every frame in the frame chain. This is used as way to
     * switch into nested frames (while considering scroll) in a single call.
     * @param frameChain The path to the frame to switch to.
     * @return The WebDriver with the switched context.
     */
    @SuppressWarnings("UnusedReturnValue")
    public WebDriver frames(FrameChain frameChain) {
        logger.verbose("enter");
        this.defaultContent();
        for (Frame frame : frameChain) {
            this.frame(frame.getReference());
            logger.verbose(String.format("frame.Reference: %s ; frame.ScrollRootElement: %s", frame.getReference(), frame.getScrollRootElement()));
            Frame newFrame = driver.getFrameChain().peek();
            newFrame.setScrollRootElement(frame.getScrollRootElement());
        }
        logger.verbose("Done switching into nested frames!");
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
        logger.verbose("enter");
        for (String frameNameOrId : framesPath) {
            logger.verbose("Switching to frame...");
            targetLocator.frame(frameNameOrId);
            logger.verbose("Done!");
        }
        logger.verbose("Done switching into nested frames!");
        return driver;
    }

    public WebDriver window(String nameOrHandle) {
        logger.verbose("enter");
        driver.getFrameChain().clear();
        logger.verbose("Done! Switching to window...");
        targetLocator.window(nameOrHandle);
        logger.verbose("Done!");
        return driver;
    }

    public WebDriver defaultContent() {
        logger.verbose("enter");
        if (driver.getFrameChain().size() != 0) {
            logger.verbose("Making preparations...");
            driver.getFrameChain().clear();
            logger.verbose("Done! Switching to default content...");
        }
        targetLocator.defaultContent();
        logger.verbose("Done!");
        return driver;
    }

    public WebElement activeElement() {
        logger.verbose("Switching to element...");
        WebElement element = targetLocator.activeElement();
        if (!(element instanceof RemoteWebElement)) {
            throw new EyesException("Not a remote web element!");
        }
        EyesRemoteWebElement result = new EyesRemoteWebElement(logger, driver, element);
        logger.verbose("Done!");
        return result;
    }

    public Alert alert() {
        logger.verbose("Switching to alert...");
        Alert result = targetLocator.alert();
        logger.verbose("Done!");
        return result;
    }

    public void resetScroll() {
        logger.verbose("enter");
        if (this.driver.getEyes() != null) {
            this.scrollPosition = ScrollPositionProviderFactory.getScrollPositionProvider(driver.getUserAgent(), logger, jsExecutor, driver.getEyes().getCurrentFrameScrollRootElement());
        }
        if (defaultContentPositionMemento != null) {
            scrollPosition.restoreState(defaultContentPositionMemento);
        }
    }
}
