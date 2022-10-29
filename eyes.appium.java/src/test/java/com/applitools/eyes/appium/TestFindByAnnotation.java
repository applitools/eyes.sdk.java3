package com.applitools.eyes.appium;

import com.applitools.eyes.StdoutLogHandler;
import com.applitools.utils.GeneralUtils;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.pagefactory.*;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebElement;
import org.openqa.selenium.support.PageFactory;
import org.testng.annotations.Test;

import java.net.MalformedURLException;
import java.net.URL;

public class TestFindByAnnotation {

    final static String USERNAME = GeneralUtils.getEnvString("SAUCE_USERNAME");
    final static String ACCESS_KEY = GeneralUtils.getEnvString("SAUCE_ACCESS_KEY");
    final static String SL_URL = "https://"+USERNAME+":" + ACCESS_KEY + "@ondemand.us-west-1.saucelabs.com:443/wd/hub";

    @Test
    public void testFindByAnnotation() throws MalformedURLException {
        DesiredCapabilities caps = new DesiredCapabilities();
        caps.setCapability("deviceName","Google Pixel 5 GoogleAPI Emulator");
        caps.setCapability("deviceOrientation", "portrait");
        caps.setCapability("platformVersion","11.0");
        caps.setCapability("platformName", "Android");
        caps.setCapability("automationName", "UiAutomator2");
        caps.setCapability("app", "/Users/idos/AndroidStudioProjects/MyApplication2/app/build/outputs/apk/debug/app-debug.apk");
        caps.setCapability("newCommandTimeout", 2000);

        AndroidDriver driver = new AndroidDriver(new URL("http://0.0.0.0:4723/wd/hub"), caps);

        Eyes eyes = new Eyes();
        eyes.setLogHandler(new StdoutLogHandler(true));
        try {
            eyes.open(driver,"Android Test","AndroidFindBy test");

            Screen screen = new Screen(driver);

            WebElement el = screen.getBtn();

            eyes.check(Target.region(el).fully().timeout(0));
            eyes.close();
        } finally {
            driver.quit();
            eyes.abortIfNotClosed();
        }
    }
}

class Screen {
    public Screen(AndroidDriver driver) {
        PageFactory.initElements(new AppiumFieldDecorator(driver), this);
    }

    @HowToUseLocators(androidAutomation = LocatorGroupStrategy.ALL_POSSIBLE)
    @AndroidFindBy(priority = 1, uiAutomator = "new UiSelector().descriptionContains(\"bla\")")
    @AndroidFindBy(priority = 2, accessibility = "button bla")
    @AndroidFindBy(priority = 3, id = "com.example.myapplication:id/button")
    private WebElement btn;

    public WebElement getBtn() {
        return btn;
    }
}

