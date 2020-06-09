package com.applitools.eyes;

import com.applitools.utils.PropertyHandler;

/**
 * Abstraction for instantiating scale providers.
 */
public abstract class ScaleProviderFactory {

    protected Logger logger;
    private final PropertyHandler<ScaleProvider> scaleProviderHandler;

    /**
     *
     * @param logger The logger to use.
     * @param scaleProviderHandler A handler to update once a {@link ScaleProvider} instance is created.
     */
    public ScaleProviderFactory(Logger logger, PropertyHandler<ScaleProvider> scaleProviderHandler) {
        this.logger = logger;
        this.scaleProviderHandler = scaleProviderHandler;
    }

    /**
     * The main API for this factory.
     *
     * @param imageToScaleWidth The width of the image to scale. This parameter CAN be by class implementing
     *                          the factory, but this is not mandatory.
     * @return A {@link ScaleProvider} instance.
     */
    public ScaleProvider getScaleProvider(int imageToScaleWidth) {
        ScaleProvider scaleProvider = getScaleProviderImpl(imageToScaleWidth);
        scaleProviderHandler.set(scaleProvider);
        return scaleProvider;
    }

    /**
     * The implementation of getting/creating the scale provider, should be implemented by child classes.
     *
     *
     * @param imageToScaleWidth The width of the image to scale. This parameter CAN be by class implementing
     *                          the factory, but this is not mandatory.
     * @return The scale provider to be used.
     */
    protected abstract ScaleProvider getScaleProviderImpl(int imageToScaleWidth);
}
