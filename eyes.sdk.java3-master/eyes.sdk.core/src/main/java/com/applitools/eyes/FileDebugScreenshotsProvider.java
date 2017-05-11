package com.applitools.eyes;

import com.applitools.utils.ImageUtils;

import java.awt.image.BufferedImage;
import java.util.Calendar;

/**
 * A debug screenshot provider for saving screenshots to file.
 */
public class FileDebugScreenshotsProvider extends DebugScreenshotsProvider {

    @Override
    public void save(BufferedImage image, String suffix) {
        String filename = getPath() + getPrefix() + Calendar.getInstance().getTimeInMillis() + "_" + suffix + ".png";
        ImageUtils.saveImage(image, filename.replace(" ", "_"));
    }
}
