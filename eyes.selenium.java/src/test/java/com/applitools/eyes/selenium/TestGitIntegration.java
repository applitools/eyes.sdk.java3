package com.applitools.eyes.selenium;

import com.applitools.connectivity.MockServerConnector;
import com.applitools.connectivity.ServerConnector;
import com.applitools.eyes.*;
import com.applitools.eyes.utils.SeleniumUtils;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.concurrent.atomic.AtomicReference;

public class TestGitIntegration {

    @Test
    public void testGitIntegration() {
        final AtomicReference<String> reference = new AtomicReference<>();
        ServerConnector serverConnector = new MockServerConnector() {
            @Override
            public void startSession(final TaskListener<RunningSession> listener, SessionStartInfo sessionStartInfo) {
                reference.set(sessionStartInfo.getParentBranchBaselineSavedBefore());
                super.startSession(listener, sessionStartInfo);
            }

            @Override
            public BatchConfig getBatchConfig(String batchId) {
                BatchConfig config = new BatchConfig();
                config.setScmSourceBranch("develop");
                config.setScmTargetBranch("master");
                return config;
            }
        };

        BatchInfo batchInfo = new BatchInfo("batch");
        batchInfo.setId("id");
        Eyes eyes = new Eyes();
        eyes.setServerConnector(serverConnector);
        eyes.setLogHandler(new StdoutLogHandler());
        eyes.setBatch(batchInfo);

        // Getting branches names from the server to calculate merge base time
        WebDriver driver = SeleniumUtils.createChromeDriver();
        try {
            eyes.open(driver, "app", "test");
            eyes.checkWindow();
            eyes.close();
        } finally {
            driver.quit();
            eyes.abortIfNotClosed();
        }

        Assert.assertNotNull(reference.get());

        // Not sending a request because but id is randomly generated
        batchInfo = new BatchInfo("batch");
        eyes.setBatch(batchInfo);
        driver = SeleniumUtils.createChromeDriver();
        try {
            eyes.open(driver, "app", "test");
            eyes.checkWindow();
            eyes.close();
        } finally {
            driver.quit();
            eyes.abortIfNotClosed();
        }

        Assert.assertNull(reference.get());

        // Not sending a request but using user defined branches names to calculate merge base time
        eyes.setBranchName("develop");
        eyes.setParentBranchName("master");
        driver = SeleniumUtils.createChromeDriver();
        try {
            eyes.open(driver, "app", "test");
            eyes.checkWindow();
            eyes.close();
        } finally {
            driver.quit();
            eyes.abortIfNotClosed();
        }

        Assert.assertNotNull(reference.get());
    }
}
