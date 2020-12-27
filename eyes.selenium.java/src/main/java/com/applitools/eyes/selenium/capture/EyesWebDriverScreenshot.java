/*
 * Applitools software.
 */
package com.applitools.eyes.selenium.capture;

import com.applitools.eyes.*;
import com.applitools.eyes.exceptions.CoordinatesTypeConversionException;
import com.applitools.eyes.positioning.PositionProvider;
import com.applitools.eyes.selenium.Borders;
import com.applitools.eyes.selenium.EyesDriverUtils;
import com.applitools.eyes.selenium.EyesSeleniumUtils;
import com.applitools.eyes.selenium.SizeAndBorders;
import com.applitools.eyes.selenium.frames.Frame;
import com.applitools.eyes.selenium.frames.FrameChain;
import com.applitools.eyes.selenium.positioning.ScrollPositionProviderFactory;
import com.applitools.eyes.selenium.wrappers.EyesRemoteWebElement;
import com.applitools.eyes.selenium.wrappers.EyesTargetLocator;
import com.applitools.eyes.selenium.wrappers.EyesSeleniumDriver;
import com.applitools.utils.ArgumentGuard;
import com.applitools.utils.ImageUtils;
import org.openqa.selenium.WebElement;

import java.awt.image.BufferedImage;

public class EyesWebDriverScreenshot extends EyesScreenshot {

    public enum ScreenshotType {VIEWPORT, ENTIRE_FRAME}

    private final EyesSeleniumDriver driver;
    private final FrameChain frameChain;
    private Location currentFrameScrollPosition;
    private final ScreenshotType screenshotType;

    // The top/left coordinates of the frame window(!) relative to the top/left
    // of the screenshot. Used for calculations, so can also be outside(!)
    // the screenshot.
    private Location frameLocationInScreenshot;

    // The part of the frame window which is visible in the screenshot
    private final Region frameWindow;

    /**
     * @param logger                    A Logger instance.
     * @param driver                    The web driver used to get the screenshot.
     * @param image                     The actual screenshot image.
     * @param screenshotType            (Optional) The screenshot's type (e.g., viewport/full page).
     * @param frameLocationInScreenshot (Optional) The current frame's location in the screenshot.
     */
    public EyesWebDriverScreenshot(Logger logger, EyesSeleniumDriver driver, BufferedImage image,
                                   ScreenshotType screenshotType, Location frameLocationInScreenshot) {
        super(logger, image);
        ArgumentGuard.notNull(logger, "logger");
        ArgumentGuard.notNull(driver, "driver");
        this.driver = driver;

        this.screenshotType = updateScreenshotType(screenshotType, image);

        PositionProvider positionProvider;
        if (frameLocationInScreenshot == null && driver.getEyes().checkFrameOrElement) {
            WebElement frameScrollRoot = driver.getEyes().getCurrentFrameScrollRootElement();
            positionProvider = ScrollPositionProviderFactory.getScrollPositionProvider(driver.getUserAgent(), logger, driver, frameScrollRoot);
        } else if (driver.getEyes().getCurrentFramePositionProvider() != null) {
            positionProvider = driver.getEyes().getCurrentFramePositionProvider();
        } else {
            positionProvider = driver.getEyes().getPositionProvider();
        }

        frameChain = driver.getFrameChain();
        try {
            updateCurrentScrollPosition(positionProvider);
        } catch (Exception e) {
            e.printStackTrace();
        }
        updateFrameLocationInScreenshot(frameLocationInScreenshot);

        RectangleSize frameContentSize = getFrameContentSize();
        if (!frameContentSize.isEmpty()) {
            frameWindow = new Region(this.frameLocationInScreenshot, frameContentSize);
            Region imageSizeAsRegion = new Region(0, 0, image.getWidth(), image.getHeight());
            frameWindow.intersect(imageSizeAsRegion);
        } else {
            frameWindow = new Region(0, 0, image.getWidth(), image.getHeight());
        }

        if (frameWindow.getWidth() <= 0 || frameWindow.getHeight() <= 0) {
            throw new EyesException("Got empty frame window for screenshot!");
        }
    }

