package com.applitools.eyes.appium.ios;

import com.applitools.eyes.locators.OcrRegion;
import com.applitools.eyes.locators.TextRegion;
import com.applitools.eyes.locators.TextRegionSettings;
import io.appium.java_client.AppiumBy;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.time.Duration;
import java.util.List;
import java.util.Map;

public class IOSExtractTextTest extends IOSTestSetup {

    @Test
    public void testIOSExtractText() {
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));

        eyes.open(driver, getApplicationName(), "Extract text test");

        WebElement showScrollView = driver.findElement(AppiumBy.accessibilityId("Scroll view"));
        List<String> textResult = eyes.extractText(new OcrRegion(showScrollView));
        Assert.assertEquals(textResult.size(), 1);
        Assert.assertEquals(textResult.get(0), "Scroll view");

        Map<String, List<TextRegion>> textRegions = eyes.extractTextRegions(new TextRegionSettings("Scroll view"));
        Assert.assertEquals(textRegions.size(), 1);
        List<TextRegion> regions = textRegions.get("Scroll view");
        Assert.assertEquals(regions.size(), 3);
        TextRegion region = regions.get(0);
        Assert.assertEquals(region, new TextRegion(159, 386, 73, 13, "Scroll view"));
        eyes.close();
    }
}
