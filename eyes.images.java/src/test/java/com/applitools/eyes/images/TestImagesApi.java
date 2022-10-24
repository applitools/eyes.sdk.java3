package com.applitools.eyes.images;

import com.applitools.eyes.EyesRunner;
import com.applitools.eyes.Region;
import com.applitools.eyes.TestResultsSummary;
import com.applitools.eyes.UnscaledFixedCutProvider;
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

    private final String TEST_IMAGE = "src/main/resources/minions.jpeg";

    @AfterMethod
    public void tearDown() {
        eyes.abortIfNotClosed();
    }

    @Test
    public void TestCheckImage() throws IOException {
        eyes.open(getApplicationName(), "CheckImage");

        // path
        eyes.checkImage(TEST_IMAGE);
        eyes.checkImage(TEST_IMAGE, "checkImage path");

        // buffered image
        BufferedImage img = ImageIO.read(new URL("https://applitools.github.io/upload/appium.png"));
        eyes.checkImage(img);
        eyes.checkImage(img, "checkImage bufferedImage");

        // byte[]
        eyes.checkImage(ImageUtils.encodeAsPng(img));
        eyes.checkImage(ImageUtils.encodeAsPng(img), "checkImage byte[]");

        eyes.close();
    }

    @Test
    public void TestCheckWindow() throws IOException {
        eyes.open(getApplicationName(), "CheckWindow");

        BufferedImage img = ImageIO.read(new URL("https://applitools.github.io/upload/appium.png"));
        eyes.checkWindow(img);
        eyes.checkWindow(img, "checkWindow");

        eyes.close();
    }

    @Test
    public void TestCheckRegion() throws IOException {
        eyes.open(getApplicationName(), "CheckRegion");

        BufferedImage img = ImageIO.read(new URL("https://applitools.github.io/upload/appium.png"));
        eyes.checkRegion(img, new Region(10, 10, 10, 10));
        eyes.checkRegion(img, new Region(10, 10, 10, 10), "checkRegion");

        eyes.close();
    }

    @Test
    public void TestCheckImage_Fluent() {
        eyes.open(getApplicationName(), "CheckFluent");
        eyes.check("TestCheckImage_Fluent", Target.image(TEST_IMAGE));
        eyes.close();
    }

    @Test
    public void TestCheckImage_WithIgnoreRegion_Fluent() {
        eyes.open(getApplicationName(), "CheckFluentIgnoreRegion");
        eyes.check("TestCheckImage_WithIgnoreRegion_Fluent", Target.image(TEST_IMAGE)
                .ignore(new Region(10, 20, 30, 40)));
        eyes.close();
    }

    @Test
    public void TestCheckImage_Fluent_CutProvider() {
        eyes.open(getApplicationName(), "CheckFluentCutProvider");
        eyes.setImageCut(new UnscaledFixedCutProvider(200, 100, 100, 50));
        eyes.check("TestCheckImage_Fluent", Target.image(TEST_IMAGE));
        eyes.close();
    }

    @Test
    public void TestCheckImage_Fluent_LazyLoad() {
        eyes.open(getApplicationName(), "CheckFluentLazyLoad");
        eyes.check("TestCheckImage_Fluent", Target.image(TEST_IMAGE).lazyLoad());
        eyes.close();
    }

//    @Test
//    public void TestCheckImage_Fluent_DebugScreenshots() {
//        eyes.setDebugScreenshotsPath("./");
//        eyes.open(getApplicationName(), "CheckFluentDebugScreenshots");
//        eyes.check("TestCheckImage_Fluent", Target.image(TEST_IMAGE).lazyLoad());
//        eyes.close();
//    }

    @Test
    public void TestCheckImage_Fluent_EnablePatterns() {
        eyes.open(getApplicationName(), "CheckFluentEnablePatterns");
        eyes.check("TestCheckImage_Fluent", Target.image(TEST_IMAGE).enablePatterns());
        eyes.close();
    }

    @Test
    public void TestCheckImage_Fluent_Layout() {
        eyes.open(getApplicationName(), "CheckFluentLayout");
        eyes.check("TestCheckImage_Fluent", Target.image(TEST_IMAGE).layout());
        eyes.close();
    }

    @Test
    public void TestCheckImage_Fluent_CloseAsync_GetAllTestResults() {
        EyesRunner runner = new ImageRunner();
        Eyes eyes1 = new Eyes(runner);

        eyes1.open(getApplicationName(), "GetAllTestResults");
        eyes1.check("TestCheckImage_Fluent", Target.image(TEST_IMAGE).layout());
        eyes1.closeAsync();

        TestResultsSummary result = runner.getAllTestResults(false);
        Assert.assertNotNull(result);

        System.out.println(result);
    }
}