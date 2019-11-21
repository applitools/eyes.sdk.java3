package com.applitools.eyes.selenium;

import com.applitools.eyes.BatchInfo;
import com.applitools.eyes.EyesRunner;
import com.applitools.eyes.FileLogger;
import com.applitools.eyes.RectangleSize;
import com.applitools.eyes.utils.CommUtils;
import com.applitools.eyes.utils.SeleniumUtils;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.annotations.Test;

public final class TestBatchAPI {
    @Test
    public void testCloseBatch() {
        ClassicRunner classicRunner = new ClassicRunner();

        eyesTest(classicRunner);

    }

    private void eyesTest(EyesRunner runner) {

        WebDriver driver = SeleniumUtils.createChromeDriver();

        // Initialize the VisualGridEyes SDK and set your private API key.
        Eyes eyes;
        BatchInfo batchInfo;
        try {
            eyes = new Eyes(runner);
            eyes.setLogHandler(new FileLogger("runnerTest.log", false, true));
//        eyes.setLogHandler(new StdoutLogHandler(true));
            eyes.setServerUrl("https://eyesfabric4eyes.applitools.com/");
//        eyes.setProxy(new ProxySettings("http://127.0.0.1:8888"));

            // Switch sendDom flag on
            eyes.setSendDom(true);
            eyes.setStitchMode(StitchMode.CSS);
            batchInfo = new BatchInfo("Runner Testing");
            eyes.setBatch(batchInfo);
            eyes.setApiKey("CAE7aS103TDz7XyegELya3tHpEIXTFi0gBBwvgq104PSHIU110");
            // Navigate the browser to the "hello world!" web-site.
//        driver.get("https://applitools.com/helloworld");

            eyes.open(driver, "Applitools Eyes Java SDK", "Classic Runner Test",
                    new RectangleSize(1200, 800));

            BatchInfo batchBeforeDelete = CommUtils.getBatch(batchInfo.getId(), eyes.getServerUrl().toString(), eyes.getApiKey());

            Assert.assertFalse(batchBeforeDelete.isCompleted());

            eyes.closeAsync();
        } finally {
            //noinspection ConstantConditions
            if (driver != null) {
                driver.quit();
            }
            runner.getAllTestResults(false);
        }
        BatchInfo batch = CommUtils.getBatch(batchInfo.getId(), eyes.getServerUrl().toString(), eyes.getApiKey());
        Assert.assertTrue(batch.isCompleted());
    }
}