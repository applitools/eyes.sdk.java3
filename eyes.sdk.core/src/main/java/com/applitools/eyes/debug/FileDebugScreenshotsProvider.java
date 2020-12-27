package com.applitools.eyes.debug;

import com.applitools.eyes.Logger;
import com.applitools.utils.ImageUtils;

import java.awt.image.BufferedImage;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * A debug screenshot provider for saving screenshots to file.
 */
public class FileDebugScreenshotsProvider extends DebugScreenshotsProvider {

    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss_SSS");
    private final Logger logger;

    public FileDebugScreenshotsProvider(Logger logger) {
        this.logger = logger;
    }

    @Override
    public void save(BufferedImage image, String suffix) {
        String filename = getPath() + getPrefix() + getFormattedTimeStamp() + "_" + suffix + ".png";
        ImageUtils.saveImage(image, filename);
    }

    private String getFormattedTimeStamp(){
        return dateFormat.format(Calendar.getInstance().getTime());
    }
}
