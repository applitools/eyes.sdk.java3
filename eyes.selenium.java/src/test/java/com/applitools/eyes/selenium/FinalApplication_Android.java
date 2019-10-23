package com.applitools.eyes.selenium;

import com.applitools.eyes.FixedCutProvider;
import com.applitools.eyes.StdoutLogHandler;
import com.applitools.eyes.utils.TestUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.awt.*;
import java.net.MalformedURLException;
import java.net.URL;

import static com.applitools.eyes.selenium.TestDataProvider.*;

/**
 *
 * @author mohamedabdulkadar.m
 */
public class FinalApplication_Android {
    static WebDriver driver;
    static WebElement wb;
    static long start = System.currentTimeMillis();
    static long stop;
    final static int URL_BAR_SIZE = 77;
    final static int NAVIGATION_BAR_SIZE = 48;

    static public void main(String[] args) throws MalformedURLException, InterruptedException, AWTException {

        // Android Emulator Capabilities
//        DesiredCapabilities caps = DesiredCapabilities.android();
//        caps.setCapability("appiumVersion", "1.5.3");
//        caps.setCapability("deviceName", "Android Emulator");
//        caps.setCapability("deviceOrientation", "portrait");
//        caps.setCapability("browserName", "Browser");
//        caps.setCapability("platformVersion", "5.0");
//        caps.setCapability("platformName", "Android");
//
        // LG Capabilities
        DesiredCapabilities caps = DesiredCapabilities.android();
        caps.setCapability("appiumVersion", "1.5.3");
        caps.setCapability("deviceName", "LG Nexus 4 Emulator");
        caps.setCapability("deviceOrientation", "portrait");
        caps.setCapability("browserName", "Browser");
        caps.setCapability("platformVersion", "4.4");
        caps.setCapability("platformName", "Android");
        caps.setCapability("username", SAUCE_USERNAME);
        caps.setCapability("accessKey", SAUCE_ACCESS_KEY);

        driver = new RemoteWebDriver(new URL(SAUCE_SELENIUM_URL), caps);
        System.out.println("caps finished");
        function();
    }
    public static void function() throws InterruptedException, AWTException {
        Eyes eyes = new Eyes();
        eyes.setSaveNewTests(false);
        eyes.setForceFullPageScreenshot(true);
        eyes.setImageCut(new FixedCutProvider(URL_BAR_SIZE, NAVIGATION_BAR_SIZE, 0, 0));
        eyes.setLogHandler(new StdoutLogHandler(TestUtils.verboseLogs));

        driver = eyes.open(driver, "sample2", "titleicon5");
        try {
            driver.get("http://atom:mota@lgi-www-sat.trimm.net/test/upc/title-with-icon.html");
//            Thread.sleep(5000);
            eyes.checkWindow("test2");
            WebElement element = driver.findElement(By.xpath("html/body/div[2]/h1[5]"));
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", element);
            Thread.sleep(500);
            eyes.checkRegion(driver.findElement(By.xpath("html/body/div[2]/h1[5]")), true);
            System.out.println("this is the element location: " + element.getLocation().toString());
            System.out.println("this is the element Height: " + element.getSize().getHeight());
            System.out.println("this is the element Width: " + element.getSize().getWidth());
//            VisualGridEyes.setBaselineName("Images");
            eyes.close();
        } finally {
            driver.quit();
            eyes.abortIfNotClosed();
            stop = System.currentTimeMillis();
            System.out.println("TIME TAKEN IN SECONDS =" + ((stop - start) / 1000));
        }
    }
}