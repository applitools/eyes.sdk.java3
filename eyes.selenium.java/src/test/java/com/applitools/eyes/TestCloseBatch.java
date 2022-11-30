package com.applitools.eyes;

import com.applitools.eyes.fluent.BatchClose;
import com.applitools.eyes.fluent.EnabledBatchClose;
import com.applitools.eyes.selenium.Eyes;
import com.applitools.eyes.selenium.fluent.Target;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.util.Arrays;

public class TestCloseBatch {

    private WebDriver driver;
    private Eyes eyes;
    private BatchInfo batch;

    @BeforeTest(alwaysRun = true)
    public void before() {
        String chromeDriverPath = System.getenv("CHROME_DRIVER_PATH");
        if(chromeDriverPath == null) throw new EyesException("CHROME_DRIVER_PATH missing");
        System.setProperty("webdriver.chrome.driver", chromeDriverPath);

        ChromeOptions options = new ChromeOptions().setHeadless(true);
        options.addArguments("--disable-gpu"); // applicable to windows os only
        options.addArguments("--no-sandbox"); // Bypass OS security model
        driver = new ChromeDriver(options);

        batch = new BatchInfo("closeBatch");
    }

    @AfterTest(alwaysRun = true)
    public void teardown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    public void testCloseBatchClassicRunner() {

        eyes = new Eyes();
        eyes.setBatch(batch);

        eyes.open(driver, "test close batch", "test close batch");
        eyes.check(Target.window().fully(false));
        eyes.close(false);

        BatchClose bc = new BatchClose();
        EnabledBatchClose close = bc.setBatchId(Arrays.asList(batch.getId()));
        close.close();
    }
}
