package com.applitools.eyes.appium;

import com.applitools.eyes.*;
import com.applitools.eyes.capture.EyesScreenshotFactory;
import com.applitools.eyes.capture.ImageProvider;
import com.applitools.eyes.debug.DebugScreenshotsProvider;
import com.applitools.eyes.logging.Stage;
import com.applitools.eyes.logging.Type;
import com.applitools.eyes.positioning.PositionMemento;
import com.applitools.eyes.positioning.PositionProvider;
import com.applitools.eyes.selenium.exceptions.EyesDriverOperationException;
import com.applitools.eyes.selenium.positioning.NullRegionPositionCompensation;
import com.applitools.eyes.selenium.positioning.RegionPositionCompensation;
import com.applitools.eyes.selenium.positioning.ScrollPositionProvider;
import com.applitools.utils.ArgumentGuard;
import com.applitools.utils.GeneralUtils;
import com.applitools.utils.ImageUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.openqa.selenium.WebElement;

import java.awt.image.BufferedImage;
import java.awt.image.Raster;

public class AppiumFullPageCaptureAlgorithm {

    protected static final int DEFAULT_STITCHING_ADJUSTMENT = 50;

    protected Logger logger;
    protected final String testId;
    private final PositionProvider originProvider;
    protected final ImageProvider imageProvider;
    protected final DebugScreenshotsProvider debugScreenshotsProvider;
    private final ScaleProviderFactory scaleProviderFactory;
    private final EyesScreenshotFactory screenshotFactory;
    protected final int waitBeforeScreenshots;

    private PositionMemento originalPosition;
    private ScaleProvider scaleProvider;
    private CutProvider cutProvider;
    protected Region regionInScreenshot;
    private double pixelRatio;
    private BufferedImage stitchedImage;
    protected Location currentPosition;
    protected WebElement scrollRootElement;

    // need to keep track of whether location and dimension coordinates returned by the driver
    // are already scaled to the pixel ratio, or are in "logical" pixels
    protected boolean coordinatesAreScaled;

    protected final PositionProvider positionProvider;
    protected final ScrollPositionProvider scrollProvider;

    private final WebElement cutElement;
    protected Integer stitchingAdjustment = DEFAULT_STITCHING_ADJUSTMENT;

    public AppiumFullPageCaptureAlgorithm(Logger logger, String testId, PositionProvider originProvider,
                                          PositionProvider positionProvider,
                                          ScrollPositionProvider scrollProvider,
                                          ImageProvider imageProvider, DebugScreenshotsProvider debugScreenshotsProvider,
                                          ScaleProviderFactory scaleProviderFactory, CutProvider cutProvider,
                                          EyesScreenshotFactory screenshotFactory, int waitBeforeScreenshots, WebElement cutElement,
                                          Integer stitchingAdjustment) {
        ArgumentGuard.notNull(logger, "logger");
        this.logger = logger;
        this.testId = testId;
        this.originProvider = originProvider;
        this.positionProvider = positionProvider;
        this.scrollProvider = scrollProvider;
        this.imageProvider = imageProvider;
        this.debugScreenshotsProvider = debugScreenshotsProvider;
        this.scaleProviderFactory = scaleProviderFactory;
        this.cutProvider = cutProvider;
        this.screenshotFactory = screenshotFactory;
        this.waitBeforeScreenshots = waitBeforeScreenshots;
        this.pixelRatio = 1.0;
        this.originalPosition = null;
        this.scaleProvider = null;
        this.regionInScreenshot = null;
        this.stitchedImage = null;
        this.currentPosition = null;
        this.coordinatesAreScaled = false;
        this.cutElement = cutElement;
        if (stitchingAdjustment != null) {
            this.stitchingAdjustment = stitchingAdjustment;
        }
    }

    public AppiumFullPageCaptureAlgorithm(Logger logger, String testId,
                                          AppiumScrollPositionProvider scrollProvider,
                                          ImageProvider imageProvider, DebugScreenshotsProvider debugScreenshotsProvider,
                                          ScaleProviderFactory scaleProviderFactory, CutProvider cutProvider,
                                          EyesScreenshotFactory screenshotFactory, int waitBeforeScreenshots, WebElement cutElement,
                                          Integer stitchingAdjustment, WebElement scrollRootElement) {

        // ensure that all the scroll/position providers used by the superclass are the same object;
        // getting the current position for appium is very expensive!
        this(logger, testId, scrollProvider, scrollProvider, scrollProvider, imageProvider,
                debugScreenshotsProvider, scaleProviderFactory, cutProvider, screenshotFactory,
                waitBeforeScreenshots, cutElement, stitchingAdjustment);
        this.scrollRootElement = scrollRootElement;
        scrollProvider.setScrollRootElement(scrollRootElement);
    }

