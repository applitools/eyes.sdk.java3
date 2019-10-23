package com.applitools.eyes.selenium;

import com.applitools.eyes.BatchInfo;
import com.applitools.eyes.ProxySettings;
import com.applitools.eyes.RectangleSize;
import com.applitools.eyes.StdoutLogHandler;
import com.applitools.eyes.selenium.fluent.Target;
import com.applitools.eyes.utils.SeleniumUtils;
import com.applitools.eyes.utils.TestUtils;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.Test;

public class TestIgnoreDisplacements {

    @Test
    public void test(){

        // Open a Chrome browser.
        WebDriver driver = SeleniumUtils.createChromeDriver();

        // Initialize the VisualGridEyes SDK and set your private API key.
        Eyes eyes = new Eyes();

        Configuration configuration = eyes.getConfiguration();

        configuration.setProxy(new ProxySettings("http://127.0.0.1", 8888, null, null));

        eyes.setConfiguration(configuration);

        eyes.setLogHandler(new StdoutLogHandler(TestUtils.verboseLogs));
        // Navigate the browser to the "hello world!" web-site.
        driver.get("https://applitools.com/helloworld");

        // Switch sendDom flag on
        BatchInfo batchInfo = new BatchInfo("Ignore Displacements");
        batchInfo.setId("Ignore Displacements");
        eyes.setBatch(batchInfo);

        eyes.open(driver, "Eyes Java SDK", "Ignore Displacements",
                new RectangleSize(1200, 800));

        eyes.setSaveDebugScreenshots(true);

        eyes.check(Target.window().ignoreDisplacements());

        eyes.close(true);
        driver.quit();


    }
}