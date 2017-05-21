package com.applitools.eyes.selenium;

import com.applitools.eyes.*;
import com.applitools.utils.ArgumentGuard;
import com.applitools.utils.GeneralUtils;
import com.applitools.utils.ImageUtils;

import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.util.Calendar;

public class FullPageCaptureAlgorithm {
    // This should pretty much cover all scroll bars (and some fixed position
    // footer elements :).
    private static final int MAX_SCROLL_BAR_SIZE = 50;
    private static final int MIN_SCREENSHOT_PART_HEIGHT = 10;

    private final Logger logger;

    public FullPageCaptureAlgorithm(Logger logger) {
        ArgumentGuard.notNull(logger, "logger");
        this.logger = logger;
    }

    private static void saveDebugScreenshotPart(DebugScreenshotsProvider debugScreenshotsProvider, BufferedImage image,
                                               Region region, String name) {
        String suffix = "part-" + name + "-" + region.getLeft() + "_" + region.getTop() + "_" + region.getWidth() + "x"
                + region.getHeight();
        debugScreenshotsProvider.save(image, suffix);
    }

    /**
     * Returns a stitching of a region.
     *
     * @param imageProvider The provider for the screenshot.
     * @param regionProvider A provider of the region to stitch. If {@code
     *                       getRegion} returns {@code Region.EMPTY}, the
     *                       entire image will be stitched.
     * @param originProvider A provider for scrolling to initial position
     *                       before starting the actual stitching.
     * @param positionProvider A provider of the scrolling implementation.
     * @param scaleProviderFactory A factory for getting the scale provider.
     * @param waitBeforeScreenshots Time to wait before each screenshot
     *                              (milliseconds).
     * @param screenshotFactory The factory to use for creating screenshots
     *                          from the images.
     * @return An image which represents the stitched region.
     */

