/*
 * Applitools software.
 */
package com.applitools.eyes.selenium.capture;

import com.applitools.eyes.*;
import com.applitools.eyes.exceptions.CoordinatesTypeConversionException;
import com.applitools.eyes.positioning.PositionProvider;
import com.applitools.eyes.selenium.*;
import com.applitools.eyes.selenium.frames.Frame;
import com.applitools.eyes.selenium.frames.FrameChain;
import com.applitools.eyes.selenium.positioning.ScrollPositionProvider;
import com.applitools.eyes.selenium.positioning.ScrollPositionProviderFactory;
import com.applitools.eyes.selenium.wrappers.EyesRemoteWebElement;
import com.applitools.eyes.selenium.wrappers.EyesTargetLocator;
import com.applitools.eyes.selenium.wrappers.EyesWebDriver;
import com.applitools.utils.ArgumentGuard;
import com.applitools.utils.ImageUtils;
import org.openqa.selenium.*;

import java.awt.image.BufferedImage;
import java.util.Iterator;

public class EyesWebDriverScreenshot extends EyesScreenshot {

    public enum ScreenshotType {VIEWPORT, ENTIRE_FRAME}

    private final EyesWebDriver driver;
    private final FrameChain frameChain;
    private  Location currentFrameScrollPosition;
    private  ScreenshotType screenshotType;

    // The top/left coordinates of the frame window(!) relative to the top/left
    // of the screenshot. Used for calculations, so can also be outside(!)
    // the screenshot.
    private Location frameLocationInScreenshot;

    // The part of the frame window which is visible in the screenshot
    private  Region frameWindow;

    // FIXME: 18/03/2018 Workaround specifically for regions
    //private final Region regionWindow;

    private static Location getDefaultContentScrollPosition(Logger logger, FrameChain currentFrames, EyesWebDriver driver) {
        IEyesJsExecutor jsExecutor = new SeleniumJavaScriptExecutor(driver);
        Location defaultContentScrollPosition;
        if (currentFrames.size() == 0) {
            defaultContentScrollPosition = getDefaultContentScrollPosition(logger, driver, jsExecutor);
        } else {
            FrameChain originalFC = new FrameChain(logger, currentFrames);

            EyesTargetLocator switchTo = (EyesTargetLocator) driver.switchTo();
            FrameChain currentFC = driver.getEyes().getOriginalFC();
            switchTo.frames(currentFC);
            defaultContentScrollPosition = getDefaultContentScrollPosition(logger, driver, jsExecutor);
            switchTo.frames(originalFC);
        }
        return defaultContentScrollPosition;
    }

    private static Location getDefaultContentScrollPosition(Logger logger, EyesWebDriver driver, IEyesJsExecutor jsExecutor) {
        WebElement scrollRootElement = driver.getEyes().getCurrentFrameScrollRootElement();
        PositionProvider positionProvider = ScrollPositionProviderFactory.getScrollPositionProvider(driver.getUserAgent(), logger, jsExecutor, scrollRootElement);
        return positionProvider.getCurrentPosition();
    }

    public static Location calcFrameLocationInScreenshot(Logger logger, EyesWebDriver driver,
                                                         FrameChain frameChain, ScreenshotType screenshotType) {

        Location windowScroll = getDefaultContentScrollPosition(logger, frameChain, driver);

        logger.verbose("windowScroll: " + windowScroll);

        logger.verbose("Getting first frame...");
        Iterator<Frame> frameIterator = frameChain.iterator();
        Frame firstFrame = frameIterator.next();
        Location locationInScreenshot = new Location(firstFrame.getLocation());
        logger.verbose("Done! locationInScreenshot: " + locationInScreenshot);

        // We only consider scroll of the default content if this is a viewport screenshot.
        if (screenshotType == ScreenshotType.VIEWPORT) {
            locationInScreenshot = locationInScreenshot.offset(-windowScroll.getX(), -windowScroll.getY());
            logger.verbose("updated locationInScreenshot: " + locationInScreenshot);
        }

        logger.verbose("Iterating over frames...");
        Frame frame;
        while (frameIterator.hasNext()) {
            logger.verbose("Getting next frame...");
            frame = frameIterator.next();
            logger.verbose("Done!");
            Location frameLocation = frame.getLocation();
            // For inner frames we must consider the scroll
            Location frameOriginalLocation = frame.getOriginalLocation();
            logger.verbose("Done! frameLocation: " + frameLocation + " ; frameOriginalLocation: " + frameOriginalLocation);
            // Offsetting the location in the screenshot
            locationInScreenshot = locationInScreenshot.offset(
                    frameLocation.getX() - frameOriginalLocation.getX(),
                    frameLocation.getY() - frameOriginalLocation.getY());
            logger.verbose("updated locationInScreenshot: " + locationInScreenshot);
        }
        logger.verbose("Done! final locationInScreenshot: " + locationInScreenshot);

        return locationInScreenshot;
    }

