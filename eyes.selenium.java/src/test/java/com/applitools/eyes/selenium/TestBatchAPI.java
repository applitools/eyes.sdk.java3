package com.applitools.eyes.selenium;

import com.applitools.eyes.*;
import com.applitools.eyes.utils.CommunicationUtils;
import com.applitools.eyes.utils.ReportingTestSuite;
import com.applitools.eyes.utils.SeleniumUtils;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.UUID;

public final class TestBatchAPI extends ReportingTestSuite {

    public TestBatchAPI() {
        super.setGroupName("selenium");
    }

    @Test
    public void testCloseBatch() throws Exception {
        ClassicRunner runner = new ClassicRunner();
        Eyes eyes = new Eyes(runner);
        eyes.setLogHandler(new StdoutLogHandler());
        BatchInfo batchInfo = new BatchInfo("Runner Testing");
        batchInfo.setId(UUID.randomUUID().toString());
        eyes.setBatch(batchInfo);

        WebDriver driver = SeleniumUtils.createChromeDriver();
        try {
            driver.get("https://applitools.com/helloworld");

            eyes.open(driver, "Applitools Eyes Java SDK", "Test Close Batch", new RectangleSize(1200, 800));

            BatchInfo batchBeforeDelete = CommunicationUtils.getBatch(eyes.getLogger(), batchInfo.getId(), eyes.getServerUrl().toString(), eyes.getApiKey());

            Assert.assertFalse(batchBeforeDelete.isCompleted());

            eyes.closeAsync();
        } finally {
            driver.quit();
            eyes.abortAsync();
            runner.getAllTestResults(false);
            eyes.getServerConnector().closeBatch(batchInfo.getId());
        }

        // Getting 200 for the request is enough for the test
        CommunicationUtils.getBatch(eyes.getLogger(), batchInfo.getId(), eyes.getServerUrl().toString(), eyes.getApiKey());
    }
}
