/*
 * Applitools software.
 */
package com.applitools.eyes.selenium.capture;

import com.applitools.eyes.*;
import com.applitools.eyes.exceptions.CoordinatesTypeConversionException;
import com.applitools.eyes.positioning.PositionProvider;
import com.applitools.eyes.selenium.*;
import com.applitools.eyes.selenium.exceptions.EyesDriverOperationException;
import com.applitools.eyes.selenium.frames.Frame;
import com.applitools.eyes.selenium.frames.FrameChain;
import com.applitools.eyes.selenium.positioning.ScrollPositionProvider;
import com.applitools.eyes.selenium.wrappers.EyesTargetLocator;
import com.applitools.eyes.selenium.wrappers.EyesWebDriver;
import com.applitools.utils.ArgumentGuard;
import com.applitools.utils.ImageUtils;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebElement;

import java.awt.image.BufferedImage;
import java.util.Iterator;

public class EyesWebDriverScreenshot extends EyesScreenshot {

    private enum ScreenshotType {VIEWPORT, ENTIRE_FRAME}

    private final Logger logger;
    private final EyesWebDriver driver;
    private final FrameChain frameChain;
    private final Location currentFrameScrollPosition;
    private final ScreenshotType screenshotType;

    // The top/left coordinates of the frame window(!) relative to the top/left
    // of the screenshot. Used for calculations, so can also be outside(!)
    // the screenshot.
    private final Location frameLocationInScreenshot;

    // The part of the frame window which is visible in the screenshot
    private final Region frameWindow;

    private static Location getDefaultContentScrollPosition(Logger logger, FrameChain currentFrames, EyesWebDriver driver) {
        IEyesJsExecutor jsExecutor = new SeleniumJavaScriptExecutor(driver);
        PositionProvider positionProvider = new ScrollPositionProvider(logger, jsExecutor);
        if (currentFrames.size() == 0) {
            return positionProvider.getCurrentPosition();
        }

        FrameChain originalFC = new FrameChain(logger, currentFrames);

        EyesTargetLocator switchTo = (EyesTargetLocator)driver.switchTo();
        switchTo.defaultContent();
        Location defaultContentScrollPosition = positionProvider.getCurrentPosition();
        switchTo.frames(originalFC);

        return defaultContentScrollPosition;
    }

    public static Location calcFrameLocationInScreenshot(Logger logger, EyesWebDriver driver,
                                                         FrameChain frameChain, ScreenshotType screenshotType) {

        Location windowScroll = getDefaultContentScrollPosition(logger, frameChain, driver);

        logger.verbose("Getting first frame...");
        Iterator<Frame> frameIterator = frameChain.iterator();
        Frame firstFrame = frameIterator.next();
        logger.verbose("Done!");
        Location locationInScreenshot = new Location(firstFrame.getLocation());

        // We only consider scroll of the default content if this is a viewport screenshot.
        if (screenshotType == ScreenshotType.VIEWPORT) {
            locationInScreenshot = locationInScreenshot.offset(-windowScroll.getX(), -windowScroll.getY());
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
            // Offsetting the location in the screenshot
            locationInScreenshot = locationInScreenshot.offset(
                    frameLocation.getX() - frameOriginalLocation.getX(),
                    frameLocation.getY() - frameOriginalLocation.getY());
        }
        logger.verbose("Done!");

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
        super(image);
        ArgumentGuard.notNull(logger, "logger");
        ArgumentGuard.notNull(driver, "driver");
        this.logger = logger;
        this.driver = driver;

        this.screenshotType = updateScreenshotType(screenshotType, image);

        PositionProvider positionProvider = driver.getEyes().getPositionProvider();

        frameChain = driver.getFrameChain();
        RectangleSize frameSize = getFrameSize(positionProvider);
        currentFrameScrollPosition = getUpdatedScrollPosition(positionProvider);
        frameLocationInScreenshot = getUpdatedFrameLocationInScreenshot(logger, frameLocationInScreenshot);

        this.frameLocationInScreenshot = frameLocationInScreenshot;

        logger.verbose("Calculating frame window...");
        this.frameWindow = new Region(frameLocationInScreenshot, frameSize);
        this.frameWindow.intersect(new Region(0, 0, image.getWidth(), image.getHeight()));
        if (this.frameWindow.getWidth() <= 0 || this.frameWindow.getHeight() <= 0) {
            throw new EyesException("Got empty frame window for screenshot!");
        }

        logger.verbose("Done!");
    }

    private Location getUpdatedFrameLocationInScreenshot(Logger logger, Location frameLocationInScreenshot) {
        logger.verbose(String.format("frameLocationInScreenshot: %s", frameLocationInScreenshot));
        if (frameChain.size() > 0) {
            frameLocationInScreenshot = calcFrameLocationInScreenshot(logger, this.driver, frameChain, this.screenshotType);
        } else if (frameLocationInScreenshot == null) {
            frameLocationInScreenshot = new Location(0, 0);
        }
        return frameLocationInScreenshot;
    }

    private static Location getUpdatedScrollPosition(PositionProvider positionProvider) {
        Location sp;
        try {
            sp = positionProvider.getCurrentPosition();
            if (sp == null) {
                sp = new Location(0,0);
            }
        } catch (EyesDriverOperationException e) {
            sp = new Location(0, 0);
        }
        return sp;
    }

    private RectangleSize getFrameSize(PositionProvider positionProvider) {
        RectangleSize frameSize;
        if (frameChain.size() != 0) {
            frameSize = frameChain.getCurrentFrameInnerSize();
        } else {
            // get entire page size might throw an exception for applications
            // which don't support Javascript (e.g., Appium). In that case
            // we'll use the viewport size as the frame's size.
            try {
                frameSize = positionProvider.getEntireSize();
            } catch (EyesDriverOperationException e) {
                frameSize = this.driver.getDefaultContentViewportSize();
            }
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
        this(logger, driver, image, null, null);
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
        super(image);
        ArgumentGuard.notNull(driver, "logger");
        ArgumentGuard.notNull(driver, "driver");
        ArgumentGuard.notNull(entireFrameSize, "entireFrameSize");
        this.logger = logger;
        this.driver = driver;
        frameChain = driver.getFrameChain();
        // The frame comprises the entire screenshot.
        screenshotType = ScreenshotType.ENTIRE_FRAME;

        currentFrameScrollPosition = new Location(0, 0);
        frameLocationInScreenshot = new Location(0, 0);
        frameWindow = new Region(new Location(0, 0), entireFrameSize);
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
                new RectangleSize(subScreenshotImage.getWidth(), subScreenshotImage.getHeight()));

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

        if (region.isEmpty()) {
            return new Region(region);
        }

        CoordinatesType originalCoordinatesType = region.getCoordinatesType();

        Region intersectedRegion = convertRegionLocation(region,
                originalCoordinatesType, CoordinatesType.SCREENSHOT_AS_IS);

        switch (originalCoordinatesType) {
            // If the request was context based, we intersect with the frame
            // window.
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
        if (intersectedRegion.isEmpty()) {
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

        if (!elementRegion.isEmpty()) {
            elementRegion = convertRegionLocation(elementRegion,
                    CoordinatesType.CONTEXT_RELATIVE,
                    CoordinatesType.SCREENSHOT_AS_IS);
        }

        return elementRegion;
    }
}
