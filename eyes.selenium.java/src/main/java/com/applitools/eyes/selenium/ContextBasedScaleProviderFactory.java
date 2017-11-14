package com.applitools.eyes.selenium;

import com.applitools.eyes.Logger;
import com.applitools.eyes.RectangleSize;
import com.applitools.eyes.ScaleProvider;
import com.applitools.eyes.ScaleProviderFactory;
import com.applitools.utils.PropertyHandler;

/**
 * Factory implementation for creating {@link ContextBasedScaleProvider} instances.
 */
public class ContextBasedScaleProviderFactory extends ScaleProviderFactory {

    private final Logger logger;
    private final RectangleSize topLevelContextEntireSize;
    private final RectangleSize viewportSize;
    private final double devicePixelRatio;
    private final boolean isMobileDevice;

    /**
     *
     *
     * @param topLevelContextEntireSize The total size of the top level
     *                                  context. E.g., for selenium this
     *                                  would be the document size of the top
     *                                  level frame.
     * @param viewportSize              The viewport size.
     * @param devicePixelRatio          The device pixel ratio of the
     *                                  platform on which the application is
     *                                  running.
     */
    public ContextBasedScaleProviderFactory(Logger logger, RectangleSize topLevelContextEntireSize,
                                            RectangleSize viewportSize, double devicePixelRatio, boolean isMobileDevice,
                                            PropertyHandler<ScaleProvider> scaleProviderHandler) {
        super(scaleProviderHandler);
        this.logger = logger;
        this.topLevelContextEntireSize = topLevelContextEntireSize;
        this.viewportSize = viewportSize;
        this.devicePixelRatio = devicePixelRatio;
        this.isMobileDevice = isMobileDevice;
    }

    @Override
    protected ScaleProvider getScaleProviderImpl(int imageToScaleWidth) {
        ContextBasedScaleProvider scaleProvider = new ContextBasedScaleProvider(logger, topLevelContextEntireSize,
                viewportSize, devicePixelRatio, isMobileDevice);
        scaleProvider.updateScaleRatio(imageToScaleWidth);
        return scaleProvider;
    }
}