    public BufferedImage getStitchedRegion(ImageProvider imageProvider,
               RegionProvider regionProvider, PositionProvider originProvider,
               PositionProvider positionProvider, ScaleProviderFactory scaleProviderFactory,
               CutProvider cutProvider, int waitBeforeScreenshots, DebugScreenshotsProvider debugScreenshotsProvider,
               EyesScreenshotFactory screenshotFactory) {
        logger.verbose("getStitchedRegion()");

        ArgumentGuard.notNull(regionProvider, "regionProvider");
        ArgumentGuard.notNull(positionProvider, "positionProvider");

        logger.verbose(String.format("Region to check: %s",
                regionProvider.getRegion()));
        logger.verbose(String.format("Coordinates type: %s",
                regionProvider.getCoordinatesType()));

        // TODO use scaling overlap offset.
        final int SCALE_MARGIN_PX = 5;

        // Saving the original position (in case we were already in the
        // outermost frame).
        PositionMemento originalPosition = originProvider.getState();
        Location currentPosition;

        int setPositionRetries = 3;
        do {
            originProvider.setPosition(new Location(0, 0));
            // Give the scroll time to stabilize
            GeneralUtils.sleep(waitBeforeScreenshots);
            currentPosition = originProvider.getCurrentPosition();
        } while (currentPosition.getX() != 0
                && currentPosition.getY() != 0
                && (--setPositionRetries > 0));

        if (currentPosition.getX() != 0 || currentPosition.getY() != 0) {
            originProvider.restoreState(originalPosition);
            throw new EyesException(
                    "Couldn't set position to the top/left corner!");
        }

        logger.verbose("Getting top/left image...");
        BufferedImage image = imageProvider.getImage();
        debugScreenshotsProvider.save(image, "original");

        // FIXME - scaling should be refactored
        ScaleProvider scaleProvider = scaleProviderFactory.getScaleProvider(image.getWidth());
        // Notice that we want to cut/crop an image before we scale it, we need to change
        double pixelRatio = 1 / scaleProvider.getScaleRatio();

        // FIXME - cropping should be overlaid, so a single cut provider will only handle a single part of the image.
        cutProvider = cutProvider.scale(pixelRatio);
        image = cutProvider.cut(image);
        debugScreenshotsProvider.save(image, "original-cut");

        logger.verbose("Done! Creating screenshot object...");
        // We need the screenshot to be able to convert the region to
        // screenshot coordinates.
        EyesScreenshot screenshot = screenshotFactory.makeScreenshot(image);
        logger.verbose("Done! Getting region in screenshot...");
        Region regionInScreenshot =
                screenshot.convertRegionLocation(regionProvider.getRegion(),
                        regionProvider.getCoordinatesType(),
                        CoordinatesType.SCREENSHOT_AS_IS);

        logger.verbose("Done! Region in screenshot: " + regionInScreenshot);
        regionInScreenshot = regionInScreenshot.scale(pixelRatio);
        logger.verbose("Scaled region: " + regionInScreenshot);

        // Handling a specific case where the region is actually larger than
        // the screenshot (e.g., when body width/height are set to 100%, and
        // an internal div is set to value which is larger than the viewport).
        regionInScreenshot.intersect(new Region(0, 0, image.getWidth(), image
                .getHeight()));
        logger.verbose("Region after intersect: " + regionInScreenshot);

        if (!regionInScreenshot.isEmpty()) {
            image = ImageUtils.getImagePart(image, regionInScreenshot);
            saveDebugScreenshotPart(debugScreenshotsProvider, image, regionProvider.getRegion(), "before-scaled");
        }

        image = ImageUtils.scaleImage(image, scaleProvider);
        debugScreenshotsProvider.save(image, "scaled");

        RectangleSize entireSize;
        try {
            entireSize = positionProvider.getEntireSize();
            logger.verbose("Entire size of region context: " + entireSize);
        } catch (EyesDriverOperationException e) {
            logger.log(
                    "WARNING: Failed to extract entire size of region context"
                            + e.getMessage());
            logger.log("Using image size instead: "
                    + image.getWidth() + "x" + image.getHeight());
            entireSize = new RectangleSize(image.getWidth(), image.getHeight());
        }

        // Notice that this might still happen even if we used
        // "getImagePart", since "entirePageSize" might be that of a frame.
        if (image.getWidth() >= entireSize.getWidth() &&
                image.getHeight() >= entireSize.getHeight()) {
            originProvider.restoreState(originalPosition);

            return image;
        }

        // These will be used for storing the actual stitched size (it is
        // sometimes less than the size extracted via "getEntireSize").
        Location lastSuccessfulLocation;
        RectangleSize lastSuccessfulPartSize;

        // The screenshot part is a bit smaller than the screenshot size,
        // in order to eliminate duplicate bottom scroll bars, as well as fixed
        // position footers.
        RectangleSize partImageSize =
                new RectangleSize(image.getWidth(),
                        Math.max(image.getHeight() - MAX_SCROLL_BAR_SIZE,
                                MIN_SCREENSHOT_PART_HEIGHT));

        logger.verbose(String.format("Total size: %s, image part size: %s",
                entireSize, partImageSize));

        // Getting the list of sub-regions composing the whole region (we'll
        // take screenshot for each one).
        Region entirePage = new Region(Location.ZERO, entireSize);
        Iterable<Region> imageParts =
                entirePage.getSubRegions(partImageSize);

        logger.verbose("Creating stitchedImage container. Size: " + entireSize);
        //Notice stitchedImage uses the same type of image as the screenshots.
        BufferedImage stitchedImage = new BufferedImage(
                entireSize.getWidth(), entireSize.getHeight(),
                image.getType());
        logger.verbose("Done! Adding initial screenshot..");
        // Starting with the screenshot we already captured at (0,0).
        Raster initialPart = image.getData();
        logger.verbose(String.format("Initial part:(0,0)[%d x %d]",
                initialPart.getWidth(), initialPart.getHeight()));
        stitchedImage.getRaster().setRect(0, 0, initialPart);
        logger.verbose("Done!");

        lastSuccessfulLocation = new Location(0, 0);
        lastSuccessfulPartSize = new RectangleSize(initialPart.getWidth(),
                initialPart.getHeight());

        PositionMemento originalStitchedState = positionProvider.getState();

        // Take screenshot and stitch for each screenshot part.
        logger.verbose("Getting the rest of the image parts...");
        BufferedImage partImage = null;
        for (Region partRegion: imageParts) {
            // Skipping screenshot for 0,0 (already taken)
            if (partRegion.getLeft() == 0 && partRegion.getTop() == 0) {
                continue;
            }
            logger.verbose(String.format("Taking screenshot for %s",
                    partRegion));
            // Set the position to the part's top/left.
            positionProvider.setPosition(partRegion.getLocation());
            // Giving it time to stabilize.
            GeneralUtils.sleep(waitBeforeScreenshots);
            // Screen size may cause the scroll to only reach part of the way.
            currentPosition = positionProvider.getCurrentPosition();
            logger.verbose(String.format("Set position to %s",
                    currentPosition));

            // Actually taking the screenshot.
            logger.verbose("Getting image...");
            partImage = imageProvider.getImage();
            debugScreenshotsProvider.save(partImage,
                    "original-scrolled-" + positionProvider.getCurrentPosition().toStringForFilename());

            // FIXME - cropping should be overlaid (see previous comment re cropping)
            partImage = cutProvider.cut(partImage);
            debugScreenshotsProvider.save(partImage,
                    "original-scrolled-cut-" + positionProvider.getCurrentPosition().toStringForFilename());

            logger.verbose("Done!");

            if (!regionInScreenshot.isEmpty()) {
                partImage = ImageUtils.getImagePart(partImage,
                        regionInScreenshot);
                saveDebugScreenshotPart(debugScreenshotsProvider, partImage, partRegion, "original-scrolled-"
                        + positionProvider.getCurrentPosition().toStringForFilename());
            }

            // FIXME - scaling should be refactored
            partImage = ImageUtils.scaleImage(partImage, scaleProvider);
            saveDebugScreenshotPart(debugScreenshotsProvider, partImage, partRegion,
                    "original-scrolled-" + positionProvider.getCurrentPosition().toStringForFilename() + "-scaled-");


            // Stitching the current part.
            logger.verbose("Stitching part into the image container...");
            stitchedImage.getRaster().setRect(currentPosition.getX(),
                    currentPosition.getY(), partImage.getData());
            logger.verbose("Done!");

            lastSuccessfulLocation = currentPosition;
        }

        if (partImage != null) {
            lastSuccessfulPartSize = new RectangleSize(partImage.getWidth(),
                    partImage.getHeight());
        }

        logger.verbose("Stitching done!");
        positionProvider.restoreState(originalStitchedState);
        originProvider.restoreState(originalPosition);


        // If the actual image size is smaller than the extracted size, we
        // crop the image.
        int actualImageWidth = lastSuccessfulLocation.getX() +
                lastSuccessfulPartSize.getWidth();
        int actualImageHeight = lastSuccessfulLocation.getY() +
                lastSuccessfulPartSize.getHeight();
        logger.verbose("Extracted entire size: " + entireSize);
        logger.verbose("Actual stitched size: " + actualImageWidth + "x" +
                actualImageHeight);

        if (actualImageWidth < stitchedImage.getWidth() ||
                actualImageHeight < stitchedImage.getHeight()) {
            logger.verbose("Trimming unnecessary margins..");
            stitchedImage = ImageUtils.getImagePart(stitchedImage,
                    new Region(0, 0,
                            Math.min(actualImageWidth, stitchedImage.getWidth()),
                            Math.min(actualImageHeight, stitchedImage.getHeight())));
            logger.verbose("Done!");
        }

        debugScreenshotsProvider.save(stitchedImage, "stitched");
        return stitchedImage;
    }
}