    protected RectangleSize captureAndStitchCurrentPart(Region partRegion) {
        GeneralUtils.sleep(waitBeforeScreenshots);
        BufferedImage partImage = imageProvider.getImage();
        debugScreenshotsProvider.save(partImage,
                "original-scrolled=" + currentPosition.toStringForFilename());

        // before we take new screenshots, we have to reset the region in the screenshot we care
        // about, since from now on we just want the scroll view, not the entire view
        setRegionInScreenshot(partImage, partRegion, new NullRegionPositionCompensation());

        partImage = cropPartToRegion(partImage, partRegion);

        stitchPartIntoContainer(partImage);
        return new RectangleSize(partImage.getWidth(), partImage.getHeight());
    }

    protected void captureAndStitchTailParts(RectangleSize entireSize, RectangleSize initialPartSize) {
        RectangleSize lastSuccessfulPartSize = new RectangleSize(initialPartSize.getWidth(), initialPartSize.getHeight());
        PositionMemento originalStitchedState = scrollProvider.getState();

        int statusBarHeight = ((AppiumScrollPositionProvider) scrollProvider).getStatusBarHeight();

        // scrollViewRegion is the (upscaled) region of the scrollview on the screen
        Region scrollViewRegion = scaleSafe(((AppiumScrollPositionProvider) scrollProvider).getScrollableViewRegion());
        // we modify the region by one pixel to make sure we don't accidentally get a pixel of the header above it
        Location newLoc = new Location(scrollViewRegion.getLeft(), scrollViewRegion.getTop() - scaleSafe(statusBarHeight) + 1);
        RectangleSize newSize = new RectangleSize(initialPartSize.getWidth(), scrollViewRegion.getHeight() - 1);
        if (scrollRootElement == null) {
            scrollViewRegion.setLocation(newLoc);
        }
        scrollViewRegion.setSize(newSize);

        ((AppiumScrollPositionProvider) scrollProvider).setCutElement(cutElement);

        ContentSize contentSize = ((AppiumScrollPositionProvider) scrollProvider).getCachedContentSize();

        int xPos = downscaleSafe(scrollViewRegion.getLeft() + 1);
        Region regionToCrop;
        int oneScrollStep = downscaleSafe(scrollViewRegion.getHeight() - stitchingAdjustment);
        int maxScrollSteps = contentSize.getScrollContentHeight() / oneScrollStep;
        int startY = downscaleSafe(scrollViewRegion.getHeight() + scrollViewRegion.getTop()) - 1 - stitchingAdjustment/2;
        int endY = startY - oneScrollStep + 2 + stitchingAdjustment/2;
        for (int step = 1; step <= maxScrollSteps; step++) {
            regionToCrop = new Region(0,
                    scrollViewRegion.getTop() + stitchingAdjustment,
                    initialPartSize.getWidth(),
                    scrollViewRegion.getHeight() - stitchingAdjustment);

            ((AppiumScrollPositionProvider) scrollProvider).scrollTo(xPos, startY, xPos, endY, false);

            currentPosition = scaleSafe(((AppiumScrollPositionProvider) scrollProvider).getCurrentPositionWithoutStatusBar(true));

            // here we make sure to say that the region we have scrolled to in the main screenshot
            // is also offset by 1, to match the change we made to the scrollViewRegion
            // We should set left = 0 because we need to a region from the start of viewport
            currentPosition = new Location(currentPosition.getX(), currentPosition.getY() + 1 + stitchingAdjustment
                    + (scrollRootElement == null ? 0 : scaleSafe(statusBarHeight)));

            lastSuccessfulPartSize = captureAndStitchCurrentPart(regionToCrop);
        }

        int heightUnderScrollableView = initialPartSize.getHeight() - scaleSafe(oneScrollStep) - scrollViewRegion.getTop();
        if (heightUnderScrollableView > 0) { // check if there is views under the scrollable view
            regionToCrop = new Region(
                    0,
                    scrollViewRegion.getHeight() + scrollViewRegion.getTop() - stitchingAdjustment,
                    initialPartSize.getWidth(),
                    heightUnderScrollableView);

            currentPosition = new Location(currentPosition.getX(), currentPosition.getY() + lastSuccessfulPartSize.getHeight() - stitchingAdjustment);

            lastSuccessfulPartSize = captureAndStitchCurrentPart(regionToCrop);
        }

        cleanupStitch(originalStitchedState, currentPosition, lastSuccessfulPartSize, entireSize);

        // 50 - extra offset to avoid overlay elements.
        moveToTopLeft(xPos, endY + statusBarHeight + 50 + stitchingAdjustment, xPos, startY + statusBarHeight);
    }



