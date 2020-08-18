package com.applitools.eyes.images;

import com.applitools.eyes.RectangleSize;
import com.applitools.eyes.StdoutLogHandler;
import com.applitools.eyes.TestResults;
import com.applitools.eyes.config.Configuration;
import com.applitools.eyes.metadata.StartInfo;
import com.applitools.eyes.utils.ReportingTestSuite;
import com.applitools.eyes.utils.TestUtils;
import org.testng.Assert;
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
        eyes.setLogHandler(new StdoutLogHandler());
        BufferedImage img;
        try {
            // Start visual testing
            eyes.open("Eyes Images SDK", "Sanity Test");

            // Load page image and validate
            img = ImageIO.read(new URL("https://applitools.github.io/upload/appium.png"));
            // Visual validation point #1
            eyes.check("Contact page", Target.image(img));

            // End visual testing. Validate visual correctness.
            TestResults results = eyes.close();
            StartInfo startInfo = TestUtils.getSessionResults(eyes.getApiKey(), results).getStartInfo();
            Assert.assertEquals(startInfo.getEnvironment().getDisplaySize(), new RectangleSize(img.getWidth(), img.getHeight()));
        } catch (IOException ex) {
            System.out.println(ex.toString());
        } finally {
            eyes.abortIfNotClosed();
        }
    }
}