    /**
     * @param logger                    A Logger instance.
     * @param driver                    The web driver used to get the screenshot.
     * @param image                     The actual screenshot image.
     * @param screenshotType            (Optional) The screenshot's type (e.g., viewport/full page).
     * @param frameLocationInScreenshot (Optional) The current frame's location in the screenshot.
     */
    public EyesWebDriverScreenshot(Logger logger, EyesWebDriver driver, BufferedImage image,
                                   ScreenshotType screenshotType, Location frameLocationInScreenshot) {
        super(logger, image);
        ArgumentGuard.notNull(logger, "logger");
        ArgumentGuard.notNull(driver, "driver");
        this.driver = driver;

        logger.verbose("enter");

        updateScreenshotType(screenshotType, image);

        PositionProvider positionProvider;
        if (frameLocationInScreenshot == null && driver.getEyes().checkFrameOrElement)
        {
            WebElement frameScrollRoot = driver.getEyes().getCurrentFrameScrollRootElement();
            positionProvider = SeleniumScrollPositionProviderFactory.getPositionProvider(logger, driver.getEyes().getStitchMode(), driver, frameScrollRoot, null);
            logger.verbose(String.format("position provider: using the current frame scroll root element's position provider: %s", positionProvider));
        }
        else if (driver.getEyes().getCurrentFramePositionProvider() != null)
        {
            positionProvider = driver.getEyes().getCurrentFramePositionProvider();
            logger.verbose(String.format("position provider: using CurrentFramePositionProvider: %s", positionProvider));
        }
        else
        {
            positionProvider = driver.getEyes().getPositionProvider();
            logger.verbose(String.format("position provider: using PositionProvider: %s", positionProvider));
        }

        //IPositionProvider positionProvider = driver.Eyes.CurrentFramePositionProvider ?? driver.Eyes.PositionProvider;

        frameChain = driver.getFrameChain();
        logger.verbose("got frame chain. getting frame size...");
        RectangleSize frameSize = getFrameSize(positionProvider);
        updateCurrentScrollPosition(positionProvider);
        updateFrameLocationInScreenshot(frameLocationInScreenshot);
        RectangleSize frameContentSize = getFrameContentSize();

        logger.verbose("Calculating frame window...");
        frameWindow = new Region(this.frameLocationInScreenshot, frameContentSize);
        Region imageSizeAsRegion = new Region(0, 0, image.getWidth(), image.getHeight());
        logger.verbose(String.format("frameWindow_: {0} ; imageSizeAsRegion: %s", frameWindow, imageSizeAsRegion));
        frameWindow.intersect(imageSizeAsRegion);
        logger.verbose(String.format("updated frameWindow_: {0}", frameWindow));

        if (frameWindow.getWidth() <= 0 || frameWindow.getHeight() <= 0)
        {
            throw new EyesException("Got empty frame window for screenshot!");
        }

        logger.verbose("Done!");
    }
    private RectangleSize getFrameContentSize()
    {
        EyesRemoteWebElement frameDocumentElement = (EyesRemoteWebElement)driver.findElement(By.tagName("html"));
        return frameDocumentElement.getClientSize();
    }
    private void updateCurrentScrollPosition(PositionProvider positionProvider)
    {
        // Getting the scroll position. For native Appium apps we can't get the
        // scroll position, so we use (0,0)
        try
        {
            currentFrameScrollPosition = positionProvider.getCurrentPosition();
        }
        catch (Exception e)
        {
            currentFrameScrollPosition = new Location(0, 0);
        }
    }

    private void updateFrameLocationInScreenshot(Location location) {
        if (location == null) {
            if (frameChain.size() > 0) {
                frameLocationInScreenshot = calcFrameLocationInScreenshot(logger, this.driver, frameChain, this.screenshotType);
            } else {
                frameLocationInScreenshot = new Location(0, 0);
            }
        } else {
            this.frameLocationInScreenshot = location;
        }
    }

    private static Location getUpdatedScrollPosition(PositionProvider positionProvider) {
        Location sp;
        try {
            sp = positionProvider.getCurrentPosition();
            if (sp == null) {
                sp = new Location(0, 0);
            }
        } catch (Exception e) {
            sp = new Location(0, 0);
        }
        return sp;
    }

