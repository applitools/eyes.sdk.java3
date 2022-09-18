package com.applitools.eyes.appium;

import com.applitools.eyes.Padding;
import com.applitools.eyes.Region;
import com.applitools.eyes.TestResults;
import com.applitools.eyes.metadata.SessionResults;
import com.applitools.eyes.utils.TestUtils;
import io.appium.java_client.MobileBy;
import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.net.MalformedURLException;
import java.net.URL;

public class TestCodedRegionPadding {

    public static final String USERNAME = "applitools-dev";
    public static final String ACCESS_KEY = "7f853c17-24c9-4d8f-a679-9cfde5b43951";
    public static final String URL = "https://"+USERNAME+":" + ACCESS_KEY + "@ondemand.saucelabs.com:443/wd/hub";

    private Eyes eyes;
    private AndroidDriver driver;

    @BeforeMethod
    public void setup() throws MalformedURLException {
        eyes = new Eyes();
        eyes.setApiKey(System.getenv("APPLITOOLS_API_KEY"));
        eyes.setForceFullPageScreenshot(false);
        eyes.setMatchTimeout(0);
        eyes.setSaveNewTests(false);

        DesiredCapabilities capabilities = new DesiredCapabilities();
        capabilities.setCapability("deviceName","Google Pixel 5 GoogleAPI Emulator");
        capabilities.setCapability("deviceOrientation", "portrait");
        capabilities.setCapability("platformVersion","11.0");
        capabilities.setCapability("platformName", "Android");
        capabilities.setCapability("automationName", "UiAutomator2");
        capabilities.setCapability("app", "https://applitools.jfrog.io/artifactory/Examples/androidx/helper_lib/1.8.5/app-androidx-debug.apk");
        capabilities.setCapability("newCommandTimeout", 2000);

        driver = new AndroidDriver<>(new URL(URL), capabilities);
    }

    @AfterMethod
    public void teardown() {
        driver.quit();

        eyes.abortIfNotClosed();
    }

    @Test
    public void testRegionPadding() throws InterruptedException {
        eyes.open(driver, "Java Appium - Android", "Test Regions Padding");

        driver.findElementById("btn_two_fragments_activity").click();
        Thread.sleep(1000);
        WebElement item1 = driver.findElement(MobileBy.id("id_2"));
        WebElement item2 = driver.findElement(MobileBy.id("id_4"));
        WebElement item3 = driver.findElement(MobileBy.id("id_6"));
        WebElement item4 = driver.findElement(MobileBy.id("id_8"));

        eyes.check(Target.window()
                .ignore(item1, new Padding().setBottom(20))
                .layout(item2, new Padding(20))
                .content(item3, new Padding().setLeft(10).setRight(10))
                .strict(item4, new Padding().setBottom(40))
                .fully(false));
        final TestResults result = eyes.close(false);
        final SessionResults info = getTestInfo(result);

        Region ignoreRegion = info.getActualAppOutput()[0].getImageMatchSettings().getIgnore()[0];
        Region layoutRegion = info.getActualAppOutput()[0].getImageMatchSettings().getLayout()[0];
        Region contentRegion = info.getActualAppOutput()[0].getImageMatchSettings().getContent()[0];
        Region strictRegion = info.getActualAppOutput()[0].getImageMatchSettings().getStrict()[0];

        Assert.assertEquals(ignoreRegion, new Region(16, 68, 361, 52), "ignore");
        Assert.assertEquals(layoutRegion, new Region(0, 151, 401, 72), "layout");
        Assert.assertEquals(contentRegion, new Region(6, 274, 381, 32), "content");
        Assert.assertEquals(strictRegion, new Region(16, 377, 361, 72), "strict");
    }

    private SessionResults getTestInfo(TestResults results) {
        SessionResults sessionResults = null;
        try {
            sessionResults = TestUtils.getSessionResults(eyes.getApiKey(), results);
        } catch (Throwable e) {
            e.printStackTrace();
            Assert.fail("Exception appeared while getting session results");
        }
        return sessionResults;
    }
}
