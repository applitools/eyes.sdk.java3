package com.applitools.eyes.images;

import com.applitools.eyes.RectangleSize;
import com.applitools.eyes.utils.ReportingTestSuite;
import org.testng.annotations.Test;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

public class ImagesDemo extends ReportingTestSuite {

    public ImagesDemo() {
        super.setGroupName("images");
    }

    @Test
    public void testSanity() {
        Eyes eyes = new Eyes();
        BufferedImage img;
        try {
            // Start visual testing
            eyes.open("Eyes Images SDK", "Sanity Test", new RectangleSize(785, 1087));

            // Load page image and validate
            img = ImageIO.read(new URL("https://applitools.github.io/upload/appium.png"));
            // Visual validation point #1
            eyes.check("Contact page", Target.image(img));

            // End visual testing. Validate visual correctness.
            eyes.close();
        } catch (IOException ex) {
            System.out.println(ex.toString());
        } finally {
            eyes.abortIfNotClosed();
        }
    }
}
