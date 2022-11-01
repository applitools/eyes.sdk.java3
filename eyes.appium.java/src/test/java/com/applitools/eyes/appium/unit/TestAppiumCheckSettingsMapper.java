package com.applitools.eyes.appium.unit;

import com.applitools.eyes.appium.AppiumCheckSettings;
import com.applitools.eyes.appium.AppiumCheckSettingsMapper;
import com.applitools.eyes.appium.Target;
import com.applitools.eyes.selenium.universal.dto.CheckSettingsDto;
import com.applitools.eyes.selenium.universal.dto.SelectorRegionDto;
import io.appium.java_client.AppiumBy;
import io.appium.java_client.pagefactory.bys.builder.ByAll;
import io.appium.java_client.pagefactory.bys.builder.ByChained;
import org.openqa.selenium.By;
import org.testng.Assert;
import org.testng.annotations.Test;

public class TestAppiumCheckSettingsMapper {

    @Test
    public void testAppiumByAllMapping() {
        AppiumCheckSettings checkSettings = Target.region(new ByAll(
                new By[]{
                        AppiumBy.id("appiumById"),
                        AppiumBy.xpath("appiumByXpath")
                }
        ));

        CheckSettingsDto dto = AppiumCheckSettingsMapper.toCheckSettingsDto(checkSettings);

        SelectorRegionDto fallback = new SelectorRegionDto();
        fallback.setSelector("appiumByXpath");
        fallback.setType("xpath");
        fallback.setFallback(null);

        SelectorRegionDto region = new SelectorRegionDto();
        region.setSelector("appiumById");
        region.setType("id");
        region.setFallback(fallback);

        SelectorRegionDto actual = (SelectorRegionDto) dto.getRegion();

        Assert.assertNotNull(dto.getRegion());
        Assert.assertEquals(actual.getSelector(), region.getSelector());
        Assert.assertEquals(actual.getType(), region.getType());
        Assert.assertEquals(actual.getFallback().getSelector(), fallback.getSelector());
        Assert.assertEquals(actual.getFallback().getType(), fallback.getType());
        Assert.assertNull(actual.getFallback().getFallback());
    }

    @Test
    public void testAppiumByChainedMapping() {
        AppiumCheckSettings checkSettings = Target.region(new ByChained(
                new By[]{
                        AppiumBy.id("appiumById"),
                        AppiumBy.xpath("appiumByXpath")
                }
        ));

        CheckSettingsDto dto = AppiumCheckSettingsMapper.toCheckSettingsDto(checkSettings);

        SelectorRegionDto child = new SelectorRegionDto();
        child.setSelector("appiumByXpath");
        child.setType("xpath");
        child.setChild(null);

        SelectorRegionDto region = new SelectorRegionDto();
        region.setSelector("appiumById");
        region.setType("id");
        region.setChild(child);

        SelectorRegionDto actual = (SelectorRegionDto) dto.getRegion();

        Assert.assertNotNull(dto.getRegion());
        Assert.assertEquals(actual.getSelector(), region.getSelector());
        Assert.assertEquals(actual.getType(), region.getType());
        Assert.assertEquals(actual.getChild().getSelector(), child.getSelector());
        Assert.assertEquals(actual.getChild().getType(), child.getType());
        Assert.assertNull(actual.getChild().getFallback());
    }
}