    private void updateCurrentScrollPosition(PositionProvider positionProvider) {
        // Getting the scroll position. For native Appium apps we can't get the
        // scroll position, so we use (0,0)
        try {
            currentFrameScrollPosition = positionProvider.getCurrentPosition();
        } catch (Exception e) {
            currentFrameScrollPosition = new Location(0, 0);
        }
    }

    private RectangleSize getFrameContentSize() {
        if (EyesDriverUtils.isMobileDevice(driver)) {
            return new RectangleSize(0, 0);
        }

        EyesRemoteWebElement frameDocumentElement = (EyesRemoteWebElement) EyesSeleniumUtils.getDefaultRootElement(logger, driver);
        return frameDocumentElement.getClientSize();
    }

    public EyesWebDriverScreenshot(Logger logger, EyesSeleniumDriver driver, BufferedImage image, RectangleSize entireFrameSize, Location frameLocationInScreenshot) {
        super(logger, image);
        ArgumentGuard.notNull(driver, "driver");
        ArgumentGuard.notNull(entireFrameSize, "entireFrameSize");
        this.driver = driver;
        this.frameChain = driver.getFrameChain();
        // The frame comprises the entire screenshot.
        this.screenshotType = ScreenshotType.ENTIRE_FRAME;
        this.currentFrameScrollPosition = new Location(0, 0);
        this.frameLocationInScreenshot = frameLocationInScreenshot;
        this.frameWindow = new Region(new Location(0, 0), entireFrameSize);
    }

    public static Location calcFrameLocationInScreenshot(Logger logger, EyesSeleniumDriver driver, FrameChain frameChain) {
        EyesTargetLocator switchTo = (EyesTargetLocator) driver.switchTo();
        FrameChain currentFC = frameChain.clone();
        switchTo.defaultContent();
        Location locationInScreenshot = new Location(0, 0);
        for (Frame frame : currentFC) {
            EyesRemoteWebElement eyesFrame;
            if (frame.getReference() instanceof EyesRemoteWebElement) {
                eyesFrame = (EyesRemoteWebElement) frame.getReference();
            } else {
                eyesFrame = new EyesRemoteWebElement(logger, driver, frame.getReference());
            }
            org.openqa.selenium.Rectangle rect = eyesFrame.getBoundingClientRect();
            SizeAndBorders sizeAndBorders = eyesFrame.getSizeAndBorders();
            Borders borders = sizeAndBorders.getBorders();
            rect.setX(rect.getX() + borders.getLeft());
            rect.setY(rect.getY() + borders.getTop());
            locationInScreenshot = locationInScreenshot.offset(rect.getX(), rect.getY());
            switchTo.frame(frame.getReference());
        }

        return locationInScreenshot;
    }