    private RectangleSize getFrameSize(PositionProvider positionProvider) {
        RectangleSize frameSize;
        if (frameChain.size() != 0) {
            frameSize = frameChain.getCurrentFrameInnerSize();
        } else if (!EyesSeleniumUtils.isMobileDevice(driver)) {
            frameSize = positionProvider.getEntireSize();
        } else {
            frameSize = this.driver.getDefaultContentViewportSize();
        }
        return frameSize;
    }

    private ScreenshotType updateScreenshotType(ScreenshotType screenshotType, BufferedImage image) {
        if (screenshotType == null) {
            RectangleSize viewportSize = driver.getEyes().getViewportSize();

            boolean scaleViewport = driver.getEyes().shouldStitchContent();

            if (scaleViewport) {
                double pixelRatio = driver.getEyes().getDevicePixelRatio();
                viewportSize = viewportSize.scale(pixelRatio);
            }

            if (image.getWidth() <= viewportSize.getWidth() && image.getHeight() <= viewportSize.getHeight()) {
                screenshotType = ScreenshotType.VIEWPORT;
            } else {
                screenshotType = ScreenshotType.ENTIRE_FRAME;
            }
        }
        return screenshotType;
    }

    /**
     * See {@link #EyesWebDriverScreenshot(Logger, EyesWebDriver, BufferedImage, ScreenshotType, Location)}.
     * {@code screenshotType} defaults to {@code null}.
     * {@code frameLocationInScreenshot} defaults to {@code null}.
     * @param logger A Logger instance.
     * @param driver The web driver used to get the screenshot.
     * @param image  The actual screenshot image.
     */
    public EyesWebDriverScreenshot(Logger logger, EyesWebDriver driver, BufferedImage image) {
        this(logger, driver, image, ScreenshotType.VIEWPORT, null);
    }

    // FIXME: 18/03/2018 This is a workaround done for handling checkRegion.

    /**
     * Creates a frame-like window screenshot, to be used for checkRegion screenshots.
     * @param logger           A Logger instance.
     * @param driver           The web driver used to get the screenshot.
     * @param image            The actual screenshot image.
     * @param screenshotRegion The region of the screenshot.
     */
    public EyesWebDriverScreenshot(Logger logger, EyesWebDriver driver,
                                   BufferedImage image, Region screenshotRegion) {
        super(logger, image);
        ArgumentGuard.notNull(driver, "driver");
        ArgumentGuard.notNull(screenshotRegion, "screenshotRegion");

        this.driver = driver;
        frameChain = driver.getFrameChain();
        // The frame comprises the entire screenshot.
        screenshotType = ScreenshotType.ENTIRE_FRAME;

        currentFrameScrollPosition = new Location(0, 0);
        frameLocationInScreenshot = new Location(0, 0);
        frameWindow = new Region(new Location(0, 0), screenshotRegion.getSize());
        //regionWindow = new Region(screenshotRegion);
    }

    // TODO replace "entireFrameSize" as frame window ctor identifier

    /**
     * Creates a frame(!) window screenshot.
     * @param logger          A Logger instance.
     * @param driver          The web driver used to get the screenshot.
     * @param image           The actual screenshot image.
     * @param entireFrameSize The full internal size of the frame.
     */
    public EyesWebDriverScreenshot(Logger logger, EyesWebDriver driver,
                                   BufferedImage image,
                                   RectangleSize entireFrameSize) {
        super(logger, image);
        ArgumentGuard.notNull(driver, "driver");
        ArgumentGuard.notNull(entireFrameSize, "entireFrameSize");
        this.driver = driver;
        frameChain = driver.getFrameChain();
        // The frame comprises the entire screenshot.
        screenshotType = ScreenshotType.ENTIRE_FRAME;

        currentFrameScrollPosition = new Location(0, 0);
        frameLocationInScreenshot = new Location(0, 0);
        frameWindow = new Region(new Location(0, 0), entireFrameSize);
        //regionWindow = new Region(0, 0, 0, 0); // FIXME: 18/03/2018 Region workaround
    }

    /**
     * @return The region of the frame which is available in the screenshot,
     * in screenshot coordinates.
     */
    public Region getFrameWindow() {
        return frameWindow;
    }

    /**
     * @return A copy of the frame chain which was available when the
     * screenshot was created.
     */
    public FrameChain getFrameChain() {
        return new FrameChain(logger, frameChain);
    }

