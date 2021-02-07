package com.applitools.eyes.appium;

import com.applitools.eyes.*;
import com.applitools.eyes.capture.EyesScreenshotFactory;
import com.applitools.eyes.capture.ImageProvider;
import com.applitools.eyes.debug.DebugScreenshotsProvider;
import com.applitools.eyes.logging.Stage;
import com.applitools.eyes.logging.TraceLevel;
import org.apache.commons.lang3.tuple.Pair;
import org.openqa.selenium.WebElement;

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
        // scrollViewRegion is the (upscaled) region of the scrollview on the screen
        Region scrollViewRegion = scaleSafe(((AppiumScrollPositionProvider) scrollProvider).getScrollableViewRegion());

        Location originalViewLocation = new Location(scrollViewRegion.getLeft(), scrollViewRegion.getTop());
        Location newLoc = new Location(originalViewLocation.getX(), originalViewLocation.getY() - scaleSafe(((AppiumScrollPositionProvider) scrollProvider).getStatusBarHeight()));
        RectangleSize newSize = new RectangleSize(initialPartSize.getWidth(), scrollViewRegion.getHeight());
        scrollViewRegion.setLocation(newLoc);
        scrollViewRegion.setSize(newSize);

        ContentSize contentSize = ((AppiumScrollPositionProvider) scrollProvider).getCachedContentSize();

        int xPos = scrollViewRegion.getLeft() + 1;
        Region regionToCrop;

        // We need to set position margin to avoid shadow at the top of view
        int oneScrollStep = scrollViewRegion.getHeight() - stitchingAdjustment;
        int maxScrollSteps = contentSize.getScrollContentHeight() / oneScrollStep;
        logger.log(TraceLevel.Debug, testId, Stage.CHECK,
                Pair.of("entireScrollableHeight", contentSize.getScrollContentHeight()),
                Pair.of("oneScrollStep", oneScrollStep));
        for (int step = 1; step <= maxScrollSteps; step++) {
            regionToCrop = new Region(0,
                    scrollViewRegion.getTop() + stitchingAdjustment,
                    initialPartSize.getWidth(),
                    scrollViewRegion.getHeight() - stitchingAdjustment);

            currentPosition = new Location(0,
                    scrollViewRegion.getTop() + ((scrollViewRegion.getHeight()) * (step)) - (stitchingAdjustment*step - stitchingAdjustment));

            // We should use original view location for scroll positions due to better calculation positions on the screen
            int startY = scrollViewRegion.getHeight() + originalViewLocation.getY() - 1 - (step != maxScrollSteps ? stitchingAdjustment/2 : 0);
            int endY = originalViewLocation.getY() + (step != maxScrollSteps ? stitchingAdjustment/2 : 0);
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

            if (step == maxScrollSteps) {
                int cropTo = contentSize.getScrollContentHeight() - (oneScrollStep * (step));
                int cropFrom = oneScrollStep - cropTo + scrollViewRegion.getTop() + stitchingAdjustment;
                regionToCrop = new Region(0,
                        cropFrom,
                        initialPartSize.getWidth(),
                        cropTo);
                currentPosition = new Location(0,
                        scrollViewRegion.getTop() + ((scrollViewRegion.getHeight()) * (step)) - (stitchingAdjustment*step));
            }
            captureAndStitchCurrentPart(regionToCrop);
        }

        int heightUnderScrollableView = initialPartSize.getHeight() - oneScrollStep - scrollViewRegion.getTop();
        if (heightUnderScrollableView > 0) { // check if there is views under the scrollable view
            regionToCrop = new Region(0, scrollViewRegion.getHeight() + scrollViewRegion.getTop() - stitchingAdjustment, initialPartSize.getWidth(), heightUnderScrollableView);

            currentPosition = new Location(0, scrollViewRegion.getTop() + contentSize.getScrollContentHeight() - stitchingAdjustment);

            captureAndStitchCurrentPart(regionToCrop);
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
