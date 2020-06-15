package com.applitools.eyes.selenium;

import com.applitools.eyes.BatchInfo;
import com.applitools.eyes.EyesRunner;
import com.applitools.eyes.FileLogger;
import com.applitools.eyes.RectangleSize;
import com.applitools.eyes.utils.CommunicationUtils;
import com.applitools.eyes.utils.ReportingTestSuite;
import com.applitools.eyes.utils.SeleniumUtils;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.annotations.Test;

public final class TestBatchAPI extends ReportingTestSuite {

    public TestBatchAPI() {
        super.setGroupName("selenium");
    }

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
//        eyes.setProxy(new ProxySettings("http://127.0.0.1:8888"));

        // Switch sendDom flag on
        eyes.setSendDom(true);
        eyes.setStitchMode(StitchMode.CSS);
        batchInfo = new BatchInfo("Runner Testing");
        eyes.setBatch(batchInfo);
        // Navigate the browser to the "hello world!" web-site.
//        eyes.setProxy(new ProxySettings("http://127.0.0.1:8888"));

//        driver.get("https://applitools.com/helloworld");

            eyes.open(driver, "Applitools Eyes Java SDK", "Classic Runner Test",
                    new RectangleSize(1200, 800));

            BatchInfo batchBeforeDelete = CommunicationUtils.getBatch(batchInfo.getId(), eyes.getServerUrl().toString(), eyes.getApiKey());

            Assert.assertFalse(batchBeforeDelete.isCompleted());

            eyes.closeAsync();
        } finally {
            //noinspection ConstantConditions
            if (driver != null) {
                driver.quit();
            }
            runner.getAllTestResults(false);
        }
        BatchInfo batch = CommunicationUtils.getBatch(batchInfo.getId(), eyes.getServerUrl().toString(), eyes.getApiKey());
        Assert.assertTrue(batch.isCompleted());
    }
}