    @Override
    public EyesWebDriverScreenshot getSubScreenshot(Region region, boolean throwIfClipped) {

        logger.verbose(String.format("getSubScreenshot([%s], %b)", region, throwIfClipped));

        ArgumentGuard.notNull(region, "region");

        // We calculate intersection based on as-is coordinates.
        Region asIsSubScreenshotRegion = getIntersectedRegion(region, CoordinatesType.SCREENSHOT_AS_IS);

        if (asIsSubScreenshotRegion.isSizeEmpty() ||
                (throwIfClipped &&
                        !asIsSubScreenshotRegion.getSize().equals(
                                region.getSize()))) {
            throw new OutOfBoundsException(String.format(
                    "Region [%s] is out of screenshot bounds [%s]",
                    region, frameWindow));
        }

        BufferedImage subScreenshotImage =
                ImageUtils.getImagePart(image, asIsSubScreenshotRegion);

        EyesWebDriverScreenshot result = new EyesWebDriverScreenshot(logger, driver, subScreenshotImage,
                new Region(region.getLeft(), region.getTop(), subScreenshotImage.getWidth(), subScreenshotImage.getHeight()));

        result.updateFrameLocationInScreenshot(new Location(-region.getLeft(), -region.getTop()));
        result.setDomUrl(this.domUrl);
        logger.verbose("Done!");
        return result;
    }

    public EyesWebDriverScreenshot getSubScreenshotForRegion(Region region, boolean throwIfClipped) {

        logger.verbose(String.format("getSubScreenshot([%s], %b)", region, throwIfClipped));

        ArgumentGuard.notNull(region, "region");

        // We calculate intersection based on as-is coordinates.
        Region asIsSubScreenshotRegion = getIntersectedRegion(region, CoordinatesType.SCREENSHOT_AS_IS);

        if (asIsSubScreenshotRegion.isEmpty() ||
                (throwIfClipped &&
                        !asIsSubScreenshotRegion.getSize().equals(
                                region.getSize()))) {
            throw new OutOfBoundsException(String.format(
                    "Region [%s] is out of screenshot bounds [%s]",
                    region, frameWindow));
        }

        BufferedImage subScreenshotImage =
                ImageUtils.getImagePart(image, asIsSubScreenshotRegion);

        EyesWebDriverScreenshot result = new EyesWebDriverScreenshot(logger, driver, subScreenshotImage,
                new Region(region.getLocation(),
                        new RectangleSize(subScreenshotImage.getWidth(), subScreenshotImage.getHeight())));

        logger.verbose("Done!");
        return result;
    }

    @Override
    public Location convertLocation(Location location,
                                    CoordinatesType from, CoordinatesType to) {

        ArgumentGuard.notNull(location, "location");
        ArgumentGuard.notNull(from, "from");
        ArgumentGuard.notNull(to, "to");

        Location result = new Location(location);

        if (from == to) {
            return result;
        }

        // If we're not inside a frame, and the screenshot is the entire
        // page, then the context as-is/relative are the same (notice
        // screenshot as-is might be different, e.g.,
        // if it is actually a sub-screenshot of a region).
        if (frameChain.size() == 0 &&
                screenshotType == ScreenshotType.ENTIRE_FRAME) {
            if ((from == CoordinatesType.CONTEXT_RELATIVE
                    || from == CoordinatesType.CONTEXT_AS_IS)
                    && to == CoordinatesType.SCREENSHOT_AS_IS) {

                // If this is not a sub-screenshot, this will have no effect.
                result = result.offset(frameLocationInScreenshot.getX(),
                        frameLocationInScreenshot.getY());

//                // FIXME: 18/03/2018 Region workaround
//                // If this is not a region subscreenshot, this will have no effect.
//                result = result.offset(-regionWindow.getLeft(), -regionWindow.getTop());

            } else if (from == CoordinatesType.SCREENSHOT_AS_IS &&
                    (to == CoordinatesType.CONTEXT_RELATIVE
                            || to == CoordinatesType.CONTEXT_AS_IS)) {

                result = result.offset(-frameLocationInScreenshot.getX(),
                        -frameLocationInScreenshot.getY());
            }
            return result;
        }

        switch (from) {
            case CONTEXT_AS_IS:
                switch (to) {
                    case CONTEXT_RELATIVE:
                        result = result.offset(currentFrameScrollPosition.getX(),
                                currentFrameScrollPosition.getY());
                        break;

                    case SCREENSHOT_AS_IS:
                        result = result.offset(frameLocationInScreenshot.getX(),
                                frameLocationInScreenshot.getY());
                        break;

                    default:
                        throw new CoordinatesTypeConversionException(from, to);
                }
                break;

            case CONTEXT_RELATIVE:
                switch (to) {
                    case SCREENSHOT_AS_IS:

                        // First, convert context-relative to context-as-is.
                        result = result.offset(-currentFrameScrollPosition.getX(),
                                -currentFrameScrollPosition.getY());
                        // Now convert context-as-is to screenshot-as-is.
                        result = result.offset(frameLocationInScreenshot.getX(),
                                frameLocationInScreenshot.getY());

                        break;

                    case CONTEXT_AS_IS:
                        result = result.offset(-currentFrameScrollPosition.getX(),
                                -currentFrameScrollPosition.getY());
                        break;

                    default:
                        throw new CoordinatesTypeConversionException(from, to);
                }
                break;

            case SCREENSHOT_AS_IS:
                switch (to) {
                    case CONTEXT_RELATIVE:
                        // First convert to context-as-is.
                        result = result.offset(-frameLocationInScreenshot.getX(),
                                -frameLocationInScreenshot.getY());
                        // Now convert to context-relative.
                        result = result.offset(currentFrameScrollPosition.getX(),
                                currentFrameScrollPosition.getY());
                        break;

                    case CONTEXT_AS_IS:
                        result = result.offset(-frameLocationInScreenshot.getX(),
                                -frameLocationInScreenshot.getY());
                        break;

                    default:
                        throw new CoordinatesTypeConversionException(from, to);
                }
                break;

            default:
                throw new CoordinatesTypeConversionException(from, to);
        }
        return result;
    }

