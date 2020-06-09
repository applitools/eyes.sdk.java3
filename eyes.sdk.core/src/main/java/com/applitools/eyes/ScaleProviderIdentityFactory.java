package com.applitools.eyes;

import com.applitools.utils.PropertyHandler;

import java.awt.image.BufferedImage;

/**
 * Factory implementation which simply returns the scale provider it is given as an argument.
 */
public class ScaleProviderIdentityFactory extends ScaleProviderFactory {

    private final ScaleProvider scaleProvider;

    /**
     *
     * @param logger The logger to use.
     * @param scaleProvider The {@link ScaleProvider}
     * @param scaleProviderHandler The handler to set(get) the scale provider on(to).
     */
    public ScaleProviderIdentityFactory(Logger logger, ScaleProvider scaleProvider,
                                        PropertyHandler<ScaleProvider> scaleProviderHandler) {
        super(logger, scaleProviderHandler);
        this.scaleProvider = scaleProvider;
    }

    @Override
    protected ScaleProvider getScaleProviderImpl(int imageToScaleWidth) {
        return scaleProvider;
    }
}
