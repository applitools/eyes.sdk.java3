package com.applitools.eyes.images;

import com.applitools.eyes.*;
import com.applitools.utils.ImageUtils;
import org.testng.annotations.Test;
import java.awt.image.BufferedImage;

public class TestEyesImages {
    private Eyes setup(){
        Eyes eyes = new Eyes();
        eyes.setBatch(new BatchInfo("TestEyesImages"));
        return eyes;
    }
    private void teardown(Eyes eyes)
    {
        try
        {
            TestResults results = eyes.close();
            eyes.getLogger().log("Mismatches: " + results.getMismatches());
        }
        finally
        {
            eyes.abort();
        }
    }

    @Test
    public void TestBitmap()
    {
        Eyes eyes = setup();
        eyes.open("TestEyesImages", "CheckImage(Bitmap)");

        eyes.checkImage(ImageUtils.imageFromFile("resources/gbg1.png"), "TestBitmap1");
        eyes.checkImage(ImageUtils.imageFromFile("resources/gbg2.png"), "TestBitmap2");
        teardown(eyes);
    }

    @Test
    public void TestBitmapWithDispose()
    {
        Eyes eyes = setup();
        eyes.open("TestEyesImages", "CheckImage(Bitmap) With Dispose");

        BufferedImage image1 = ImageUtils.imageFromFile("resources/gbg1.png");
        try {
            eyes.checkImage(image1, "TestBitmap1");
        } finally {
            if (image1 != null) image1.flush();
        }

        BufferedImage image2 = ImageUtils.imageFromFile("resources/gbg2.png");
        try {
            eyes.checkImage(image2, "TestBitmap1");
        } finally {
            if (image2 != null) image2.flush();
        }

        teardown(eyes);
    }

    @Test
    public void TestBytes()
    {
        Eyes eyes = setup();
        eyes.open("TestEyesImages", "CheckImage(byte[])", new RectangleSize(1024, 768));

        eyes.checkImage(ImageUtils.encodeAsPng(ImageUtils.imageFromFile("resources/gbg1.png")), "TestBytes1");
        eyes.checkImage(ImageUtils.encodeAsPng(ImageUtils.imageFromFile("resources/gbg2.png")), "TestBytes2");
        teardown(eyes);
    }

    //@Test
    public void TestBase64()
    {
        Eyes eyes = setup();
        eyes.open("TestEyesImages", "CheckImage(base64)", new RectangleSize(1024, 768));

        eyes.checkImage(ImageUtils.base64FromImage(ImageUtils.imageFromFile("resources/gbg1.png")), "TestBase64 1");
        eyes.checkImage(ImageUtils.base64FromImage(ImageUtils.imageFromFile("resources/gbg2.png")), "TestBase64 2");
        teardown(eyes);
    }

    @Test
    public void TestFile()
    {
        Eyes eyes = setup();
        eyes.open("TestEyesImages", "CheckImageFile", new RectangleSize(1024, 768));

        eyes.checkImage("resources/gbg1.png", "TestPath1");
        eyes.checkImage("resources/gbg2.png", "TestPath2");
        teardown(eyes);
    }

    @Test
    public void TestFluent_Path()
    {
        Eyes eyes = setup();
        eyes.open("TestEyesImages", "CheckImage_Fluent", new RectangleSize(1024, 768));
        eyes.check("CheckImage_Fluent", Target.image("resources/gbg1.png"));
        teardown(eyes);
    }
    @Test
    public void TestFluent_Bitmap()
    {
        Eyes eyes = setup();
        eyes.open("TestEyesImages", "CheckImage_Fluent", new RectangleSize(1024, 768));
        eyes.check("CheckImage_Fluent", Target.image(ImageUtils.imageFromFile("resources/gbg1.png")));
        teardown(eyes);
    }
    @Test
    public void TestFluent_WithIgnoreRegion()
    {
        Eyes eyes = setup();
        eyes.open("TestEyesImages", "CheckImage_WithIgnoreRegion_Fluent", new RectangleSize(1024, 768));
        eyes.check("CheckImage_WithIgnoreRegion_Fluent", Target.image("resources/gbg1.png").ignore(new Region(10, 20, 30, 40)));
        teardown(eyes);
    }

    //@Test
    public void TestFluent_WithRegion()
    {
        Eyes eyes = setup();
        eyes.open("TestEyesImages", "CheckImage_WithRegion_Fluent");
        eyes.check("CheckImage_WithRegion_Fluent", Target.image("resources/gbg1.png").content(new Region(10, 20, 30, 40)));
        teardown(eyes);
    }

    @Test
    public void TestLayout2()
    {
        Eyes eyes = setup();
        eyes.setMatchLevel(MatchLevel.LAYOUT);
        eyes.open("CheckLayout2", "Check Layout2", new RectangleSize(1024, 768));
        eyes.checkImage("resources/yahoo1a.png", "resources/yahoo1a.png");
        teardown(eyes);
    }
}