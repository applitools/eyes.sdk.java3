package com.applitools.eyes;

import com.applitools.utils.ArgumentGuard;
import com.applitools.utils.ImageUtils;

import java.awt.image.BufferedImage;

/**
 * Cut provider based on fixed cut values, run AFTER scaling (so coordinates should be normalized).
 */
@SuppressWarnings("WeakerAccess")
public class FixedCutProvider implements CutProvider {

    protected Logger logger = new Logger();
    protected final int header;
    protected final int footer;
    protected final int left;
    protected final int right;

    /**
     *
     * @param header The header to cut in pixels.
     * @param footer The footer to cut in pixels.
     * @param left The left to cut in pixels.
     * @param right The right to cut in pixels.
     */
    public FixedCutProvider(int header, int footer, int left, int right) {
        this.header = header;
        this.footer = footer;
        this.left = left;
        this.right = right;
    }

    public BufferedImage cut(BufferedImage image) {
        if (header == 0 && footer == 0 && left == 0 && right == 0) return image;
        Region targetRegion = new Region(left, header,
                image.getWidth() - left - right,
                image.getHeight() - header - footer);
        return ImageUtils.cropImage(logger, image, targetRegion);
    }

    public CutProvider scale(double scaleRatio) {
        int scaledHeader = (int) Math.ceil(header * scaleRatio);
        int scaledFooter = (int) Math.ceil(footer * scaleRatio);
        int scaledLeft = (int) Math.ceil(left * scaleRatio);
        int scaledRight = (int) Math.ceil(right * scaleRatio);

        FixedCutProvider cutProvider = new FixedCutProvider(scaledHeader, scaledFooter, scaledLeft, scaledRight);
        cutProvider.setLogger(logger);
        return cutProvider;
    }

    public void setLogger(Logger logger) {
        ArgumentGuard.notNull(logger, "logger");
        this.logger = logger;
    }

    @Override
    public Region toRegion(RectangleSize size) {

        Region rect = new Region(left, header,
                size.getWidth() - left - right,
                size.getHeight() - header - footer);

        return rect;
    }
}
