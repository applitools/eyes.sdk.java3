package com.applitools.eyes.images;

import com.applitools.ICheckSettings;
import com.applitools.eyes.Region;
import com.applitools.utils.ImageUtils;

import java.awt.image.BufferedImage;
import java.net.URL;

public class Target {

    /**
     * Specify the target as image.
     *
     * @param image the buffered image.
     * @return the check settings
     */
    public static ICheckSettings image(BufferedImage image) {
        return new ImagesCheckSettings(image);
    }

    /**
     * Specify the target as image.
     *
     * @param imageBytes byte array of the image.
     * @return the check settings
     */
    public static ICheckSettings image(byte[] imageBytes) {
        return image(ImageUtils.imageFromBytes(imageBytes));
    }

    /**
     * Specify the target as image.
     *
     * @param path the path to the image, or the url if remote.
     * @return the check settings
     */
    public static ICheckSettings image(String path) {
        return new ImagesCheckSettings(path);
    }

    /**
     * Specify the target as a region.
     *
     * @param image the image.
     * @param region the region to capture inside the image.
     * @return the check settings
     */
    public static ICheckSettings region(BufferedImage image, Region region) {
        return new ImagesCheckSettings(image, region);
    }
}