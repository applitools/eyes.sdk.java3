package com.applitools.eyes.appium;

import com.applitools.eyes.*;
import com.applitools.eyes.capture.EyesScreenshotFactory;
import com.applitools.eyes.capture.ImageProvider;
import com.applitools.eyes.debug.DebugScreenshotsProvider;
import com.applitools.eyes.positioning.PositionMemento;
import com.applitools.eyes.positioning.PositionProvider;
import com.applitools.eyes.selenium.exceptions.EyesDriverOperationException;
import com.applitools.eyes.selenium.positioning.NullRegionPositionCompensation;
import com.applitools.eyes.selenium.positioning.RegionPositionCompensation;
import com.applitools.eyes.selenium.positioning.ScrollPositionProvider;
import com.applitools.utils.ArgumentGuard;
import com.applitools.utils.GeneralUtils;
import com.applitools.utils.ImageUtils;
import org.openqa.selenium.WebElement;

import java.awt.image.BufferedImage;
import java.awt.image.Raster;

public class AppiumFullPageCaptureAlgorithm {

    private static final int MIN_SCREENSHOT_PART_HEIGHT = 10;

    protected Logger logger;

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

    // need to keep track of whether location and dimension coordinates returned by the driver
    // are already scaled to the pixel ratio, or are in "logical" pixels
    protected boolean coordinatesAreScaled;

    protected final PositionProvider positionProvider;
    protected final ScrollPositionProvider scrollProvider;

    private final WebElement cutElement;

    public AppiumFullPageCaptureAlgorithm(Logger logger, PositionProvider originProvider,
                                          PositionProvider positionProvider,
                                          ScrollPositionProvider scrollProvider,
                                          ImageProvider imageProvider, DebugScreenshotsProvider debugScreenshotsProvider,
                                          ScaleProviderFactory scaleProviderFactory, CutProvider cutProvider,
                                          EyesScreenshotFactory screenshotFactory, int waitBeforeScreenshots, WebElement cutElement) {
        ArgumentGuard.notNull(logger, "logger");
        this.logger = logger;
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
    }

    public AppiumFullPageCaptureAlgorithm(Logger logger,
                                          AppiumScrollPositionProvider scrollProvider,
                                          ImageProvider imageProvider, DebugScreenshotsProvider debugScreenshotsProvider,
                                          ScaleProviderFactory scaleProviderFactory, CutProvider cutProvider,
                                          EyesScreenshotFactory screenshotFactory, int waitBeforeScreenshots, WebElement cutElement) {

        // ensure that all the scroll/position providers used by the superclass are the same object;
        // getting the current position for appium is very expensive!
        this(logger, scrollProvider, scrollProvider, scrollProvider, imageProvider,
                debugScreenshotsProvider, scaleProviderFactory, cutProvider, screenshotFactory,
                waitBeforeScreenshots, cutElement);
    }

