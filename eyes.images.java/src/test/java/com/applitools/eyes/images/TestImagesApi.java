package com.applitools.eyes.images;

import com.applitools.eyes.*;
import com.applitools.eyes.locators.TextRegion;
import com.applitools.eyes.locators.TextRegionSettings;
import com.applitools.eyes.utils.ReportingTestSuite;
import com.applitools.utils.ImageUtils;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

public class TestImagesApi extends ReportingTestSuite {
    private Eyes eyes;
    private static final String TEST_SUITE_NAME = "Eyes Image SDK";
    private static BatchInfo batchInfo;

    public TestImagesApi() {
        super.setGroupName("images");
    }

    @BeforeClass
    public static void setUpOnce() {
        batchInfo = new BatchInfo(TEST_SUITE_NAME);
    }

    @BeforeMethod
    public void setup(Method method) {
        eyes = new Eyes();

        LogHandler logHandler = new StdoutLogHandler();
        eyes.setLogHandler(logHandler);

        eyes.setBatch(batchInfo);
        eyes.setAgentId("My Custom Agent ID");

        //eyes.setProxy(new ProxySettings("http://localhost:8888"));

        String testName = method.getName();
        eyes.open(TEST_SUITE_NAME, testName);

        eyes.setDebugScreenshotsPrefix("Java_Images_SDK_" + testName + "_");
    }

    @AfterMethod
    public void tearDown() {
        eyes.close();
        eyes.abortIfNotClosed();
    }

    @Test
    public void TestCheckImage() {
        eyes.checkImage("resources/minions-800x500.jpg");
    }

    @Test
    public void TestCheckImage_Fluent() {
        eyes.check("TestCheckImage_Fluent", Target.image("resources/minions-800x500.jpg"));
    }

    @Test
    public void TestCheckImage_WithIgnoreRegion_Fluent() {
        eyes.check("TestCheckImage_WithIgnoreRegion_Fluent", Target.image("resources/minions-800x500.jpg")
                .ignore(new Region(10, 20, 30, 40)));
    }

    @Test
    public void TestCheckImage_Fluent_CutProvider() {
        eyes.setImageCut(new UnscaledFixedCutProvider(200, 100, 100, 50));
        eyes.check("TestCheckImage_Fluent", Target.image("resources/minions-800x500.jpg"));
    }

    @Test
    public void TestExtractText() {
        eyes.setScreenshot(ImageUtils.imageFromFile("resources/extractText.png"));
        List<String> result = eyes.extractText(new OcrRegion());
        Assert.assertEquals(result.size(), 1);
        Assert.assertEquals(result.get(0), "This is the navigation bar");

        Map<String, List<TextRegion>> textRegions = eyes.extractTextRegions(new TextRegionSettings(".+"));
        Assert.assertEquals(textRegions.size(), 1);
        List<TextRegion> regions = textRegions.get(".+");
        Assert.assertEquals(regions.size(), 1);
        TextRegion region = regions.get(0);
        Assert.assertEquals(region, new TextRegion(10, 11, 214, 18, "This is the navigation bar"));
    }
}
