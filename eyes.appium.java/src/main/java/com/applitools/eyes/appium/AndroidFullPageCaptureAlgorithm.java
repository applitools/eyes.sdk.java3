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
        moveToTopLeft();
        int behaviorOffset = 0;
        boolean behaviorScrolled;
        int offset = ((AndroidScrollPositionProvider) scrollProvider).getBehaviorOffsetWithHelperLibrary();
        if (offset != -1) {
            behaviorOffset = offset;
        }
        int scrollViewYBeforeBehaviorScrolled = getScrollableViewY();
        behaviorScrolled = ((AndroidScrollPositionProvider) scrollProvider).tryScrollBehaviorOffsetWithHelperLibrary(scrollableElementId, behaviorOffset);
        int scrollViewYAfterBehaviorScrolled = getScrollableViewY();
        int scrollableViewHeight = getScrollableViewHeight();
        RectangleSize lastSuccessfulPartSize = new RectangleSize(initialPartSize.getWidth(), initialPartSize.getHeight());

        ContentSize contentSize = ((AppiumScrollPositionProvider) scrollProvider).getCachedContentSize();
        int xPos = getScrollableViewX() + getScrollableViewWidth() / 2;
        Region regionToCrop;

        // We need to set position margin to avoid shadow at the top of view
        int oneScrollStep = scrollableViewHeight - stitchingAdjustment;
        int maxScrollSteps = contentSize.getScrollContentHeight() / oneScrollStep;
        logger.log(TraceLevel.Debug, testId, Stage.CHECK,
                Pair.of("entireScrollableHeight", contentSize.getScrollContentHeight()),
                Pair.of("oneScrollStep", oneScrollStep));
        for (int step = 1; step <= maxScrollSteps; step++) {
            regionToCrop = new Region(0,
                    scrollViewYAfterBehaviorScrolled + (step == 1 ? 0 : stitchingAdjustment),
                    initialPartSize.getWidth(),
                    oneScrollStep + (step == 1 ? stitchingAdjustment : 0));
            if (step == 1) {
                currentPosition = new Location(0,
                        (scrollViewYBeforeBehaviorScrolled + statusBarHeight));
            }
            lastSuccessfulPartSize = captureAndStitchCurrentPart(regionToCrop);

            int startY = scrollableViewHeight + scrollViewYAfterBehaviorScrolled - 1 - (step != maxScrollSteps ? stitchingAdjustment/2 : 0);
            int endY = scrollViewYAfterBehaviorScrolled + (step != maxScrollSteps ? stitchingAdjustment/2 : 0);
            boolean isScrolledWithHelperLibrary = false;
            if (scrollableElementId != null) { // it means that we want to scroll on a specific element
                logger.log(TraceLevel.Debug, testId, Stage.CHECK,
                        Pair.of("scrollRootElementId", scrollableElementId));
                isScrolledWithHelperLibrary = ((AndroidScrollPositionProvider) scrollProvider).tryScrollWithHelperLibrary(scrollableElementId, (startY - endY), step, maxScrollSteps);
            }
            if (!isScrolledWithHelperLibrary) {
                ((AppiumScrollPositionProvider) scrollProvider).scrollTo(xPos,
                        startY,
                        xPos,
                        endY,
                        false);//step != maxScrollSteps);
            }
            currentPosition = new Location(0, currentPosition.getY() + lastSuccessfulPartSize.getHeight());
            if (step == maxScrollSteps) {
                if (isScrolledWithHelperLibrary) {
                    // We should make additional scroll on parent in case of scrollable element inside ScrollView
                    ((AndroidScrollPositionProvider) scrollProvider).tryScrollWithHelperLibrary(scrollableElementId, (startY - endY), -1, maxScrollSteps);
                }
                int cropTo = contentSize.getScrollContentHeight() - (oneScrollStep * (step)) - stitchingAdjustment;
                int cropFrom = scrollableViewHeight + scrollViewYAfterBehaviorScrolled - cropTo;
                if (cropTo > 0) {
                    regionToCrop = new Region(0,
                            cropFrom - stitchingAdjustment,
                            initialPartSize.getWidth(),
                            cropTo + stitchingAdjustment);
                    currentPosition = new Location(0, currentPosition.getY() - stitchingAdjustment);
                    lastSuccessfulPartSize = captureAndStitchCurrentPart(regionToCrop);
                    currentPosition = new Location(0, currentPosition.getY() + lastSuccessfulPartSize.getHeight());
                }
            }
        }
        int heightUnderScrollableView = initialPartSize.getHeight() - scrollableViewHeight - scrollViewYAfterBehaviorScrolled;
        if (heightUnderScrollableView > 0) { // check if there is views under the scrollable view
            regionToCrop = new Region(0, scrollableViewHeight + scrollViewYAfterBehaviorScrolled, initialPartSize.getWidth(), heightUnderScrollableView);
            lastSuccessfulPartSize = captureAndStitchCurrentPart(regionToCrop);
        }

        if (behaviorScrolled) {
            ((AndroidScrollPositionProvider) scrollProvider).tryScrollBehaviorOffsetWithHelperLibrary(scrollableElementId, -behaviorOffset);
        }
        cleanupStitch(null, currentPosition, lastSuccessfulPartSize);

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

    private int getScrollableViewY() {
        WebElement element = ((AndroidScrollPositionProvider) scrollProvider).getFirstScrollableView();
        return element.getLocation().getY() - ((AndroidScrollPositionProvider) scrollProvider).getStatusBarHeight();
    }

    private int getScrollableViewX() {
        WebElement element = ((AndroidScrollPositionProvider) scrollProvider).getFirstScrollableView();
        return element.getLocation().getX();
    }

    private int getScrollableViewHeight() {
        WebElement element = ((AndroidScrollPositionProvider) scrollProvider).getFirstScrollableView();
        return element.getSize().getHeight();
    }

    private int getScrollableViewWidth() {
        WebElement element = ((AndroidScrollPositionProvider) scrollProvider).getFirstScrollableView();
        return element.getSize().getWidth();
    }
}