    /** FPCA - from JL */


    private void saveDebugScreenshotPart(BufferedImage image,
                                         Region region, String name) {
        String suffix =
                "part-" + name + "-" + region.getLeft() + "_" + region.getTop() + "_" + region
                        .getWidth() + "x"
                        + region.getHeight();
        debugScreenshotsProvider.save(image, suffix);
    }

    /**
     * Scrolls root scrollable view to the content beginning.
     * @param startX Start X coordinate of scroll action.
     * @param startY Start Y coordinate of scroll action.
     * @param endX End X coordinate of scroll action.
     * @param endY End Y coordinate of scroll action.
     */
    protected void moveToTopLeft(int startX, int startY, int endX, int endY) {
        currentPosition = originProvider.getCurrentPosition();
        if (currentPosition.getX() <= 0 && currentPosition.getY() <= 0) {
            return;
        }

        // Recalculate coordinates if they all were passed with 0 value.
        if ( startX == 0 && startY == 0 && endX == 0 && endY == 0 ) {
            Region scrollViewRegion = scaleSafe(((AppiumScrollPositionProvider) scrollProvider).getScrollableViewRegion());
            int oneScrollStep = downscaleSafe(scrollViewRegion.getHeight());
            startX = endX = downscaleSafe(scrollViewRegion.getLeft() + 1);
            startY = downscaleSafe(scrollViewRegion.getTop()) + 1;
            endY = startY + oneScrollStep - 2;
        }

        do {
            ((AppiumScrollPositionProvider) scrollProvider).scrollTo(startX, startY, endX, endY, false);
            GeneralUtils.sleep(waitBeforeScreenshots);
            currentPosition = originProvider.getCurrentPosition();
        } while (currentPosition.getX() > 0 || currentPosition.getY() > 0);
    }

    protected void moveToTopLeft() {
        currentPosition = originProvider.getCurrentPosition();
        if (currentPosition.getX() <= 0 && currentPosition.getY() <= 0) {
            // Just to make sure that we are on the top of the screen we need scroll up for a one step.
            // Because position can not be 'top' after Appium calculating last scrollable data.
            ((AppiumScrollPositionProvider) originProvider).forceScrollToTop();
            try { Thread.sleep(1000); } catch (InterruptedException ignored) {}
            return;
        }

        int setPositionRetries = 3;
        do {
            originProvider.setPosition(new Location(0, 0));
            // Give the scroll time to stabilize
            GeneralUtils.sleep(waitBeforeScreenshots);
            currentPosition = originProvider.getCurrentPosition();
        } while (currentPosition.getX() != 0
                && currentPosition.getY() != 0
                && (--setPositionRetries > 0));
        // TODO examine the while loop condition logic above, currently we will stop scrolling if
        // we get to 0 on EITHER the x or y axis; shouldn't we need to get there on both?

        if (currentPosition.getY() > 0) {
            originProvider.restoreState(originalPosition);
            throw new EyesException("Couldn't set position to the top/left corner!");
        }
    }

    private BufferedImage getTopLeftScreenshot() {
        moveToTopLeft(0, 0, 0, 0);
        BufferedImage image = imageProvider.getImage();
        debugScreenshotsProvider.save(image, "original");

        // FIXME - scaling should be refactored
        scaleProvider = scaleProviderFactory.getScaleProvider(image.getWidth());
        // Notice that we want to cut/crop an image before we scale it, we need to change
        pixelRatio = 1 / scaleProvider.getScaleRatio();

        // FIXME - cropping should be overlaid, so a single cut provider will only handle a single part of the image.
        cutProvider = cutProvider.scale(pixelRatio);
        if (!(cutProvider instanceof NullCutProvider)) {
            image = cutProvider.cut(image);
            debugScreenshotsProvider.save(image, "original-cut");
        }

        return image;
    }

    private BufferedImage cropToRegion(BufferedImage image, Region region,
                                       RegionPositionCompensation regionPositionCompensation) {
        setRegionInScreenshot(image, region, regionPositionCompensation);
        if (!regionInScreenshot.isEmpty()) {
            image = ImageUtils.getImagePart(image, regionInScreenshot);
            saveDebugScreenshotPart(image, region, "cropped");
        }

        return image;
    }

