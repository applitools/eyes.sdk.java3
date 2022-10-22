package com.applitools.eyes.images;

import com.applitools.eyes.locators.TextRegion;
import com.applitools.eyes.locators.TextRegionSettings;
import com.applitools.eyes.locators.VisualLocatorSettings;
import com.applitools.utils.ImageUtils;
import org.testng.Assert;
import org.testng.annotations.*;

import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Map;

public class TestLocators extends TestSetup {

    private final String EXTRACT_TEST_IMAGE = "src/main/resources/extractText.png";

    @Test(timeOut = 60000)
    public void testLocate() {
        BufferedImage image = ImageUtils.imageFromFile(EXTRACT_TEST_IMAGE);

        eyes.setAppName(getApplicationName());
        eyes.locate(new VisualLocatorSettings().name("test").image(image));
    }

    @Test
    public void testLocateText() {
        eyes.open(getApplicationName(), "TestLocators");
        BufferedImage image = ImageUtils.imageFromFile(EXTRACT_TEST_IMAGE);

        Map<String, List<TextRegion>> textRegions = eyes.extractTextRegions(new TextRegionSettings(".+").image(image));
        Assert.assertEquals(textRegions.size(), 1);
        List<TextRegion> regions = textRegions.get(".+");
        Assert.assertEquals(regions.size(), 1);
        TextRegion region = regions.get(0);
        Assert.assertEquals(region, new TextRegion(10, 11, 214, 18, "This is the navigation bar"));
        eyes.close();
    }

    @Test
    public void testExtractText() {
        eyes.open(getApplicationName(), "TestLocators");
        BufferedImage image = ImageUtils.imageFromFile(EXTRACT_TEST_IMAGE);
        List<String> result = eyes.extractText(new OcrRegion(image));
        Assert.assertEquals(result.size(), 1);
        Assert.assertEquals(result.get(0), "This is the navigation bar");
        eyes.close();
    }
}
