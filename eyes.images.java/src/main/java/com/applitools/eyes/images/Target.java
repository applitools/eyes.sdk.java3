package com.applitools.eyes.images;

import com.applitools.ICheckSettings;
import com.applitools.eyes.Region;
import com.applitools.utils.ImageUtils;

import java.awt.image.BufferedImage;
import java.net.URL;

public class Target {

    public static ICheckSettings image(BufferedImage image) {
        return new ImagesCheckSettings(image);
    }

    public static ICheckSettings image(String path) {
        return image(ImageUtils.imageFromFile(path));
    }

    public static ICheckSettings image(byte[] imageBytes) {
        return image(ImageUtils.imageFromBytes(imageBytes));
    }

    public static ICheckSettings image(URL url) {
        return image(ImageUtils.imageFromUrl(url));
    }

    public static ICheckSettings region(BufferedImage image, Region region) {
        return new ImagesCheckSettings(image, region);
    }
}