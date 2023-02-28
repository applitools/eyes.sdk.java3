package com.applitools.eyes.images.unit;

import com.applitools.ICheckSettings;
import com.applitools.eyes.DensityMetrics;
import com.applitools.eyes.config.Configuration;
import com.applitools.eyes.images.Target;
import com.applitools.eyes.images.universal.mapper.ImagesCheckSettingsMapper;
import com.applitools.eyes.universal.dto.CheckSettingsDto;
import com.applitools.eyes.utils.ReportingTestSuite;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class TestImagesCheckSettingsMapper extends ReportingTestSuite {

    @BeforeClass
    public void setup() {
        super.setGroupName("images");
    }

    @Test
    public void testImagesDensityMetrics() {
        ICheckSettings checkSettings1 = Target.image("").densityMetrics(10, 20);
        ICheckSettings checkSettings2 = Target.image("").densityMetrics(10, 20, 2.0);

        CheckSettingsDto dto1 = ImagesCheckSettingsMapper.toCheckSettingsDto(checkSettings1, new Configuration());
        CheckSettingsDto dto2 = ImagesCheckSettingsMapper.toCheckSettingsDto(checkSettings2, new Configuration());

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
