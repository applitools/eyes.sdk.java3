package com.applitools.eyes.images;

import com.applitools.eyes.LogHandler;
import com.applitools.eyes.Logger;
import com.applitools.eyes.Region;
import com.applitools.eyes.utils.ReportingTestSuite;
import com.applitools.utils.ImageUtils;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class TestImageUtils extends ReportingTestSuite {

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

        @Override
        public boolean isOpen() {
            return true;
        }

        public boolean contains (String message) {
            return messages.contains(message);
        }
    }

    public TestImageUtils() {
        super.setGroupName("images");
    }

    @Test
    public void TestCropImage_Regular() {
        BufferedImage image = ImageUtils.imageFromFile("resources/minions-800x500.jpg");
        Logger logger = new Logger();
        logger.setLogHandler(new TestLogHandler());
        BufferedImage cropped = ImageUtils.cropImage(logger, image, new Region(100, 100, 300, 200));
        Assert.assertEquals(cropped.getWidth(), 300, "widths differ");
        Assert.assertEquals(cropped.getHeight(), 200, "heights differ");
    }

    @Test
    public void TestCropImage_PartialObscured() {
        TestLogHandler testLogHandler = new TestLogHandler();
        Logger logger = new Logger();
        logger.setLogHandler(testLogHandler);
        BufferedImage image = ImageUtils.imageFromFile("resources/minions-800x500.jpg");
        BufferedImage cropped = ImageUtils.cropImage(logger, image, new Region(600, 350, 300, 300));
        Assert.assertEquals(cropped.getWidth(), 200, "widths differ");
        Assert.assertEquals(cropped.getHeight(), 150, "heights differ");
        Assert.assertTrue(testLogHandler.contains("[LOG    ] {} [1] com.applitools.utils.ImageUtils.cropImage(): WARNING - requested cropped getArea overflows image boundaries."));
    }

    @Test
    public void TestCropImage_AllObscured() {
        TestLogHandler testLogHandler = new TestLogHandler();
        Logger logger = new Logger();
        logger.setLogHandler(testLogHandler);
        BufferedImage image = ImageUtils.imageFromFile("resources/minions-800x500.jpg");
        BufferedImage cropped = ImageUtils.cropImage(logger, image, new Region(850, 100, 300, 200));
        Assert.assertEquals(cropped.getWidth(), 800, "widths differ");
        Assert.assertEquals(cropped.getHeight(), 500, "heights differ");
        Assert.assertTrue(testLogHandler.contains("[LOG    ] {} [1] com.applitools.utils.ImageUtils.cropImage(): WARNING - requested cropped getArea results in zero-size image! Cropped not performed. Returning original image."));
    }
}