    private RectangleSize getEntireSize(BufferedImage image, boolean checkingAnElement) {
        RectangleSize entireSize;
        if (!checkingAnElement) {
            try {
                entireSize = scrollProvider.getEntireSize();
            } catch (EyesDriverOperationException e) {
                GeneralUtils.logExceptionStackTrace(logger, Stage.CHECK, Type.CAPTURE_SCREENSHOT, e, testId);
                entireSize = new RectangleSize(image.getWidth(), image.getHeight());
            }
        } else {
            entireSize = positionProvider.getEntireSize();
        }
        return entireSize;
    }

    protected void setRegionInScreenshot(BufferedImage image, Region region,
                                          RegionPositionCompensation regionPositionCompensation) {
        // We need the screenshot to be able to convert the region to screenshot coordinates.
        EyesScreenshot screenshot = screenshotFactory.makeScreenshot(image);
        regionInScreenshot = getRegionInScreenshot(region, image, pixelRatio, screenshot,
                regionPositionCompensation);

        // if it didn't work the first time, just try again!??
        if (!regionInScreenshot.getSize().equals(region.getSize())) {
            // TODO - ITAI
            regionInScreenshot = getRegionInScreenshot(region, image, pixelRatio, screenshot,
                    regionPositionCompensation);
        }
    }

    protected BufferedImage cropPartToRegion(BufferedImage partImage, Region partRegion) {

        // FIXME - cropping should be overlaid (see previous comment re cropping)
        if (!(cutProvider instanceof NullCutProvider)) {
            partImage = cutProvider.cut(partImage);
            debugScreenshotsProvider.save(partImage,
                    "original-scrolled-cut-" + currentPosition
                            .toStringForFilename());
        }

        if (!regionInScreenshot.isEmpty()) {
            partImage = ImageUtils.getImagePart(partImage, regionInScreenshot);
            saveDebugScreenshotPart(partImage, partRegion,
                    "original-scrolled-"
                            + currentPosition.toStringForFilename());
        }

        return partImage;
    }

    protected void cleanupStitch(PositionMemento originalStitchedState,
                                 Location lastSuccessfulLocation,
                                 RectangleSize lastSuccessfulPartSize, RectangleSize entireSize) {
        positionProvider.restoreState(originalStitchedState);
        originProvider.restoreState(originalPosition);

        // If the actual image size is smaller than the extracted size, we crop the image.
        int actualImageWidth = lastSuccessfulLocation.getX() + lastSuccessfulPartSize.getWidth();
        int actualImageHeight = lastSuccessfulLocation.getY() + lastSuccessfulPartSize.getHeight();

        if (actualImageWidth < stitchedImage.getWidth() || actualImageHeight < stitchedImage
                .getHeight()) {
            stitchedImage = ImageUtils.getImagePart(stitchedImage,
                    new Region(0, 0,
                            Math.min(actualImageWidth, stitchedImage.getWidth()),
                            Math.min(actualImageHeight, stitchedImage.getHeight())));
        }

        debugScreenshotsProvider.save(stitchedImage, "stitched");
    }

    protected void stitchPartIntoContainer(BufferedImage partImage) {
        // We should stitch images from the start of X coordinate
        stitchedImage.getRaster()
                .setRect(0, currentPosition.getY(), partImage.getData());
    }