    protected RectangleSize captureAndStitchCurrentPart(Region partRegion) {
        logger.verbose("Taking screenshot for current scroll location");
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

        logger.verbose("Capturing all the tail parts for an Appium screen");

        RectangleSize lastSuccessfulPartSize = new RectangleSize(initialPartSize.getWidth(), initialPartSize.getHeight());
        PositionMemento originalStitchedState = scrollProvider.getState();

        int statusBarHeight = ((AppiumScrollPositionProvider) scrollProvider).getStatusBarHeight();

        // scrollViewRegion is the (upscaled) region of the scrollview on the screen
        Region scrollViewRegion = scaleSafe(((AppiumScrollPositionProvider) scrollProvider).getScrollableViewRegion());
        // we modify the region by one pixel to make sure we don't accidentally get a pixel of the header above it
        Location newLoc = new Location(scrollViewRegion.getLeft(), scrollViewRegion.getTop() - scaleSafe(statusBarHeight) + 1);
        RectangleSize newSize = new RectangleSize(initialPartSize.getWidth(), scrollViewRegion.getHeight() - 1);
        scrollViewRegion.setLocation(newLoc);
        scrollViewRegion.setSize(newSize);

        ((AppiumScrollPositionProvider) scrollProvider).setCutElement(cutElement);

        ContentSize contentSize = ((AppiumScrollPositionProvider) scrollProvider).getCachedContentSize();

        int xPos = downscaleSafe(scrollViewRegion.getLeft() + 1);
        Region regionToCrop;
        int oneScrollStep = downscaleSafe(scrollViewRegion.getHeight());
        int maxScrollSteps = contentSize.getScrollContentHeight() / oneScrollStep;
        logger.verbose("maxScrollSteps: " + maxScrollSteps);

        for (int step = 1; step <= maxScrollSteps; step++) {
            regionToCrop = new Region(0,
                    scrollViewRegion.getTop(),
                    initialPartSize.getWidth(),
                    scrollViewRegion.getHeight());

            int startY = downscaleSafe(scrollViewRegion.getHeight() + scrollViewRegion.getTop()) - 1;
            int endY = startY - oneScrollStep;
            ((AppiumScrollPositionProvider) scrollProvider).scrollTo(xPos, startY, xPos, endY, false);

            currentPosition = scaleSafe(((AppiumScrollPositionProvider) scrollProvider).getCurrentPositionWithoutStatusBar(true));

            // here we make sure to say that the region we have scrolled to in the main screenshot
            // is also offset by 1, to match the change we made to the scrollViewRegion
            // We should set left = 0 because we need to a region from the start of viewport
            Region scrolledRegion = new Region(scrollViewRegion.getLeft(), scrollViewRegion.getTop() + 1, scrollViewRegion.getWidth(),
                    scrollViewRegion.getHeight());
            currentPosition = new Location(currentPosition.getX(), currentPosition.getY() + 1);
            logger.verbose("The region to capture will be " + scrolledRegion);

            lastSuccessfulPartSize = captureAndStitchCurrentPart(regionToCrop);
        }

        int heightUnderScrollableView = initialPartSize.getHeight() - scaleSafe(oneScrollStep) - scrollViewRegion.getTop();
        if (heightUnderScrollableView > 0) { // check if there is views under the scrollable view
            logger.verbose("There are extra space under the scrollable element. (height: " + heightUnderScrollableView + ")");
            regionToCrop = new Region(0, scrollViewRegion.getHeight() + scrollViewRegion.getTop(), initialPartSize.getWidth(), heightUnderScrollableView);

            currentPosition = new Location(currentPosition.getX(), currentPosition.getY() + lastSuccessfulPartSize.getHeight());

            lastSuccessfulPartSize = captureAndStitchCurrentPart(regionToCrop);
        }

        cleanupStitch(originalStitchedState, currentPosition, lastSuccessfulPartSize, entireSize);
        moveToTopLeft();
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

    protected void moveToTopLeft() {
        logger.verbose("Moving to the top left of the screen");
        currentPosition = originProvider.getCurrentPosition();
        if (currentPosition.getX() <= 0 && currentPosition.getY() <= 0) {
            logger.verbose("We are already at the top left, doing nothing");
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
        moveToTopLeft();
        logger.verbose("Getting top/left image...");
        BufferedImage image = imageProvider.getImage();
        debugScreenshotsProvider.save(image, "original");

        // FIXME - scaling should be refactored
        scaleProvider = scaleProviderFactory.getScaleProvider(image.getWidth());
        // Notice that we want to cut/crop an image before we scale it, we need to change
        pixelRatio = 1 / scaleProvider.getScaleRatio();
        logger.verbose("Set pixel ratio for this run to " + pixelRatio);

        // FIXME - cropping should be overlaid, so a single cut provider will only handle a single part of the image.
        cutProvider = cutProvider.scale(pixelRatio);
        if (!(cutProvider instanceof NullCutProvider)) {
            logger.verbose("We have a cut provider, so cutting top left screenshot");
            image = cutProvider.cut(image);
            debugScreenshotsProvider.save(image, "original-cut");
        }

        return image;
    }

    private BufferedImage cropToRegion(BufferedImage image, Region region,
                                       RegionPositionCompensation regionPositionCompensation) {
        logger.verbose("Cropping image with dimensions [" + image.getWidth() + ", " + image.getHeight() + "] to region " + region);

        setRegionInScreenshot(image, region, regionPositionCompensation);

        if (regionInScreenshot.isEmpty()) {
            logger.verbose("Region in screenshot was empty, no need to crop");
        } else {
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
                logger.verbose("Entire size of region context: " + entireSize);
            } catch (EyesDriverOperationException e) {
                logger.log("WARNING: Failed to extract entire size of region context" + e.getMessage());
                logger.log("Using image size instead: " + image.getWidth() + "x" + image.getHeight());
                entireSize = new RectangleSize(image.getWidth(), image.getHeight());
            }
        } else {
            entireSize = positionProvider.getEntireSize();
        }
        return entireSize;
    }

    protected void setRegionInScreenshot (BufferedImage image, Region region,
                                          RegionPositionCompensation regionPositionCompensation) {

        logger.verbose("Creating screenshot object...");
        // We need the screenshot to be able to convert the region to screenshot coordinates.
        EyesScreenshot screenshot = screenshotFactory.makeScreenshot(image);
        logger.verbose("Getting region in screenshot...");
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
            logger.verbose("cutting...");
            partImage = cutProvider.cut(partImage);
            debugScreenshotsProvider.save(partImage,
                    "original-scrolled-cut-" + currentPosition
                            .toStringForFilename());
        }

        if (!regionInScreenshot.isEmpty()) {
            logger.verbose("cropping...");
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

        logger.verbose("Stitching done!");
        positionProvider.restoreState(originalStitchedState);
        originProvider.restoreState(originalPosition);

        // If the actual image size is smaller than the extracted size, we crop the image.
        int actualImageWidth = lastSuccessfulLocation.getX() + lastSuccessfulPartSize.getWidth();
        int actualImageHeight = lastSuccessfulLocation.getY() + lastSuccessfulPartSize.getHeight();
        logger.verbose("Extracted entire size: " + entireSize);
        logger.verbose("Actual stitched size: " + actualImageWidth + "x" + actualImageHeight);

        if (actualImageWidth < stitchedImage.getWidth() || actualImageHeight < stitchedImage
                .getHeight()) {
            logger.verbose("Trimming unnecessary margins..");
            stitchedImage = ImageUtils.getImagePart(stitchedImage,
                    new Region(0, 0,
                            Math.min(actualImageWidth, stitchedImage.getWidth()),
                            Math.min(actualImageHeight, stitchedImage.getHeight())));
            logger.verbose("Done!");
        }

        debugScreenshotsProvider.save(stitchedImage, "stitched");
    }

    private void captureAndStitchPart(Region partRegion) {
        logger.verbose(String.format("Taking screenshot for %s", partRegion));
        // Set the position to the part's top/left. May need to downscale since partRegion is in
        // upscaled dimensions
        positionProvider.setPosition(downscaleSafe(partRegion.getLocation()));
        // Giving it time to stabilize.
        GeneralUtils.sleep(waitBeforeScreenshots);
        // Screen size may cause the scroll to only reach part of the way. Make sure we get the
        // current position in scaled coordinates if necessary
        currentPosition = scaleSafe(positionProvider.getCurrentPosition());
        logger.verbose(String.format("Set position to %s", currentPosition));

        // Actually taking the screenshot.
        logger.verbose("Getting image...");
        BufferedImage partImage = imageProvider.getImage();
        debugScreenshotsProvider.save(partImage,
                "original-scrolled-" + currentPosition.toStringForFilename());

        partImage = cropPartToRegion(partImage, partRegion);
        stitchPartIntoContainer(partImage);
    }

    protected void stitchPartIntoContainer(BufferedImage partImage) {
        // Stitching the current part.
        logger.verbose("Stitching part into the image container...");
        // We should stitch images from the start of X coordinate
        stitchedImage.getRaster()
                .setRect(0, currentPosition.getY(), partImage.getData());
        logger.verbose("Done!");
    }



    /**
     * Returns a stitching of a region.
     *
     * @param region The region to stitch. If {@code Region.EMPTY}, the entire image will be stitched.
     * @param regionPositionCompensation A strategy for compensating region positions for some browsers.
     * @return An image which represents the stitched region.
     */
    public BufferedImage getStitchedRegion(Region region, RegionPositionCompensation regionPositionCompensation) {
        logger.verbose("getStitchedRegion()");

        ArgumentGuard.notNull(region, "region");

        logger.verbose(String.format(
                "getStitchedRegion: originProvider: %s ; positionProvider: %s ; cutProvider: %s",
                originProvider.getClass(), positionProvider.getClass(), cutProvider.getClass()));

        logger.verbose(String.format("Region to check: %s", region));

        // Saving the original position (in case we were already in the outermost frame).
        originalPosition = originProvider.getState();

        // first, scroll to the origin and get the top left screenshot
        BufferedImage image = getTopLeftScreenshot();

        // now crop the screenshot based on the provided region
        image = cropToRegion(image, region, regionPositionCompensation);

        // get the entire size of the region context, falling back to image size

        boolean checkingAnElement = !region.isEmpty();
        RectangleSize entireSize = scaleSafe(getEntireSize(image, checkingAnElement));
        logger.verbose("Scaled entire size is " + entireSize);

        // If the image is already the same as or bigger than the entire size, we're done!
        // Notice that this might still happen even if we used
        // "getImagePart", since "entirePageSize" might be that of a frame.
        if (image.getWidth() >= entireSize.getWidth() && image.getHeight() >= entireSize
                .getHeight()) {
            logger.verbose("Image was already bigger than entire size, so returning straightaway");
            originProvider.restoreState(originalPosition);

            return ImageUtils.scaleImage(image, scaleProvider.getScaleRatio(), true);
        }

        // Otherwise, make a big image to stitch smaller parts into
        logger.verbose("Creating stitchedImage container. Size: " + entireSize);
        //Notice stitchedImage uses the same type of image as the screenshots.
        // Use initial image width for stitched image to prevent wrong image part size
        // if scrollable view has some padding or margins
        stitchedImage = new BufferedImage(
                image.getWidth(), entireSize.getHeight(), image.getType());
        logger.verbose("Done!");

        // First of all we want to stitch the screenshot we already captured at (0, 0)
        logger.verbose("Adding initial screenshot..");
        Raster initialPart = image.getData();
        RectangleSize initialPartSize = new RectangleSize(initialPart.getWidth(),
                initialPart.getHeight());
        logger.verbose(String.format("Initial part:(0,0)[%d x %d]",
                initialPart.getWidth(), initialPart.getHeight()));
        stitchedImage.getRaster().setRect(0, 0, initialPart);
        logger.verbose("Done!");

        /* TODO need to determine if there is anything in the initial part which should be cut
           off and reapplied at the bottom of the stitched image. Can do this by checking whether
           the scrolling view has a height less than the screen height */

        captureAndStitchTailParts(entireSize, initialPartSize);

        // Finally, scale the image appropriately
        if (pixelRatio == 1.0) {
            logger.verbose("Pixel ratio was 1, no need to scale stitched image");
        } else {
            logger.verbose("Pixel ratio was " + pixelRatio + "; scaling stitched image");
            stitchedImage = ImageUtils.scaleImage(stitchedImage, scaleProvider.getScaleRatio(), true);
            debugScreenshotsProvider.save(stitchedImage, "scaled");
        }

        return stitchedImage;
    }

    private Region getRegionInScreenshot(Region region, BufferedImage image, double pixelRatio,
                                         EyesScreenshot screenshot, RegionPositionCompensation regionPositionCompensation) {
        // Region regionInScreenshot = screenshot.convertRegionLocation(regionProvider.getRegion(), regionProvider.getCoordinatesType(), CoordinatesType.SCREENSHOT_AS_IS);
        region.setLocation(new Location(0, region.getLocation().getY()));
        Region regionInScreenshot = screenshot
                .getIntersectedRegion(region, CoordinatesType.SCREENSHOT_AS_IS);

        logger.verbose("Done! Region in screenshot: " + regionInScreenshot);

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
        logger.verbose("Region after intersect: " + regionInScreenshot);
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
