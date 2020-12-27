package com.applitools.eyes.images;

import com.applitools.eyes.Region;
import com.applitools.eyes.utils.ReportingTestSuite;
import com.applitools.utils.ImageUtils;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.awt.image.BufferedImage;

public class TestImageUtils extends ReportingTestSuite {

    public TestImageUtils() {
        super.setGroupName("images");
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
        BufferedImage image = ImageUtils.imageFromFile("resources/minions-800x500.jpg");
        BufferedImage cropped = ImageUtils.cropImage(image, new Region(600, 350, 300, 300));
        Assert.assertEquals(cropped.getWidth(), 200, "widths differ");
        Assert.assertEquals(cropped.getHeight(), 150, "heights differ");
    }

    @Test
    public void TestCropImage_AllObscured() {
        BufferedImage image = ImageUtils.imageFromFile("resources/minions-800x500.jpg");
        BufferedImage cropped = ImageUtils.cropImage(image, new Region(850, 100, 300, 200));
        Assert.assertEquals(cropped.getWidth(), 800, "widths differ");
        Assert.assertEquals(cropped.getHeight(), 500, "heights differ");
    }
}
