package com.applitools.eyes.selenium;

import com.applitools.eyes.BatchInfo;
import com.applitools.eyes.RectangleSize;
import com.applitools.eyes.StdoutLogHandler;
import com.applitools.eyes.selenium.fluent.Target;
import com.applitools.eyes.utils.SeleniumUtils;
import com.applitools.eyes.utils.TestUtils;
import org.openqa.selenium.WebDriver;

public final class TestDomCapture {
    public static void main(String[] args) throws Exception {

        // Open a Chrome browser.
        WebDriver driver = SeleniumUtils.createChromeDriver();

        // Initialize the VisualGridEyes SDK and set your private API key.
        Eyes eyes = new Eyes();

        eyes.setLogHandler(new StdoutLogHandler(TestUtils.verboseLogs));
        eyes.setServerUrl("https://eyes.applitools.com/");
//        eyes.setProxy(new ProxySettings("http://127.0.0.1:8888"));

        // Switch sendDom flag on
        eyes.setSendDom(true);
        eyes.setStitchMode(StitchMode.CSS);
        BatchInfo batchInfo = new BatchInfo("TTS - config batch");
        batchInfo.setId("RCA_Batch_ID");
        eyes.setBatch(batchInfo);
//        try {

        eyes.open(driver, "DOM Capture with config Test", "DOM Capture V2 - usa today",
                new RectangleSize(1200, 800));

        // Navigate the browser to the "hello world!" web-site.
//        driver.get("https://www.usatoday.com");
        driver.get("http://applitools.github.io/demo/TestPages/VisualGridTestPage/index.html");
//        driver.get("https://nikita-andreev.github.io/applitools/dom_capture.html?aaa");
//        Thread.sleep(5000);

//        WebElement element = driver.findElement(By.className("video-container"));
//        eyes.checkElement(element, "Test DOM diffs");

        eyes.check(Target.window().fully());

        eyes.check(Target.frame("iframe").fully());

        driver.quit();
        eyes.close(true);


    }
}