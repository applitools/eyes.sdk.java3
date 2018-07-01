package com.applitools.eyes.images;

import com.applitools.eyes.LogHandler;
import com.applitools.eyes.Region;
import com.applitools.utils.ImageUtils;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class TestImageUtils {

    public class TestLogHandler implements LogHandler {

        private ArrayList<String> messages = new ArrayList<>();

        @Override
        public void open() {}

        @Override
        public void onMessage(boolean verbose, String logString) {
            messages.add(logString);
        }

        @Override
        public void close() {}

        public boolean contains (String message) {
            return messages.contains(message);
        }
    }

    @Test
    public void TestCropImage_Regular() {
        BufferedImage image = ImageUtils.imageFromFile("resources/minions-800x500.jpg");
        BufferedImage cropped = ImageUtils.cropImage(image, new Region(100, 100, 300, 200));
        Assert.assertEquals(cropped.getWidth(), 300, "widths differ");
        Assert.assertEquals(cropped.getHeight(), 200, "heights differ");
    }

    @Test
    public void TestCropImage_PartialObscured() {
        TestLogHandler testLogHandler = new TestLogHandler();
        ImageUtils.setLogHandler(testLogHandler);
        BufferedImage image = ImageUtils.imageFromFile("resources/minions-800x500.jpg");
        BufferedImage cropped = ImageUtils.cropImage(image, new Region(600, 350, 300, 300));
        Assert.assertEquals(cropped.getWidth(), 200, "widths differ");
        Assert.assertEquals(cropped.getHeight(), 150, "heights differ");
        Assert.assertTrue(testLogHandler.contains("[LOG    ] {} com.applitools.utils.ImageUtils.cropImage(): WARNING - requested cropped area overflows image boundaries."));
    }

    @Test
    public void TestCropImage_AllObscured() {
        TestLogHandler testLogHandler = new TestLogHandler();
        ImageUtils.setLogHandler(testLogHandler);
        BufferedImage image = ImageUtils.imageFromFile("resources/minions-800x500.jpg");
        BufferedImage cropped = ImageUtils.cropImage(image, new Region(850, 100, 300, 200));
        Assert.assertEquals(cropped.getWidth(), 800, "widths differ");
        Assert.assertEquals(cropped.getHeight(), 500, "heights differ");
        Assert.assertTrue(testLogHandler.contains("[LOG    ] {} com.applitools.utils.ImageUtils.cropImage(): WARNING - requested cropped area results in zero-size image! Cropped not performed. Returning original image."));
    }
}
