package com.applitools.eyes.images;

import com.applitools.eyes.*;
import com.applitools.eyes.images.utils.TestSetup;
import com.applitools.utils.ImageUtils;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

public class TestImagesApi extends TestSetup {

    private final String TEST_IMAGE = "src/main/resources/minions_jpeg.jpeg";

    @AfterMethod
    public void tearDown() {
        eyes.abortIfNotClosed();
    }

    @Test
    public void TestCheckImage() throws IOException {
        BufferedImage img = ImageIO.read(new URL("https://applitools.github.io/upload/appium.png"));

        eyes.open(getApplicationName(), "CheckImage", new RectangleSize(img.getWidth(), img.getHeight()));

        // buffered image
        eyes.checkImage(img);
        eyes.checkImage(img, "checkImage bufferedImage");

        // path
        eyes.checkImage(TEST_IMAGE);
        eyes.checkImage(TEST_IMAGE, "checkImage path");


        // byte[]
        eyes.checkImage(ImageUtils.encodeAsPng(img));
        eyes.checkImage(ImageUtils.encodeAsPng(img), "checkImage byte[]");

        eyes.close();
    }

    @Test
    public void TestCheckWindow() throws IOException {
        BufferedImage img = ImageIO.read(new URL("https://applitools.github.io/upload/appium.png"));

        eyes.open(getApplicationName(), "CheckWindow", new RectangleSize(img.getWidth(), img.getHeight()));

        eyes.checkWindow(img);
        eyes.checkWindow(img, "checkWindow");

        eyes.close();
    }

    @Test
    public void TestCheckRegion() throws IOException {
        BufferedImage img = ImageIO.read(new URL("https://applitools.github.io/upload/appium.png"));

        eyes.open(getApplicationName(), "CheckRegion", new RectangleSize(img.getWidth(), img.getHeight()));

        eyes.checkRegion(img, new Region(10, 10, 10, 10));
        eyes.checkRegion(img, new Region(10, 10, 10, 10), "checkRegion");

        eyes.close();
    }

    @Test
    public void TestCheckImage_Fluent() {
        BufferedImage img = ImageUtils.imageFromFile(TEST_IMAGE);

        eyes.open(getApplicationName(), "CheckFluent", new RectangleSize(img.getWidth(), img.getHeight()));
        eyes.check("TestCheckImage_Fluent", Target.image(TEST_IMAGE));
        eyes.close();
    }

    @Test
    public void TestCheckImage_WithIgnoreRegion_Fluent() {
        BufferedImage img = ImageUtils.imageFromFile(TEST_IMAGE);

        eyes.open(getApplicationName(), "CheckFluentIgnoreRegion", new RectangleSize(img.getWidth(), img.getHeight()));
        eyes.check("TestCheckImage_WithIgnoreRegion_Fluent", Target.image(TEST_IMAGE)
                .ignore(new Region(10, 20, 30, 40)));
        eyes.close();
    }

    @Test
    public void TestCheckImage_Fluent_CutProvider() {
        BufferedImage img = ImageUtils.imageFromFile(TEST_IMAGE);

        eyes.open(getApplicationName(), "CheckFluentCutProvider", new RectangleSize(img.getWidth(), img.getHeight()));
        eyes.setImageCut(new UnscaledFixedCutProvider(200, 100, 100, 50));
        eyes.check("TestCheckImage_Fluent", Target.image(TEST_IMAGE));
        eyes.close();
        eyes.setImageCut(null);
    }

    @Test
    public void TestCheckImage_Fluent_LazyLoad() {
        BufferedImage img = ImageUtils.imageFromFile(TEST_IMAGE);

        eyes.open(getApplicationName(), "CheckFluentLazyLoad", new RectangleSize(img.getWidth(), img.getHeight()));
        eyes.check("TestCheckImage_Fluent", Target.image(TEST_IMAGE).lazyLoad());
        eyes.close();
    }

    @Test
    public void TestCheckImage_Fluent_EnablePatterns() {
        BufferedImage img = ImageUtils.imageFromFile(TEST_IMAGE);

        eyes.open(getApplicationName(), "CheckFluentEnablePatterns", new RectangleSize(img.getWidth(), img.getHeight()));
        eyes.check("TestCheckImage_Fluent", Target.image(TEST_IMAGE).enablePatterns());
        eyes.close();
    }

    @Test
    public void TestCheckImage_Fluent_Layout() {
        BufferedImage img = ImageUtils.imageFromFile(TEST_IMAGE);

        eyes.open(getApplicationName(), "CheckFluentLayout", new RectangleSize(img.getWidth(), img.getHeight()));
        eyes.check("TestCheckImage_Fluent", Target.image(TEST_IMAGE).layout());
        eyes.close();
    }

    @Test
    public void TestCheckImage_Fluent_CloseAsync_GetAllTestResults() {
        EyesRunner runner = new ImageRunner();
        Eyes eyes1 = new Eyes(runner);

        BufferedImage img = ImageUtils.imageFromFile(TEST_IMAGE);

        eyes1.open(getApplicationName(), "GetAllTestResults", new RectangleSize(img.getWidth(), img.getHeight()));
        eyes1.check("TestCheckImage_Fluent", Target.image(TEST_IMAGE).layout());
        eyes1.closeAsync();

        TestResultsSummary result = runner.getAllTestResults(false);
        Assert.assertNotNull(result);

        System.out.println(result);
    }
}