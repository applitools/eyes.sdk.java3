package com.applitools.eyes;

import org.junit.Assert;
import org.testng.annotations.Test;

import java.awt.*;

public class TestRegion {

    @Test
    public void TestRegionSplitting_iPhone_XS_12_2_Portrait()
    {
        Logger logger = new Logger();
        Region region = new Region(0, 0, 375, 2255);
        SubregionForStitching[] regions = region.getSubRegions(new RectangleSize(375, 635), 10, 3, new Rectangle(0, 0, 1125, 1905), logger);
        SubregionForStitching[] expectedRegions = new SubregionForStitching[] {
                new SubregionForStitching(new Point(0, 0), new Point(0, 0), new Rectangle(0, 0, 1125, 1905), new Rectangle(0, 0, 375, 635)),
                new SubregionForStitching(new Point(0, 615), new Point(0, 625), new Rectangle(0, 0, 1125, 1905), new Rectangle(0, 10, 375, 625)),
                new SubregionForStitching(new Point(0, 1230), new Point(0, 1240), new Rectangle(0, 0, 1125, 1905), new Rectangle(0, 10, 375, 625)),
                new SubregionForStitching(new Point(0, 1620), new Point(0, 1825), new Rectangle(0, 585, 1125, 1320), new Rectangle(0, 10, 375, 430))
        };
        Assert.assertArrayEquals(expectedRegions, regions);
        // ScrollTo: { X = 0,Y = 0}; PasteLocation: { X = 0,Y = 0}; PhysicalCropArea: { X = 0,Y = 0,Width = 1125,Height = 1905}; LogicalCropArea { X = 0,Y = 0,Width = 375,Height = 635}
        // ScrollTo: { X = 0,Y = 615}; PasteLocation: { X = 0,Y = 625}; PhysicalCropArea: { X = 0,Y = 0,Width = 1125,Height = 1905}; LogicalCropArea { X = 0,Y = 10,Width = 375,Height = 625}
        // ScrollTo: { X = 0,Y = 1230}; PasteLocation: { X = 0,Y = 1240}; PhysicalCropArea: { X = 0,Y = 0,Width = 1125,Height = 1905}; LogicalCropArea { X = 0,Y = 10,Width = 375,Height = 625}
        // ScrollTo: { X = 0,Y = 1620}; PasteLocation: { X = 0,Y = 1827}; PhysicalCropArea: { X = 0,Y = 585,Width = 1125,Height = 1320}; LogicalCropArea { X = 0,Y = 10,Width = 375,Height = 430}
    }

    @Test
    public void TestRegionSplitting_iPhone_XR_12_2_Portrait()
    {
        Logger logger = new Logger();
        Region region = new Region(0, 0, 414, 2169);
        SubregionForStitching[] regions = region.getSubRegions(new RectangleSize(414, 719), 10, 2, new Rectangle(0, 0, 828, 1438), logger);
        SubregionForStitching[] expectedRegions = new SubregionForStitching[] {
                new SubregionForStitching(new Point(0, 0), new Point(0, 0), new Rectangle(0, 0, 828, 1438), new Rectangle(0, 0, 414, 719)),
                new SubregionForStitching(new Point(0, 699), new Point(0, 709), new Rectangle(0, 0, 828, 1438), new Rectangle(0, 10, 414, 709)),
                new SubregionForStitching(new Point(0, 1398), new Point(0, 1408), new Rectangle(0, 0, 828, 1438), new Rectangle(0, 10, 414, 709)),
                new SubregionForStitching(new Point(0, 1450), new Point(0, 2077), new Rectangle(0, 1234, 828, 204), new Rectangle(0, 10, 414, 92))
        };
        Assert.assertArrayEquals(expectedRegions, regions);
    }
}
