package com.applitools.eyes.selenium.capture;

import com.applitools.eyes.*;
import com.applitools.eyes.capture.EyesScreenshotFactory;
import com.applitools.eyes.capture.ImageProvider;
import com.applitools.eyes.debug.DebugScreenshotsProvider;
import com.applitools.eyes.CutProvider;
import com.applitools.eyes.logging.Stage;
import com.applitools.eyes.logging.TraceLevel;
import com.applitools.eyes.logging.Type;
import com.applitools.eyes.positioning.PositionMemento;
import com.applitools.eyes.positioning.PositionProvider;
import com.applitools.eyes.selenium.positioning.NullRegionPositionCompensation;
import com.applitools.eyes.selenium.positioning.RegionPositionCompensation;
import com.applitools.utils.ArgumentGuard;
import com.applitools.utils.GeneralUtils;
import com.applitools.utils.ImageUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Collections;

public class FullPageCaptureAlgorithm {
    private static final int MIN_SCREENSHOT_PART_SIZE = 10;

    private final Logger logger;
    private final String testId;
    private final RegionPositionCompensation regionPositionCompensation;
    private final int waitBeforeScreenshots;
    private final DebugScreenshotsProvider debugScreenshotsProvider;
    private final EyesScreenshotFactory screenshotFactory;
    private final ScaleProviderFactory scaleProviderFactory;
    private final CutProvider cutProvider;
    private final int stitchingOverlap;
    private final ImageProvider imageProvider;
    private final ISizeAdjuster sizeAdjuster;
    private final int maxHeight;
    private final int maxArea;

    public FullPageCaptureAlgorithm(Logger logger, String testId, RegionPositionCompensation regionPositionCompensation,
                                    int waitBeforeScreenshots, DebugScreenshotsProvider debugScreenshotsProvider,
                                    EyesScreenshotFactory screenshotFactory,
                                    ScaleProviderFactory scaleProviderFactory, CutProvider cutProvider,
                                    int stitchingOverlap, ImageProvider imageProvider, int maxHeight, int maxArea,
                                    ISizeAdjuster sizeAdjuster) {

        ArgumentGuard.notNull(logger, "logger");

        this.logger = logger;
        this.testId = testId;
        this.waitBeforeScreenshots = waitBeforeScreenshots;
        this.debugScreenshotsProvider = debugScreenshotsProvider;
        this.screenshotFactory = screenshotFactory;
        this.scaleProviderFactory = scaleProviderFactory;
        this.cutProvider = cutProvider;
        this.stitchingOverlap = stitchingOverlap;
        this.imageProvider = imageProvider;
        this.sizeAdjuster = sizeAdjuster != null ? sizeAdjuster : NullSizeAdjuster.getInstance();
        this.maxHeight = maxHeight;
        this.maxArea = maxArea;

        this.regionPositionCompensation =
                regionPositionCompensation != null
                        ? regionPositionCompensation
                        : new NullRegionPositionCompensation();
    }

    private void saveDebugScreenshotPart(BufferedImage image, Region region, String name) {

        String suffix = String.format("part-%s-%d_%d_%dx%d",
                name, region.getLeft(), region.getTop(), region.getWidth(), region.getHeight());

        debugScreenshotsProvider.save(image, suffix);
    }

