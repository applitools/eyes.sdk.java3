/*
 * Applitools SDK for Selenium integration.
 */
package com.applitools.eyes.selenium.wrappers;

import com.applitools.eyes.EyesException;
import com.applitools.eyes.Location;
import com.applitools.eyes.Logger;
import com.applitools.eyes.RectangleSize;
import com.applitools.eyes.selenium.SeleniumJavaScriptExecutor;
import com.applitools.eyes.selenium.frames.Frame;
import com.applitools.eyes.selenium.frames.FrameChain;
import com.applitools.eyes.selenium.positioning.ScrollPositionProvider;
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
    private final EyesWebDriver driver;
    private final SeleniumJavaScriptExecutor jsExecutor;
    private final ScrollPositionProvider scrollPosition;
    private final WebDriver.TargetLocator targetLocator;

    /**
     * Will be called before switching into a frame.
     * @param targetFrame The element about to be switched to.
     */
    public void willSwitchToFrame(WebElement targetFrame) {

        ArgumentGuard.notNull(targetFrame, "targetFrame");

        EyesRemoteWebElement eyesFrame = (targetFrame instanceof EyesRemoteWebElement) ?
                (EyesRemoteWebElement) targetFrame : new EyesRemoteWebElement(logger, driver, targetFrame);

        Point pl = targetFrame.getLocation();
        Dimension ds = targetFrame.getSize();

        int clientWidth = eyesFrame.getClientWidth();
        int clientHeight = eyesFrame.getClientHeight();

        int borderLeftWidth = eyesFrame.getComputedStyleInteger("border-left-width");
        int borderTopWidth = eyesFrame.getComputedStyleInteger("border-top-width");

        Location contentLocation = new Location(pl.getX() + borderLeftWidth, pl.getY() + borderTopWidth);

        Location originalLocation = scrollPosition.getCurrentPosition();

        Frame frame = new Frame(logger, targetFrame,
                contentLocation,
                new RectangleSize(ds.getWidth(), ds.getHeight()),
                new RectangleSize(clientWidth, clientHeight),
                originalLocation);

        driver.getFrameChain().push(frame);
    }

    /**
     * Initialized a new EyesTargetLocator object.
     * @param driver        The WebDriver from which the targetLocator was received.
     * @param targetLocator The actual TargetLocator object.
     */
    public EyesTargetLocator(Logger logger, EyesWebDriver driver,
                             WebDriver.TargetLocator targetLocator) {
        ArgumentGuard.notNull(logger, "logger");
        ArgumentGuard.notNull(driver, "driver");
        ArgumentGuard.notNull(targetLocator, "targetLocator");
        this.logger = logger;
        this.driver = driver;
        this.targetLocator = targetLocator;
        this.jsExecutor = new SeleniumJavaScriptExecutor(driver);
        this.scrollPosition = new ScrollPositionProvider(logger, jsExecutor);
    }

    public WebDriver frame(int index) {
        logger.verbose(String.format("EyesTargetLocator.frame(%d)", index));
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
        logger.verbose(String.format("EyesTargetLocator.frame('%s')",
                nameOrId));
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
        logger.verbose("Done! Switching to frame...");
        targetLocator.frame(nameOrId);
        logger.verbose("Done!");
        return driver;
    }

    public WebDriver frame(WebElement frameElement) {
        logger.verbose("EyesTargetLocator.frame(element)");
        logger.verbose("Making preparations...");
        willSwitchToFrame(frameElement);
        logger.verbose("Done! Switching to frame...");
        targetLocator.frame(frameElement);
        logger.verbose("Done!");
        return driver;
    }

    public WebDriver parentFrame() {
        logger.verbose("EyesTargetLocator.parentFrame()");
        if (driver.getFrameChain().size() != 0) {
            logger.verbose("Making preparations..");
            driver.getFrameChain().pop();
            logger.verbose("Done! Switching to parent frame..");
            targetLocator.parentFrame();
        }
        logger.verbose("Done!");
        return driver;
    }

    /**
     * Switches into every frame in the frame chain. This is used as way to
     * switch into nested frames (while considering scroll) in a single call.
     * @param frameChain The path to the frame to switch to.
     * @return The WebDriver with the switched context.
     */
    public WebDriver framesDoScroll(FrameChain frameChain) {
        logger.verbose("EyesTargetLocator.framesDoScroll(frameChain)");
        driver.switchTo().defaultContent();
        for (Frame frame : frameChain) {
            logger.verbose("Scrolling by parent scroll position...");
            Location frameLocation = frame.getLocation();
            scrollPosition.setPosition(frameLocation);
            logger.verbose("Done! Switching to frame...");
            driver.switchTo().frame(frame.getReference());
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
    public WebDriver frames(FrameChain frameChain) {
        logger.verbose("EyesTargetLocator.frames(frameChain)");
        driver.switchTo().defaultContent();
        for (Frame frame : frameChain) {
            driver.switchTo().frame(frame.getReference());
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
        logger.verbose("EyesTargetLocator.frames(framesPath)");
        for (String frameNameOrId : framesPath) {
            logger.verbose("Switching to frame...");
            driver.switchTo().frame(frameNameOrId);
            logger.verbose("Done!");
        }
        logger.verbose("Done switching into nested frames!");
        return driver;
    }

    public WebDriver window(String nameOrHandle) {
        logger.verbose("EyesTargetLocator.window()");
        driver.getFrameChain().clear();
        logger.verbose("Done! Switching to window...");
        targetLocator.window(nameOrHandle);
        logger.verbose("Done!");
        return driver;
    }

    public WebDriver defaultContent() {
        logger.verbose("EyesTargetLocator.defaultContent()");
        if (driver.getFrameChain().size() != 0) {
            logger.verbose("Making preparations...");
            driver.getFrameChain().clear();
            logger.verbose("Done! Switching to default content...");
            targetLocator.defaultContent();
            logger.verbose("Done!");
        }
        return driver;
    }

    public WebElement activeElement() {
        logger.verbose("EyesTargetLocator.activeElement()");
        logger.verbose("Switching to element...");
        WebElement element = targetLocator.activeElement();
        if (!(element instanceof RemoteWebElement)) {
            throw new EyesException("Not a remote web element!");
        }
        EyesRemoteWebElement result = new EyesRemoteWebElement(logger, driver, (RemoteWebElement) element);
        logger.verbose("Done!");
        return result;
    }

    public Alert alert() {
        logger.verbose("EyesTargetLocator.alert()");
        logger.verbose("Switching to alert...");
        Alert result = targetLocator.alert();
        logger.verbose("Done!");
        return result;
    }
}