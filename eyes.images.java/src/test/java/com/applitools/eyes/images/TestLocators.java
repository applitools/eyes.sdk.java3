package com.applitools.eyes.images;

import com.applitools.eyes.CoordinatesType;
import com.applitools.eyes.Region;
import com.applitools.eyes.images.utils.TestSetup;
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

    @Test
    public void testLocate() {
        eyes.open(getApplicationName(), "Locate");
        BufferedImage image = ImageUtils.imageFromFile(EXTRACT_TEST_IMAGE);

        Map<String, List<Region>> res = eyes.locate(new VisualLocatorSettings().name("the").image(image));
        List<Region> regions = res.get("the");
        Assert.assertEquals(regions.size(), 1);
        Region region = regions.get(0);
        Assert.assertEquals(region, new Region(69, 8, 31, 22, CoordinatesType.SCREENSHOT_AS_IS));
        eyes.close();
    }

    @Test
    public void testLocateText() {
        eyes.open(getApplicationName(), "LocateText");
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
        eyes.open(getApplicationName(), "ExtractText");
        BufferedImage image = ImageUtils.imageFromFile(EXTRACT_TEST_IMAGE);
        List<String> result = eyes.extractText(new OcrRegion(image));
        Assert.assertEquals(result.size(), 1);
        Assert.assertEquals(result.get(0), "This is the navigation bar");
        eyes.close();
    }
}
