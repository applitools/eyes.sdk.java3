package com.applitools.eyes.appium.ios;

import com.applitools.eyes.locators.OcrRegion;
import com.applitools.eyes.locators.TextRegion;
import com.applitools.eyes.locators.TextRegionSettings;
import io.appium.java_client.MobileBy;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class IOSExtractTextTest extends IOSTestSetup {

    @Test
    public void testIOSExtractText() {
        driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);

        eyes.open(driver, getApplicationName(), "Extract text test");

        WebElement showScrollView = driver.findElement(MobileBy.AccessibilityId("Scroll view"));
        List<String> textResult = eyes.extractText(new OcrRegion(showScrollView));
        Assert.assertEquals(textResult.size(), 1);
        Assert.assertEquals(textResult.get(0), "Scroll view");

        Map<String, List<TextRegion>> textRegions = eyes.extractTextRegions(new TextRegionSettings("Scroll view"));
        Assert.assertEquals(textRegions.size(), 1);
        List<TextRegion> regions = textRegions.get("Scroll view");
        Assert.assertEquals(regions.size(), 1);
        TextRegion region = regions.get(0);
        Assert.assertEquals(region, new TextRegion(151, 388, 73, 12, "Scroll view"));
        eyes.close();
    }
}