    /**
     * Returns a stitching of a region.
     *
     * @param region The region to stitch. If {@code Region.EMPTY}, the entire image will be stitched.
     * @param regionPositionCompensation A strategy for compensating region positions for some browsers.
     * @return An image which represents the stitched region.
     */
    public BufferedImage getStitchedRegion(Region region, RegionPositionCompensation regionPositionCompensation) {
        ArgumentGuard.notNull(region, "region");

        // Saving the original position (in case we were already in the outermost frame).
        originalPosition = originProvider.getState();

        // Extra pause to wait for application actions will be finished.
        // Moving from one screen to another for example
        try { Thread.sleep(1000); } catch (InterruptedException ignored) {}

        // first, scroll to the origin and get the top left screenshot
        BufferedImage image = getTopLeftScreenshot();
        logger.log(testId, Stage.CHECK, Type.CAPTURE_SCREENSHOT,
                Pair.of("region", region),
                Pair.of("pixelRatio", pixelRatio),
                Pair.of("originProvider", originalPosition.getClass().getName()),
                Pair.of("positionProvider", positionProvider.getClass().getName()),
                Pair.of("cutProvider", cutProvider.getClass().getName()));

        // now crop the screenshot based on the provided region
        image = cropToRegion(image, region, regionPositionCompensation);

        // get the entire size of the region context, falling back to image size

        boolean checkingAnElement = !region.isEmpty();
        RectangleSize entireSize = scaleSafe(getEntireSize(image, checkingAnElement));
        logger.log(testId, Stage.CHECK, Type.CAPTURE_SCREENSHOT, Pair.of("entireSize", entireSize));

        // If the image is already the same as or bigger than the entire size, we're done!
        // Notice that this might still happen even if we used
        // "getImagePart", since "entirePageSize" might be that of a frame.
        if (image.getWidth() >= entireSize.getWidth() && image.getHeight() >= entireSize
                .getHeight()) {
            originProvider.restoreState(originalPosition);

            return ImageUtils.scaleImage(image, scaleProvider.getScaleRatio(), true);
        }

        // Otherwise, make a big image to stitch smaller parts into
        //Notice stitchedImage uses the same type of image as the screenshots.
        // Use initial image width for stitched image to prevent wrong image part size
        // if scrollable view has some padding or margins
        stitchedImage = new BufferedImage(
                image.getWidth(), entireSize.getHeight(), image.getType());

        // First of all we want to stitch the screenshot we already captured at (0, 0)
        Raster initialPart = image.getData();
        RectangleSize initialPartSize = new RectangleSize(initialPart.getWidth(),
                initialPart.getHeight());
        logger.log(testId, Stage.CHECK, Type.CAPTURE_SCREENSHOT, Pair.of("initialPart", initialPartSize));
        stitchedImage.getRaster().setRect(0, 0, initialPart);

        /* TODO need to determine if there is anything in the initial part which should be cut
           off and reapplied at the bottom of the stitched image. Can do this by checking whether
           the scrolling view has a height less than the screen height */

        captureAndStitchTailParts(entireSize, initialPartSize);

        // Finally, scale the image appropriately
        if (pixelRatio != 1.0) {
            stitchedImage = ImageUtils.scaleImage(stitchedImage, scaleProvider.getScaleRatio(), true);
            debugScreenshotsProvider.save(stitchedImage, "scaled");
        }

        return stitchedImage;
    }

    private Region getRegionInScreenshot(Region region, BufferedImage image, double pixelRatio,
                                         EyesScreenshot screenshot, RegionPositionCompensation regionPositionCompensation) {
        // Region regionInScreenshot = screenshot.convertRegionLocation(regionProvider.getRegion(), regionProvider.getCoordinatesType(), CoordinatesType.SCREENSHOT_AS_IS);
        region.setLocation(new Location(0, region.getLocation().getY()));
        Region regionInScreenshot = screenshot.getIntersectedRegion(region, CoordinatesType.SCREENSHOT_AS_IS);
        if (regionPositionCompensation == null) {
            regionPositionCompensation = new NullRegionPositionCompensation();
        }

        // TODO probably need to adjust this logic now that the regionInScreenshot is always upscaled
        regionInScreenshot = regionPositionCompensation
                .compensateRegionPosition(regionInScreenshot, pixelRatio);

        // Handling a specific case where the region is actually larger than
        // the screenshot (e.g., when body width/height are set to 100%, and
        // an internal div is set to value which is larger than the viewport).
        regionInScreenshot.intersect(new Region(0, 0, image.getWidth(), image.getHeight()));
        logger.log(testId, Stage.CHECK, Type.CAPTURE_SCREENSHOT, Pair.of("regionInScreenshot", regionInScreenshot));
        return regionInScreenshot;
    }

    protected RectangleSize scaleSafe(RectangleSize rs) {
        if (coordinatesAreScaled) {
            return rs;
        }
        return rs.scale(pixelRatio);
    }

    protected Location scaleSafe(Location loc) {
        if (coordinatesAreScaled) {
            return loc;
        }
        return loc.scale(pixelRatio);
    }

    protected Region scaleSafe(Region reg) {
        if (coordinatesAreScaled) {
            return reg;
        }
        return reg.scale(pixelRatio);
    }

    protected Location downscaleSafe(Location loc) {
        if (coordinatesAreScaled) {
            return loc;
        }
        return loc.scale(1 / pixelRatio);
    }

    protected int scaleSafe(int value) {
        if (coordinatesAreScaled) {
            return value;
        }
        return (int) Math.ceil(value * pixelRatio);
    }

    protected int downscaleSafe(int value) {
        if (coordinatesAreScaled) {
            return value;
        }
        return (int) Math.ceil(value/pixelRatio);
    }
}
