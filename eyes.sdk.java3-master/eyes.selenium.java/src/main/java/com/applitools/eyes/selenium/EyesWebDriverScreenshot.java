/*
 * Applitools software.
 */
package com.applitools.eyes.selenium;

import com.applitools.eyes.*;
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

    private static Location calcFrameLocationInScreenshot(Logger logger,
            FrameChain frameChain, ScreenshotType screenshotType) {

        logger.verbose("Getting first frame..");
        Iterator<Frame> frameIterator = frameChain.iterator();
        Frame firstFrame = frameIterator.next();
        logger.verbose("Done!");
        Location locationInScreenshot = new Location(firstFrame.getLocation());

        // We only consider scroll of the default content if this is
        // a viewport screenshot.
        if (screenshotType == ScreenshotType.VIEWPORT) {
            Location defaultContentScroll = firstFrame
                    .getParentScrollPosition();
            locationInScreenshot = locationInScreenshot.offset(-defaultContentScroll.getX(),
                    -defaultContentScroll.getY());
        }

        logger.verbose("Iterating over frames..");
        Frame frame;
        while (frameIterator.hasNext()) {
            logger.verbose("Getting next frame...");
            frame = frameIterator.next();
            logger.verbose("Done!");
            Location frameLocation = frame.getLocation();
            // For inner frames we must consider the scroll
            Location frameParentScrollPosition = frame
                    .getParentScrollPosition();
            // Offsetting the location in the screenshot
            locationInScreenshot = locationInScreenshot.offset(
                    frameLocation.getX() - frameParentScrollPosition.getX(),
                    frameLocation.getY() - frameParentScrollPosition.getY());
        }
        logger.verbose("Done!");
        return locationInScreenshot;
    }

    /**
     * @param logger                     A Logger instance.
     * @param driver                     The web driver used to get the screenshot.
     * @param image                      The actual screenshot image.
     * @param screenshotType             (Optional) The screenshot's type (e.g.,
     *                                   viewport/full page).
     * @param frameLocationInScreenshot  (Optional) The current frame's
     *                                   location in the screenshot.
     */
    public EyesWebDriverScreenshot(Logger logger, EyesWebDriver driver,
                                   BufferedImage image,
                                   ScreenshotType screenshotType,
                                   Location frameLocationInScreenshot) {
        super(image);
        ArgumentGuard.notNull(logger, "logger");
        ArgumentGuard.notNull(driver, "driver");
        this.logger = logger;
        this.driver = driver;
        ScrollPositionProvider positionProvider =
                new ScrollPositionProvider(logger, driver);
        RectangleSize viewportSize = driver.getDefaultContentViewportSize();
        frameChain = driver.getFrameChain();
        // If we're inside a frame, then the frame size is given by the frame
        // chain. Otherwise, it's the size of the entire page.
        RectangleSize frameSize;
        if (frameChain.size() != 0) {
            frameSize = frameChain.getCurrentFrameSize();
        } else {
            // get entire page size might throw an exception for applications
            // which don't support Javascript (e.g., Appium). In that case
            // we'll use the viewport size as the frame's size.
            RectangleSize fs;
            try {
                fs = positionProvider.getEntireSize();
            } catch (EyesDriverOperationException e) {
                fs = viewportSize;
            }
            frameSize = fs;
        }
        // Getting the scroll position. For native Appium apps we can't get the
        // scroll position, so we use (0,0)
        Location sp;
        try {
            sp = positionProvider.getCurrentPosition();
        } catch (EyesDriverOperationException e) {
            sp = new Location(0, 0);
        }
        currentFrameScrollPosition = sp;

        if (screenshotType == null) {
            if (image.getWidth() <= viewportSize.getWidth()
                    && image.getHeight() <= viewportSize.getHeight()) {
                screenshotType = ScreenshotType.VIEWPORT;
            } else {
                screenshotType = ScreenshotType.ENTIRE_FRAME;
            }
        }
        this.screenshotType = screenshotType;

        // This is used for frame related calculations.
        if (frameLocationInScreenshot == null) {
            if (frameChain.size() > 0) {
                frameLocationInScreenshot =
                        calcFrameLocationInScreenshot(logger, frameChain,
                                this.screenshotType);
            } else {
                frameLocationInScreenshot = new Location(0, 0);
            }
        }
        this.frameLocationInScreenshot = frameLocationInScreenshot;

        logger.verbose("Calculating frame window..");
        this.frameWindow = new Region(frameLocationInScreenshot, frameSize);
        this.frameWindow.intersect(new Region(0, 0, image.getWidth(),
                image.getHeight()));
        if (this.frameWindow.getWidth() <= 0 ||
                this.frameWindow.getHeight() <= 0) {
            throw new EyesException("Got empty frame window for screenshot!");
        }

        logger.verbose("Done!");
    }

    /**
     * See {@link #EyesWebDriverScreenshot(Logger, EyesWebDriver, BufferedImage, ScreenshotType, Location)}.
     * {@code screenshotType} defaults to {@code null}.
     * {@code frameLocationInScreenshot} defaults to {@code null}.
     *
     * @param logger A Logger instance.
     * @param driver The web driver used to get the screenshot.
     * @param image The actual screenshot image.
     */
    public EyesWebDriverScreenshot(Logger logger, EyesWebDriver driver,
                                   BufferedImage image) {
        this(logger, driver, image, null, null);
    }

    // TODO replace "entireFrameSize" as frame window ctor identifier
    /**
     * Creates a frame(!) window screenshot.
     * @param logger A Logger instance.
     * @param driver The web driver used to get the screenshot.
     * @param image The actual screenshot image.
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
    public EyesWebDriverScreenshot getSubScreenshot(Region region,
            CoordinatesType coordinatesType, boolean throwIfClipped) {

        logger.verbose(String.format("getSubScreenshot([%s], %s, %b)",
                region, coordinatesType, throwIfClipped));

        ArgumentGuard.notNull(region, "region");
        ArgumentGuard.notNull(coordinatesType, "coordinatesType");

        // We calculate intersection based on as-is coordinates.
        Region asIsSubScreenshotRegion = getIntersectedRegion(region,
                coordinatesType, CoordinatesType.SCREENSHOT_AS_IS);

        if (asIsSubScreenshotRegion.isEmpty() ||
                (throwIfClipped &&
                        !asIsSubScreenshotRegion.getSize().equals(
                                region.getSize()))) {
            throw new OutOfBoundsException(String.format(
                    "Region [%s, (%s)] is out of screenshot bounds [%s]",
                    region, coordinatesType, frameWindow));
        }

        BufferedImage subScreenshotImage =
                ImageUtils.getImagePart(image, asIsSubScreenshotRegion);

        // The frame location in the sub screenshot is the negative of the
        // context-as-is location of the region.
        Location contextAsIsRegionLocation =
                convertLocation(asIsSubScreenshotRegion.getLocation(),
                        CoordinatesType.SCREENSHOT_AS_IS,
                        CoordinatesType.CONTEXT_AS_IS);

        Location frameLocationInSubScreenshot =
                new Location(-contextAsIsRegionLocation.getX(),
                        -contextAsIsRegionLocation.getY());

        EyesWebDriverScreenshot result = new EyesWebDriverScreenshot(logger,
                driver, subScreenshotImage, screenshotType,
                frameLocationInSubScreenshot);
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
                            || to == CoordinatesType.CONTEXT_AS_IS)){

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
                                          CoordinatesType originalCoordinatesType,
                                          CoordinatesType resultCoordinatesType) {

        if (region.isEmpty()) {
            return new Region(region);
        }

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
        if(intersectedRegion.isEmpty()) {
            return intersectedRegion;
        }

        // Converting the result to the required coordinates type.
        intersectedRegion = convertRegionLocation(intersectedRegion,
                CoordinatesType.SCREENSHOT_AS_IS, resultCoordinatesType);

        return intersectedRegion;
    }

    @SuppressWarnings("UnusedDeclaration")
    /**
     * Gets the elements region in the screenshot.
     *
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
