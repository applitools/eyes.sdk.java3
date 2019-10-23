package com.applitools.eyes.selenium;

import com.applitools.eyes.TestResults;
import com.applitools.eyes.utils.SeleniumUtils;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.annotations.*;

public class TestSeleniumEyesWithNullRunner {
    private String testSuitName = "Eyes Selenium SDK - Null Runner";
    private String testedPageUrl = "https://applitools.github.io/demo/TestPages/FramesTestPage/";
    private Eyes eyes;
    private ChromeDriver driver;
    private TestResults close;

    @BeforeClass
    public void beforeClass(){
        eyes = new Eyes(null);
        eyes.setBatch(TestDataProvider.batchInfo);
        driver = SeleniumUtils.createChromeDriver();
        driver.get(testedPageUrl);
    }
    @BeforeMethod
    public void beforeMethod(){
        eyes.open(driver, testSuitName, "TestSeleniumEyesWithNullRunner");
        eyes.checkWindow();
    }

    @Test
    public void testCloseTrue(){
        close = eyes.close(true);
    }

    @Test
    public void testCloseFalse(){
        close = eyes.close(false);
    }

    @Test
    public void testClose(){
        close = eyes.close();
    }

    @AfterMethod
    public void afterMethod(){
        System.out.println(close);
    }

    @AfterClass
    public void afterClass(){
        driver.quit();
    }
}
