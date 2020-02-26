package com.applitools.eyes.images;

import com.applitools.eyes.BatchInfo;
import com.applitools.eyes.EyesException;
import com.applitools.eyes.exceptions.DiffsFoundException;
import com.applitools.utils.GeneralUtils;
import com.applitools.utils.ImageUtils;
import org.testng.Assert;
import org.testng.annotations.Test;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import com.applitools.eyes.RectangleSize;
//import java.lang.*;

public class TestCloseMethod {
    private Eyes eyes;

    @Test
    public void TestClose()
    {
        BatchInfo batch = new BatchInfo("TestClose");
        eyes = new Eyes();
        eyes.setBatch(batch);
        eyes.setViewportSize(RectangleSize.EMPTY);

        eyes.open(this.getClass().getSimpleName(), new Object(){}.getClass().getEnclosingMethod().getName());
        eyes.checkImage(ImageUtils.imageFromFile("resources/gbg1.png"), "TestBitmap1");
        eyes.close(false);

        eyes.open(this.getClass().getSimpleName(), new Object(){}.getClass().getEnclosingMethod().getName());
        eyes.checkImage(ImageUtils.imageFromFile("resources/twitter1d-s4.png"), "TestBitmap1");
        Assert.assertThrows(DiffsFoundException.class, new Assert.ThrowingRunnable() {
            @Override
            public void run() {
                eyes.close(true);
            }
        });
    }
}