    @Override
    public Location getLocationInScreenshot(Location location,
                                            CoordinatesType coordinatesType) throws OutOfBoundsException {

        location = convertLocation(location, coordinatesType,
                CoordinatesType.SCREENSHOT_AS_IS);

        // Making sure it's within the screenshot bounds
        if (!frameWindow.contains(location)) {
            throw new OutOfBoundsException(String.format(
                    "Location %s ('%s') is not visible in screenshot!", location,
                    coordinatesType));
        }
        return location;
    }

    @Override
    public Region getIntersectedRegion(Region region,
                                       CoordinatesType resultCoordinatesType) {

        if (region.isSizeEmpty()) {
            return new Region(region);
        }

        CoordinatesType originalCoordinatesType = region.getCoordinatesType();

        Region intersectedRegion = convertRegionLocation(region,
                originalCoordinatesType, CoordinatesType.SCREENSHOT_AS_IS);

        switch (originalCoordinatesType) {
            // If the request was context based, we intersect with the frame window.
            case CONTEXT_AS_IS:
            case CONTEXT_RELATIVE:
                intersectedRegion.intersect(frameWindow);
                break;

            // If the request is screenshot based, we intersect with the image
            case SCREENSHOT_AS_IS:
                intersectedRegion.intersect(new Region(0, 0,
                        image.getWidth(), image.getHeight()));
                break;

            default:
                throw new CoordinatesTypeConversionException(
                        String.format("Unknown coordinates type: '%s'",
                                originalCoordinatesType));

        }

        // If the intersection is empty we don't want to convert the
        // coordinates.
        if (intersectedRegion.isSizeEmpty()) {
            return intersectedRegion;
        }

        // Converting the result to the required coordinates type.
        intersectedRegion = convertRegionLocation(intersectedRegion,
                CoordinatesType.SCREENSHOT_AS_IS, resultCoordinatesType);

        return intersectedRegion;
    }

    /**
     * Gets the elements region in the screenshot.
     * @param element The element which region we want to intersect.
     * @return The intersected region, in {@code SCREENSHOT_AS_IS} coordinates
     * type.
     */
    public Region getIntersectedRegion(WebElement element) {
        ArgumentGuard.notNull(element, "element");

        Point pl = element.getLocation();
        Dimension ds = element.getSize();

        Region elementRegion = new Region(pl.getX(), pl.getY(), ds.getWidth(),
                ds.getHeight());

        // Since the element coordinates are in context relative
        elementRegion = getIntersectedRegion(elementRegion,
                CoordinatesType.CONTEXT_RELATIVE);

        if (!elementRegion.isSizeEmpty()) {
            elementRegion = convertRegionLocation(elementRegion,
                    CoordinatesType.CONTEXT_RELATIVE,
                    CoordinatesType.SCREENSHOT_AS_IS);
        }

        return elementRegion;
    }
}