    /**
     * Returns a stitching of a region.
     * @param region           The region to stitch. If {@code Region.EMPTY}, the entire image will be stitched.
     * @param fullarea         The wanted getArea of the resulting image. If unknown, pass in {@code null} or {@code RectangleSize.EMPTY}.
     * @param positionProvider A provider of the scrolling implementation.
     * @return An image which represents the stitched region.
     */
    public BufferedImage getStitchedRegion(Region region, Region fullarea, PositionProvider positionProvider,
                                           PositionProvider originProvider, RectangleSize stitchOffset) {
        ArgumentGuard.notNull(region, "region");
        ArgumentGuard.notNull(positionProvider, "positionProvider");

        logger.log(testId, Stage.CHECK, Type.CAPTURE_SCREENSHOT,
                Pair.of("region", region),
                Pair.of("fullArea", fullarea),
                Pair.of("positionProvider", positionProvider.getClass().getName()));

        PositionMemento originalPosition = originProvider.getState();
        PositionMemento originalStitchedState = positionProvider.getState();
        originProvider.setPosition(Location.ZERO);

        try {
            Thread.sleep(waitBeforeScreenshots);
        } catch (InterruptedException ignored) {}

        BufferedImage initialScreenshot = imageProvider.getImage();
        RectangleSize initialSize = new RectangleSize(initialScreenshot.getWidth(), initialScreenshot.getHeight());

        saveDebugScreenshotPart(initialScreenshot, region, "initial");

        ScaleProvider scaleProvider = scaleProviderFactory.getScaleProvider(initialScreenshot.getWidth());
        double pixelRatio = 1 / scaleProvider.getScaleRatio();

        RectangleSize initialSizeScaled = new RectangleSize((int) Math.round(initialScreenshot.getWidth() / pixelRatio), (int) Math.round(initialScreenshot.getHeight() / pixelRatio));

        CutProvider scaledCutProvider = cutProvider.scale(pixelRatio);
        if (pixelRatio != 1 && !(scaledCutProvider instanceof NullCutProvider)) {
            initialScreenshot = cutProvider.cut(initialScreenshot);
            debugScreenshotsProvider.save(initialScreenshot, "original-cut");
        }

        Region regionInScreenshot = getRegionInScreenshot(region, initialScreenshot, pixelRatio);
        logger.log(testId, Stage.CHECK, Type.CAPTURE_SCREENSHOT, Pair.of("regionInScreenshot", regionInScreenshot));
        BufferedImage croppedInitialScreenshot = cropScreenshot(initialScreenshot, regionInScreenshot);
        debugScreenshotsProvider.save(croppedInitialScreenshot, "cropped");

        BufferedImage scaledInitialScreenshot = ImageUtils.scaleImage(croppedInitialScreenshot, scaleProvider.getScaleRatio());
        if (scaledInitialScreenshot != croppedInitialScreenshot) {
            saveDebugScreenshotPart(scaledInitialScreenshot, regionInScreenshot, "scaled");
        }

        if (fullarea.isEmpty()) {
            RectangleSize entireSize;
            try {
                entireSize = positionProvider.getEntireSize();
            } catch (EyesException e) {
                GeneralUtils.logExceptionStackTrace(logger, Stage.CHECK, Type.CAPTURE_SCREENSHOT, e, testId);
                entireSize = new RectangleSize(scaledInitialScreenshot.getWidth(), scaledInitialScreenshot.getHeight());
            }

            // Notice that this might still happen even if we used
            // "getImagePart", since "entirePageSize" might be that of a frame.
            if (scaledInitialScreenshot.getWidth() >= entireSize.getWidth() && scaledInitialScreenshot.getHeight() >= entireSize.getHeight()) {
                logger.log(TraceLevel.Warn, Collections.singleton(testId), Stage.CHECK, Type.CAPTURE_SCREENSHOT,
                        Pair.of("message", "Seems the image is already a full page screenshot"));
                originProvider.restoreState(originalPosition);
                return scaledInitialScreenshot;
            }
            fullarea = new Region(Location.ZERO, entireSize, CoordinatesType.SCREENSHOT_AS_IS);
            logger.log(testId, Stage.CHECK, Type.CAPTURE_SCREENSHOT,
                    Pair.of("entireSize", entireSize),
                    Pair.of("fullArea", fullarea));
        }

        float currentFullWidth = fullarea.getWidth();
        fullarea = sizeAdjuster.adjustRegion(fullarea, initialSizeScaled);
        logger.log(testId, Stage.CHECK, Type.CAPTURE_SCREENSHOT, Pair.of("adjustedFullArea", fullarea));

        float sizeRatio = currentFullWidth / fullarea.getWidth();
        Location scaledCropLocation = fullarea.getLocation();
        Location physicalCropLocation = new Location(
                (int) Math.ceil(scaledCropLocation.getX() * pixelRatio),
                (int) Math.ceil(scaledCropLocation.getY() * pixelRatio));

        Region sourceRegion;
        if (regionInScreenshot.isSizeEmpty()) {
            RectangleSize physicalCropSize = new RectangleSize(
                    initialSize.getWidth() - physicalCropLocation.getX(),
                    initialSize.getHeight() - physicalCropLocation.getY());
            sourceRegion = new Region(physicalCropLocation, physicalCropSize);
        } else {
            // Starting with the screenshot we already captured at (0,0).
            sourceRegion = regionInScreenshot;
        }

        Region scaledCroppedSourceRect = cutProvider.toRegion(sourceRegion.getSize());
        scaledCroppedSourceRect = scaledCroppedSourceRect.offset(sourceRegion.getLeft(), sourceRegion.getTop());
        Rectangle scaledCroppedSourceRegion = new Rectangle(
                (int) Math.ceil(scaledCroppedSourceRect.getLeft() / pixelRatio),
                (int) Math.ceil(scaledCroppedSourceRect.getTop() / pixelRatio),
                (int) Math.ceil(scaledCroppedSourceRect.getWidth() / pixelRatio),
                (int) Math.ceil(scaledCroppedSourceRect.getHeight() / pixelRatio));

        Dimension scaledCropSize = scaledCroppedSourceRegion.getSize();

        // The screenshot part is a bit smaller than the screenshot size, in order to eliminate
        // duplicate bottom/right-side scroll bars, as well as fixed position footers.
        RectangleSize screenshotPartSize = new RectangleSize(
                Math.max((int) scaledCropSize.getWidth(), MIN_SCREENSHOT_PART_SIZE),
                Math.max((int) scaledCropSize.getHeight(), MIN_SCREENSHOT_PART_SIZE)
        );

        // Getting the list of viewport regions composing the page (we'll take screenshot for each one).
        Rectangle rectInScreenshot;
        if (regionInScreenshot.isSizeEmpty()) {
            int x = Math.max(0, fullarea.getLeft());
            int y = Math.max(0, fullarea.getTop());
            int w = Math.min(fullarea.getWidth(), (int) scaledCropSize.getWidth());
            int h = Math.min(fullarea.getHeight(), (int) scaledCropSize.getHeight());
            rectInScreenshot = new Rectangle(
                    (int) Math.round(x * pixelRatio),
                    (int) Math.round(y * pixelRatio),
                    (int) Math.round(w * pixelRatio),
                    (int) Math.round(h * pixelRatio));
        } else {
            rectInScreenshot = new Rectangle(
                    regionInScreenshot.getLeft(), regionInScreenshot.getTop(),
                    regionInScreenshot.getWidth(), regionInScreenshot.getHeight());
        }

        fullarea = coerceImageSize(fullarea);
        logger.log(testId, Stage.CHECK, Type.CAPTURE_SCREENSHOT, Pair.of("finalFullArea", fullarea));
        SubregionForStitching[] screenshotParts = fullarea.getSubRegions(screenshotPartSize, stitchingOverlap, pixelRatio, rectInScreenshot, logger);

        BufferedImage stitchedImage = new BufferedImage(fullarea.getWidth(), fullarea.getHeight(), BufferedImage.TYPE_4BYTE_ABGR);
        // Take screenshot and stitch for each screenshot part.
        stitchScreenshot(stitchOffset, positionProvider, screenshotParts, stitchedImage, scaleProvider.getScaleRatio(), scaledCutProvider, sizeRatio);

        positionProvider.restoreState(originalStitchedState);
        originProvider.restoreState(originalPosition);

        return stitchedImage;
    }

