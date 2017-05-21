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
     * @param scaleProvider The {@link ScaleProvider}
     */
    public ScaleProviderIdentityFactory(ScaleProvider scaleProvider,
                                        PropertyHandler<ScaleProvider> scaleProviderHandler) {
        super(scaleProviderHandler);
        this.scaleProvider = scaleProvider;
    }

    @Override
    protected ScaleProvider getScaleProviderImpl(int imageToScaleWidth) {
        return scaleProvider;
    }
}
