package com.applitools.eyes;

import com.applitools.utils.PropertyHandler;

import java.awt.image.BufferedImage;

/**
 * Factory implementation for creating {@link FixedScaleProvider} instances.
 */
public class FixedScaleProviderFactory extends ScaleProviderFactory {

    private final double scaleRatio;

    /**
     *
     * @param scaleRatio The ratio to scale by.
     */
    public FixedScaleProviderFactory(double scaleRatio,
                                     PropertyHandler<ScaleProvider> scaleProviderHandler) {
        super(scaleProviderHandler);
        this.scaleRatio = scaleRatio;
    }

    @Override
    protected ScaleProvider getScaleProviderImpl(int imageToScaleWidth) {
        return new FixedScaleProvider(scaleRatio);
    }
}