    private void updateFrameLocationInScreenshot(Location location) {
        if (location == null) {
            if (frameChain.size() > 0) {
                frameLocationInScreenshot = calcFrameLocationInScreenshot(logger, this.driver, frameChain);
            } else {
                frameLocationInScreenshot = new Location(0, 0);
            }
        } else {
            this.frameLocationInScreenshot = location;
        }
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
     * See {@link #EyesWebDriverScreenshot(Logger, EyesSeleniumDriver, BufferedImage, ScreenshotType, Location)}.
     * {@code screenshotType} defaults to {@code null}.
     * {@code frameLocationInScreenshot} defaults to {@code null}.
     * @param logger A Logger instance.
     * @param driver The web driver used to get the screenshot.
     * @param image  The actual screenshot image.
     */
    public EyesWebDriverScreenshot(Logger logger, EyesSeleniumDriver driver, BufferedImage image) {
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
    public EyesWebDriverScreenshot(Logger logger, EyesSeleniumDriver driver,
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
    public EyesWebDriverScreenshot(Logger logger, EyesSeleniumDriver driver,
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
        return result;
    }

    public EyesWebDriverScreenshot getSubScreenshotForRegion(Region region, boolean throwIfClipped) {
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

        return new EyesWebDriverScreenshot(logger, driver, subScreenshotImage,
                new Region(region.getLocation(),
                        new RectangleSize(subScreenshotImage.getWidth(), subScreenshotImage.getHeight())));
    }

    @Override
    public Location convertLocation(Location location,
                                    CoordinatesType from, CoordinatesType to) {

        ArgumentGuard.notNull(location, "location");
        ArgumentGuard.notNull(from, "from");
        ArgumentGuard.notNull(to, "to");

        if (from == to) {
            return location;
        }

        Location result = location;

        // If we're not inside a frame, and the screenshot is the entire
        // page, then the context as-is/relative are the same (notice
        // screenshot as-is might be different, e.g.,
        // if it is actually a sub-screenshot of a region).
        if (frameChain.size() == 0 && screenshotType == ScreenshotType.ENTIRE_FRAME) {
            if ((from == CoordinatesType.CONTEXT_RELATIVE || from == CoordinatesType.CONTEXT_AS_IS) && to == CoordinatesType.SCREENSHOT_AS_IS) {
                // If this is not a sub-screenshot, this will have no effect.
                result = result.offset((int) frameLocationInScreenshot.getX(), (int) frameLocationInScreenshot.getY());

            } else if (from == CoordinatesType.SCREENSHOT_AS_IS &&
                    (to == CoordinatesType.CONTEXT_RELATIVE || to == CoordinatesType.CONTEXT_AS_IS)) {
                result = result.offset(-(int) frameLocationInScreenshot.getX(), -(int) frameLocationInScreenshot.getY());
            }
            return result;
        }

        switch (from) {
            case CONTEXT_AS_IS:
                switch (to) {
                    case CONTEXT_RELATIVE:
                        result = result.offset(currentFrameScrollPosition);
                        break;

                    case SCREENSHOT_AS_IS:
                        result = result.offset((int) frameLocationInScreenshot.getX(), (int) frameLocationInScreenshot.getY());
                        break;

                    default:
                        throw new CoordinatesTypeConversionException(from, to);
                }
                break;

            case CONTEXT_RELATIVE:
                switch (to) {
                    case SCREENSHOT_AS_IS:
                        // First, convert context-relative to context-as-is.
                        result = result.offset(-currentFrameScrollPosition.getX(), -currentFrameScrollPosition.getY());
                        // Now convert context-as-is to screenshot-as-is.
                        result = result.offset(frameLocationInScreenshot.getX(), frameLocationInScreenshot.getY());
                        break;

                    case CONTEXT_AS_IS:
                        result = result.offset(-currentFrameScrollPosition.getX(), -currentFrameScrollPosition.getY());
                        break;

                    default:
                        throw new CoordinatesTypeConversionException(from, to);
                }
                break;

            case SCREENSHOT_AS_IS:
                switch (to) {
                    case CONTEXT_RELATIVE:
                        // First convert to context-as-is.
                        result = result.offset(-(int) frameLocationInScreenshot.getX(), -(int) frameLocationInScreenshot.getY());
                        // Now convert to context-relative.
                        result = result = result.offset(currentFrameScrollPosition);
                        break;

                    case CONTEXT_AS_IS:
                        result = result.offset(-(int) frameLocationInScreenshot.getX(), -(int) frameLocationInScreenshot.getY());
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
    public Region getIntersectedRegion(Region region, CoordinatesType resultCoordinatesType) {
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

        // If the intersection is empty we don't want to convert the coordinates.
        if (intersectedRegion.isSizeEmpty()) {
            return intersectedRegion;
        }

        // Converting the result to the required coordinates type.
        intersectedRegion = convertRegionLocation(intersectedRegion,
                CoordinatesType.SCREENSHOT_AS_IS, resultCoordinatesType);

        return intersectedRegion;
    }
}
