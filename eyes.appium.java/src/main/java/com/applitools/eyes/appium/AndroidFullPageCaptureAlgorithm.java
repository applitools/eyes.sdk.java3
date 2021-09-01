package com.applitools.eyes.appium;

import com.applitools.eyes.*;
import com.applitools.eyes.capture.EyesScreenshotFactory;
import com.applitools.eyes.capture.ImageProvider;
import com.applitools.eyes.debug.DebugScreenshotsProvider;
import com.applitools.eyes.logging.Stage;
import com.applitools.eyes.logging.TraceLevel;
import com.applitools.eyes.selenium.positioning.NullRegionPositionCompensation;
import org.apache.commons.lang3.tuple.Pair;
import org.openqa.selenium.WebElement;

import java.awt.image.BufferedImage;

public class AndroidFullPageCaptureAlgorithm extends AppiumFullPageCaptureAlgorithm {

    private String scrollableElementId = null;

    public AndroidFullPageCaptureAlgorithm(Logger logger, String testId,
                                           AppiumScrollPositionProvider scrollProvider,
                                           ImageProvider imageProvider, DebugScreenshotsProvider debugScreenshotsProvider,
                                           ScaleProviderFactory scaleProviderFactory, CutProvider cutProvider,
                                           EyesScreenshotFactory screenshotFactory, int waitBeforeScreenshots,
                                           Integer stitchingAdjustment, WebElement scrollRootElement) {

        super(logger, testId, scrollProvider, imageProvider, debugScreenshotsProvider,
            scaleProviderFactory, cutProvider, screenshotFactory, waitBeforeScreenshots, null, stitchingAdjustment, scrollRootElement);

        // Android returns pixel coordinates which are already scaled according to the pixel ratio
        this.coordinatesAreScaled = true;
        if (scrollRootElement != null) {
            this.scrollableElementId = scrollRootElement.getAttribute("resourceId").split("/")[1];
        }
    }

