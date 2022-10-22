package com.applitools.eyes.images;

import com.applitools.eyes.BatchInfo;
import com.applitools.eyes.EyesRunner;
import com.applitools.eyes.TestResults;
import com.applitools.eyes.TestResultsSummary;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;

public class ImagesDemo {

    private Eyes eyes;
    private EyesRunner runner;

    @BeforeMethod
    public void setup() {
        runner = new ImageRunner();
        eyes = new Eyes(runner);

        eyes.setApiKey(System.getenv("APPLITOOLS_API_KEY"));
        eyes.setBatch(new BatchInfo("Eyes Images Java"));
    }

    @AfterMethod
    public void teardown() {
        eyes.abortIfNotClosed();

        TestResultsSummary summary = runner.getAllTestResults();
        System.out.println(summary);
    }

    @Test
    public void testCheckSettings() throws IOException {
        eyes.open("Eyes Images SDK", "CheckSettings with BufferedImage");

        // Load page image and validate
        BufferedImage img = ImageIO.read(new URL("https://applitools.github.io/upload/appium.png"));

        eyes.check(Target.image(img).withName("test"));
        TestResults results = eyes.close();
        System.out.println(results);
    }
}