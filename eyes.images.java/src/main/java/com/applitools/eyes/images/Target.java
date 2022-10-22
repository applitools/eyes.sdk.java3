package com.applitools.eyes.images;

import com.applitools.ICheckSettings;
import com.applitools.utils.ImageUtils;

import java.awt.image.BufferedImage;

public class Target {

    public static ICheckSettings image(BufferedImage image) {
        return new ImagesCheckSettings(image);
    }

    public static ICheckSettings image(String path) {
        return image(ImageUtils.imageFromFile(path));
    }
}