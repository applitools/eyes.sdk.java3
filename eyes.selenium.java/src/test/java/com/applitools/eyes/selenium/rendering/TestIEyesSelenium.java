package com.applitools.eyes.selenium.rendering;

import com.applitools.eyes.BatchInfo;
import com.applitools.eyes.RectangleSize;
import com.applitools.eyes.TestResults;
import com.applitools.eyes.logging.Stage;
import com.applitools.eyes.metadata.ActualAppOutput;
import com.applitools.eyes.metadata.Image;
import com.applitools.eyes.metadata.SessionResults;
import com.applitools.eyes.selenium.Eyes;
import com.applitools.eyes.utils.TestUtils;
import com.applitools.utils.GeneralUtils;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;

import java.io.IOException;

public class TestIEyesSelenium extends TestIEyesBase{

    private final BatchInfo batchInfo = new BatchInfo("Top Sites - Selenium");

    public TestIEyesSelenium()
    {
        super("selenium");
    }

    protected Eyes initEyes(WebDriver webDriver, String testedUrl)
    {
        Eyes eyes = new Eyes();
        eyes.setServerUrl(SERVER_URL);
        eyes.setLogHandler(logHandler);
        logger = eyes.getLogger();

        eyes.setBatch(batchInfo);
        eyes.open(webDriver, "Top Sites", "Top Sites - " + testedUrl, new RectangleSize(1024, 768));
        return eyes;
    }

    void validateResults(Eyes eyes, TestResults results)
    {
        SessionResults sessionResults = null;
        try {
            sessionResults = TestUtils.getSessionResults(eyes.getApiKey(), results);
        } catch (IOException e) {
            GeneralUtils.logExceptionStackTrace(logger, Stage.GENERAL, e);
        }

        ActualAppOutput[] actualAppOutputs = sessionResults.getActualAppOutput();
        Assert.assertEquals(2, actualAppOutputs.length);

        Image image1 = actualAppOutputs[0].getImage();
        Assert.assertTrue(image1.getHasDom());
        Assert.assertEquals(1024, image1.getSize().getWidth());
        Assert.assertEquals(768, image1.getSize().getHeight());

        Image image2 = actualAppOutputs[1].getImage();
        Assert.assertTrue(image2.getHasDom());
    }
}
