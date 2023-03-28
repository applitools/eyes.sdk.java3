package com.applitools.eyes.unit;

import com.applitools.eyes.DensityMetrics;
import com.applitools.eyes.config.Configuration;
import com.applitools.eyes.playwright.fluent.PlaywrightCheckSettings;
import com.applitools.eyes.playwright.fluent.Target;
import com.applitools.eyes.playwright.universal.mapper.PlaywrightCheckSettingsMapper;
import com.applitools.eyes.universal.dto.CheckSettingsDto;
import com.applitools.eyes.utils.ReportingTestSuite;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class TestPlaywrightCheckSettingsMapper extends ReportingTestSuite {

    @BeforeClass
    public void setup() {
        super.setGroupName("selenium");
        super.setSdkName("java_playwright");
    }

    @Test
    public void testPlaywrightDensityMetrics() {
        PlaywrightCheckSettings checkSettings1 = Target.window().densityMetrics(10, 20);
        PlaywrightCheckSettings checkSettings2 = Target.window().densityMetrics(10, 20, 2.0);

        // Refer here doesn't matter since we are not "refing" any element/selector
        CheckSettingsDto dto1 = PlaywrightCheckSettingsMapper.toCheckSettingsDto(checkSettings1, new Configuration(), null, null);
        CheckSettingsDto dto2 = PlaywrightCheckSettingsMapper.toCheckSettingsDto(checkSettings2, new Configuration(), null, null);

        DensityMetrics densityMetrics1 = dto1.getDensityMetrics();
        DensityMetrics densityMetrics2 = dto2.getDensityMetrics();

        Assert.assertEquals((int) densityMetrics1.getXdpi(), 10);
        Assert.assertEquals((int) densityMetrics1.getYdpi(), 20);
        Assert.assertNull(densityMetrics1.getScaleRatio());

        Assert.assertEquals((int) densityMetrics2.getXdpi(), 10);
        Assert.assertEquals((int) densityMetrics2.getYdpi(), 20);
        Assert.assertEquals(densityMetrics2.getScaleRatio(), 2.0);
    }
}