    private Region coerceImageSize(Region fullarea) {
        if (fullarea.getHeight() < maxHeight && fullarea.getArea() < maxArea)
        {
            return fullarea;
        }

        if (maxArea == 0 || maxHeight == 0)
        {
            return fullarea;
        }

        int trimmedHeight = Math.min(maxArea / fullarea.getWidth(), maxHeight);
        Region newRegion = new Region(fullarea.getLeft(), fullarea.getTop(), fullarea.getWidth(), trimmedHeight, fullarea.getCoordinatesType());
        if (newRegion.isSizeEmpty())
        {
            return fullarea;
        }
        return newRegion;

    }

    private BufferedImage cropScreenshot(BufferedImage initialScreenshot, Region regionInScreenshot) {
        if (!regionInScreenshot.isSizeEmpty()) {
            BufferedImage croppedInitialScreenshot = ImageUtils.cropImage(initialScreenshot, regionInScreenshot);
            initialScreenshot = croppedInitialScreenshot;
            saveDebugScreenshotPart(croppedInitialScreenshot, regionInScreenshot, "cropped");
        }
        return initialScreenshot;
    }

    private void stitchScreenshot(RectangleSize stitchOffset, PositionProvider stitchProvider,
                                  SubregionForStitching[] screenshotParts, BufferedImage stitchedImage, double scaleRatio,
                                  CutProvider scaledCutProvider, float sizeRatio) {
        //noinspection unused
        int index = 0;
        logger.log(testId, Stage.CHECK, Type.CAPTURE_SCREENSHOT,
                Pair.of("stitchOffset", stitchOffset),
                Pair.of("scaleRatio", scaleRatio));

        for (SubregionForStitching partRegion : screenshotParts) {
            logger.log(TraceLevel.Info, Collections.singleton(testId), Stage.CHECK, Type.CAPTURE_SCREENSHOT,
                    Pair.of("scrollTo", new Location(partRegion.getScrollTo())),
                    Pair.of("pastePhysicalLocation", new Location(partRegion.getPastePhysicalLocation())),
                    Pair.of("physicalCropArea", new Region(partRegion.getLogicalCropArea())),
                    Pair.of("logicalCropArea", new Region(partRegion.getPhysicalCropArea())));

            // Scroll to the part's top/left
            Point partAbsoluteLocationInCurrentFrame = partRegion.getScrollTo();
            partAbsoluteLocationInCurrentFrame.translate(stitchOffset.getWidth(), stitchOffset.getHeight());
            Location scrollPosition = new Location(Math.round(partAbsoluteLocationInCurrentFrame.x * sizeRatio), Math.round(partAbsoluteLocationInCurrentFrame.y * sizeRatio));
            Location originPosition = stitchProvider.setPosition(scrollPosition);

            int dx = scrollPosition.getX() - originPosition.getX();
            int dy = scrollPosition.getY() - originPosition.getY();

            Point partPastePosition = partRegion.getPastePhysicalLocation();
            partPastePosition.translate(dx, dy);

            // Actually taking the screenshot.
            try {
                Thread.sleep(waitBeforeScreenshots);
            } catch (InterruptedException ignored) {}

            BufferedImage partImage = imageProvider.getImage();
            BufferedImage cutPart = scaledCutProvider.cut(partImage);
            BufferedImage croppedPart;
            Rectangle r = partRegion.getPhysicalCropArea();
            if (!r.isEmpty()) {
                croppedPart = ImageUtils.cropImage(cutPart, new Region(r.x, r.y, r.width, r.height));
            } else {
                croppedPart = cutPart;
            }

            Rectangle r2 = partRegion.getLogicalCropArea();

            BufferedImage scaledPartImage = ImageUtils.scaleImage(croppedPart, scaleRatio);
            BufferedImage scaledCroppedPartImage = ImageUtils.cropImage(scaledPartImage,  new Region(r2.x, r2.y, r2.width, r2.height));

            debugScreenshotsProvider.save(partImage, "partImage-" + originPosition.getX() + "_" + originPosition.getY());
            debugScreenshotsProvider.save(scaledCroppedPartImage, "scaledCroppedPartImage-" + partPastePosition.getX() + "_" + partPastePosition.getY());
            logger.log(TraceLevel.Info, Collections.singleton(testId), Stage.CHECK, Type.CAPTURE_SCREENSHOT,Pair.of("partPastePosition", partPastePosition));
            stitchedImage.getRaster().setRect(partPastePosition.x, partPastePosition.y, scaledCroppedPartImage.getData());
            index++;
        }

        debugScreenshotsProvider.save(stitchedImage, "stitched");
    }

