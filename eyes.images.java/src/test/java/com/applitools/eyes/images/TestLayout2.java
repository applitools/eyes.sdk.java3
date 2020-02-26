package com.applitools.eyes.images;

import com.applitools.eyes.*;
import org.testng.annotations.Test;

public class TestLayout2 {

    private final String AppName = "Layout2";
    private boolean baseline;

    private Eyes Setup()
    {
        baseline = false;

        Eyes eyes = new Eyes();

        //eyes.BaselineEnvName = "Layout2";
        eyes.setBatch(new BatchInfo("Layout2"));
        eyes.setLogHandler(new StdoutLogHandler());
        eyes.setMatchLevel(MatchLevel.LAYOUT);
        if (baseline)
        {
            eyes.setSaveNewTests(true);
            eyes.setSaveDiffs(true);
        }

        eyes.getLogger().log("running test: " + new Object(){}.getClass().getEnclosingMethod().getName());

        return eyes;
    }

    private void tearDown(Eyes eyes)
    {
        eyes.abort();
    }

    @Test
    public void TestAol1ab()
    {
        Eyes eyes = Setup();
        eyes.setHostOS("Windows 6.1");
        eyes.setHostApp("Firefox");

        eyes.open(AppName, "AOL dynamic content", new RectangleSize(1024, 768));

        check(eyes, "resources/aol1a.png", "resources/aol1b.png");
        tearDown(eyes);
    }

    private void check(Eyes eyes, String baselineImage, String actualImage)
    {
        if (baseline)
        {
            eyes.checkImage(baselineImage);
        }
        else
        {
            eyes.checkImage(actualImage);
        }

        TestResults results = eyes.close();
    }
}