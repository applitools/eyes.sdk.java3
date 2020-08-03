package com.applitools.eyes.images;

import com.applitools.eyes.RectangleSize;
import com.applitools.eyes.config.Configuration;
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
        Configuration configuration = eyes.getConfiguration();
        configuration.setViewportSize(new RectangleSize(785, 1087));
        eyes.setConfiguration(configuration);
        BufferedImage img;
        try {
            // Start visual testing
            eyes.open("Eyes Images SDK", "Sanity Test");

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