    private Region getRegionInScreenshot(Region region, BufferedImage image, double pixelRatio) {
        if (region.isSizeEmpty()) {
            return region;
        }

        // We need the screenshot to be able to convert the region to screenshot coordinates.
        EyesScreenshot screenshot = screenshotFactory.makeScreenshot(image);

        // Region regionInScreenshot = screenshot.convertRegionLocation(regionProvider.getRegion(), regionProvider.getCoordinatesType(), CoordinatesType.SCREENSHOT_AS_IS);
        Region regionInScreenshot = screenshot.getIntersectedRegion(region, CoordinatesType.SCREENSHOT_AS_IS);

        RectangleSize scaledImageSize = new RectangleSize((int) Math.round(image.getWidth() / pixelRatio), (int) Math.round(image.getHeight() / pixelRatio));
        regionInScreenshot = sizeAdjuster.adjustRegion(regionInScreenshot, scaledImageSize);
        regionInScreenshot = regionInScreenshot.scale(pixelRatio);
        regionInScreenshot = regionPositionCompensation.compensateRegionPosition(regionInScreenshot, pixelRatio);

        // Handling a specific case where the region is actually larger than
        // the screenshot (e.g., when body width/height are set to 100%, and
        // an internal div is set to value which is larger than the viewport).
        regionInScreenshot.intersect(new Region(0, 0, image.getWidth(), image.getHeight()));
        return regionInScreenshot;
    }
}
