package com.applitools.eyes.scaling;

import com.applitools.eyes.ScaleProvider;
import com.applitools.eyes.ScaleProviderFactory;
import com.applitools.utils.PropertyHandler;

/**
 * Factory implementation for creating {@link FixedScaleProvider} instances.
 */
public class FixedScaleProviderFactory extends ScaleProviderFactory {

    private FixedScaleProvider fixedScaleProvider;

    /**
     *
     * @param scaleRatio The ratio to scale by.
     */
    public FixedScaleProviderFactory(double scaleRatio,
                                     PropertyHandler<ScaleProvider> scaleProviderHandler) {
        super(scaleProviderHandler);
        this.fixedScaleProvider = new FixedScaleProvider(scaleRatio);
    }

    @Override
    protected ScaleProvider getScaleProviderImpl(int imageToScaleWidth) {
        return fixedScaleProvider;
    }
}
