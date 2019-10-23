package com.applitools.eyes.renderingGrid;

import com.applitools.ICheckSettings;
import com.applitools.eyes.BatchInfo;
import com.applitools.eyes.RectangleSize;
import com.applitools.eyes.TestResults;
import com.applitools.eyes.metadata.ActualAppOutput;
import com.applitools.eyes.metadata.Image;
import com.applitools.eyes.metadata.SessionResults;
import com.applitools.eyes.selenium.Eyes;
import com.applitools.eyes.utils.TestUtils;
import com.applitools.eyes.visualgrid.services.VisualGridRunner;
import com.applitools.utils.GeneralUtils;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;

import java.io.IOException;

public class TestIEyesVG2 extends TestIEyesBase {
    private static final BatchInfo batchInfo = new BatchInfo("Top Sites - Visual Grid 2");
    private VisualGridRunner runner;

    public TestIEyesVG2()
    {
        super("visual_grid2");
        runner = new VisualGridRunner(10);
        runner.setServerUrl(SERVER_URL);
        runner.setLogHandler(logHandler);
        runner.setApiKey(API_KEY);
    }

    protected Eyes initEyes(WebDriver webDriver, String testedUrl)
    {
        Eyes eyes = new Eyes(runner);
        eyes.setLogHandler(logHandler);
        logger = eyes.getLogger();

        eyes.setBatch(batchInfo);
        eyes.open(webDriver, "Top Sites", "Top Sites - " + testedUrl, new RectangleSize(800, 600));
        return eyes;
    }

    protected ICheckSettings getCheckSettings()
    {
        return super.getCheckSettings().fully(false);
    }

    void validateResults(Eyes eyes, TestResults results)
    {
        SessionResults sessionResults = null;
        try {
            sessionResults = TestUtils.getSessionResults(eyes.getApiKey(), results);
        } catch (IOException e) {
            GeneralUtils.logExceptionStackTrace(logger, e);
        }
        Assert.assertNotNull(sessionResults);

        ActualAppOutput[] actualAppOutputs = sessionResults.getActualAppOutput();
        Assert.assertNotNull(actualAppOutputs);

        Assert.assertEquals(2, actualAppOutputs.length);

        Image image1 = actualAppOutputs[0].getImage();
        Assert.assertTrue(image1.getHasDom());
        Assert.assertEquals(800, image1.getSize().getWidth());
        Assert.assertEquals(600, image1.getSize().getHeight());

        Image image2 = actualAppOutputs[1].getImage();
        Assert.assertTrue(image2.getHasDom());
    }

}
