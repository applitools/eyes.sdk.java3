package com.applitools.eyes.selenium;

import com.applitools.eyes.Logger;
import com.applitools.eyes.RectangleSize;
import com.applitools.eyes.ScaleProvider;
import com.applitools.eyes.logging.Stage;
import com.applitools.eyes.logging.TraceLevel;
import com.applitools.utils.ArgumentGuard;
import org.apache.commons.lang3.tuple.Pair;

public class ContextBasedScaleProvider implements ScaleProvider {

    // Allowed deviations for viewport size and default content entire size.
    private static final int ALLOWED_VS_DEVIATION = 1;
    private static final int ALLOWED_DCES_DEVIATION = 10;
    private static final int UNKNOWN_SCALE_RATIO = 0;

    private final Logger logger;

    private final double devicePixelRatio;
    private final RectangleSize topLevelContextEntireSize;
    private final RectangleSize viewportSize;
    private final boolean isMobileDevice;
    private double scaleRatio;

    private static double getScaleRatioToViewport(int viewportWidth, int imageToScaleWidth, double currentScaleRatio) {
        int scaledImageWidth = (int)Math.round(imageToScaleWidth * currentScaleRatio);
        double fromScaledToViewportRatio = ((double)viewportWidth) / scaledImageWidth;
        return currentScaleRatio * fromScaledToViewportRatio;
    }

    /**
     *
     * @param topLevelContextEntireSize The total size of the top level
     *                                  context. E.g., for selenium this
     *                                  would be the document size of the top
     *                                  level frame.
     * @param viewportSize              The viewport size.
     * @param devicePixelRatio          The device pixel ratio of the
     *                                  platform on which the application is
     *                                  running.
     * @param logger                    the logger.
     * @param isMobileDevice            is mobile device.
     */
    @SuppressWarnings("WeakerAccess")
    public ContextBasedScaleProvider(Logger logger,
            RectangleSize topLevelContextEntireSize, RectangleSize viewportSize,
            double devicePixelRatio, boolean isMobileDevice) {

        ArgumentGuard.notNull(logger, "logger");
        this.logger = logger;
        this.topLevelContextEntireSize = topLevelContextEntireSize;
        this.viewportSize = viewportSize;
        this.devicePixelRatio = devicePixelRatio;
        this.isMobileDevice = isMobileDevice;
        // Since we need the image size to decide what the scale ratio is.
        scaleRatio = UNKNOWN_SCALE_RATIO;
    }

    /**
     *
     * {@inheritDoc}
     */
    public double getScaleRatio() {
        ArgumentGuard.isValidState(scaleRatio != UNKNOWN_SCALE_RATIO,
                "scaleRatio not defined yet");
        return scaleRatio;
    }

    /**
     * Set the scale ratio based on the given image.
     * @param imageToScaleWidth The width of the image to scale, used for calculating the scale ratio.
     */
    public void updateScaleRatio(int imageToScaleWidth) {
        int viewportWidth = viewportSize.getWidth();
        int dcesWidth = topLevelContextEntireSize.getWidth();

        // If the image's width is the same as the viewport's width or the
        // top level context's width, no scaling is necessary.
        if (((imageToScaleWidth >= viewportWidth - ALLOWED_VS_DEVIATION)
                && (imageToScaleWidth <= viewportWidth + ALLOWED_VS_DEVIATION))
                || ((imageToScaleWidth >= dcesWidth - ALLOWED_DCES_DEVIATION)
                && imageToScaleWidth <= dcesWidth + ALLOWED_DCES_DEVIATION)) {
            scaleRatio = 1;
        } else {
            scaleRatio = 1 / devicePixelRatio;
            if (isMobileDevice) {
                scaleRatio = getScaleRatioToViewport(viewportWidth, imageToScaleWidth, scaleRatio);
            }
            logger.log(TraceLevel.Info, null, Stage.GENERAL, Pair.of("scaleRatio", scaleRatio));
        }
    }
}