    @Override
    protected void captureAndStitchTailParts(RectangleSize entireSize, RectangleSize initialPartSize) {
        moveToTopLeft();
        ContentSize contentSize = ((AppiumScrollPositionProvider) scrollProvider).getCachedContentSize();

        int behaviorOffset = 0;
        if (scrollRootElement != null) {
            int offset = ((AndroidScrollPositionProvider) scrollProvider).getBehaviorOffsetWithHelperLibrary(scrollableElementId);
            if (offset != -1) {
                behaviorOffset = offset;
            }
        }
        int oneScrollStep = contentSize.getHeight() - stitchingAdjustment;
        int maxScrollSteps = contentSize.getScrollContentHeight() / oneScrollStep;

        int statusBarHeight = ((AppiumScrollPositionProvider) scrollProvider).getStatusBarHeight();
        int topPosition = contentSize.getTop() - statusBarHeight;

        currentPosition = new Location(0, 0);

        // part before scrollable content
        if (contentSize.getTop() - statusBarHeight > 0) {
            Region partRegion = new Region(0, 0, initialPartSize.getWidth(), topPosition);
            captureAndStitchCurrentPart(partRegion);
            currentPosition = new Location(0, currentPosition.getY() + partRegion.getHeight());
        }

        int xPos = contentSize.getLeft() + 1;

        boolean behaviorScrolled = false;
        // scrollable content
        for (int step = 0; step <= maxScrollSteps; step++) {
            BufferedImage partImage = imageProvider.getImage();
            debugScreenshotsProvider.save(partImage,
                    "original-scrolled=" + currentPosition.toStringForFilename());

            Region partRegion = new Region(0,
                    topPosition + (step != 0 ? stitchingAdjustment/2 : 0),
                    initialPartSize.getWidth(),
                    oneScrollStep - (step != 0 ? stitchingAdjustment/2 : 0));

            int startY = contentSize.getHeight() + contentSize.getTop() - 1 - (step != maxScrollSteps ? stitchingAdjustment/2 : 0);
            int endY = contentSize.getHeight() + contentSize.getTop() - oneScrollStep + (step != maxScrollSteps ? stitchingAdjustment/2 : 0);

            boolean isScrolledWithHelperLibrary = false;
            if (scrollableElementId != null) { // it means that we want to scroll on a specific element
                logger.log(TraceLevel.Debug, testId, Stage.CHECK,
                        Pair.of("scrollRootElementId", scrollableElementId));
                isScrolledWithHelperLibrary = ((AndroidScrollPositionProvider) scrollProvider).tryScrollWithHelperLibrary(scrollableElementId, (startY - endY), step, maxScrollSteps);
                if (step == maxScrollSteps && isScrolledWithHelperLibrary) {
                    // We should make additional scroll on parent in case of scrollable element inside ScrollView
                    ((AndroidScrollPositionProvider) scrollProvider).tryScrollWithHelperLibrary(scrollableElementId, (startY - endY), -1, maxScrollSteps);
                }
            }
            if (!isScrolledWithHelperLibrary) {
                // We should use release() touch action for the last scroll action
                // For some applications scrolling is not executed for the last part with cancel() action
                ((AppiumScrollPositionProvider) scrollProvider).scrollTo(xPos,
                        startY,
                        xPos,
                        endY,
                        step != maxScrollSteps);
            }

            if (step == maxScrollSteps && maxScrollSteps != 0) {
                int croppedHeight = contentSize.getScrollContentHeight() - (oneScrollStep * step) + (stitchingAdjustment*step) - stitchingAdjustment/2;
                if (croppedHeight > 0) {
                    int cropFrom = (contentSize.getHeight() + topPosition) - croppedHeight;
                    if (behaviorOffset > 0) {
                        int updatedPosition = ((AndroidScrollPositionProvider) scrollProvider).getFirstScrollableView().getLocation().getY() - statusBarHeight;
                        cropFrom += updatedPosition - stitchingAdjustment - stitchingAdjustment/4;
                    }
                    partRegion = new Region(0,
                            cropFrom,
                            initialPartSize.getWidth(),
                            croppedHeight);
                }
                currentPosition = new Location(0, currentPosition.getY());
            }

            setRegionInScreenshot(partImage, partRegion, new NullRegionPositionCompensation());
            partImage = cropPartToRegion(partImage, partRegion);
            stitchPartIntoContainer(partImage);
            currentPosition = new Location(0, currentPosition.getY() + partImage.getHeight() - stitchingAdjustment/2);

            if (step == maxScrollSteps - 1 && behaviorOffset > 0) {
                behaviorScrolled = ((AndroidScrollPositionProvider) scrollProvider).tryScrollBehaviorOffsetWithHelperLibrary(scrollableElementId, behaviorOffset);
            }
        }

        // part after scrollable content
        int remainingHeight = initialPartSize.getHeight() - topPosition - contentSize.getHeight();
        if (remainingHeight > 0) {
            currentPosition = new Location(0, currentPosition.getY() + stitchingAdjustment/2);
            Region partRegion = new Region(0, topPosition + contentSize.getHeight(), initialPartSize.getWidth(), remainingHeight);
            captureAndStitchCurrentPart(partRegion);
            currentPosition = new Location(0, currentPosition.getY() + partRegion.getHeight());
        }

        if (behaviorScrolled) {
            ((AndroidScrollPositionProvider) scrollProvider).tryScrollBehaviorOffsetWithHelperLibrary(scrollableElementId, -behaviorOffset);
        }
        moveToTopLeft();
    }

    @Override
    protected void moveToTopLeft() {
        boolean isScrolledWithHelperLibrary = false;
        if (scrollableElementId != null) {
            isScrolledWithHelperLibrary = ((AndroidScrollPositionProvider) scrollProvider).moveToTop(scrollableElementId);
        }
        if (!isScrolledWithHelperLibrary) {
            super.moveToTopLeft();
        }
    }

    @Override
    protected void moveToTopLeft(int startX, int startY, int endX, int endY) {
        // For Android we should use simple moveToTopLeft() because this method was created for iOS
        moveToTopLeft();
    }
}
