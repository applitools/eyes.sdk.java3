package com.applitools.eyes.appium;

import com.applitools.eyes.appium.android.AndroidTestSetup;
import com.applitools.eyes.selenium.Configuration;
import com.applitools.eyes.visualgrid.model.DeviceName;
import io.appium.java_client.AppiumBy;
import io.appium.java_client.Setting;
import io.appium.java_client.TouchAction;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.remote.AndroidMobileCapabilityType;
import io.appium.java_client.touch.offset.PointOption;
import org.openqa.selenium.interactions.PointerInput;
import org.openqa.selenium.interactions.Sequence;
import org.testng.annotations.Test;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;
import java.util.Arrays;

public class TestSuncorp extends AndroidTestSetup {

    private final int TIMEOUT = 3000;


    @Test
    public void testPolicyDetails() throws InterruptedException {
        login();
        driver.findElement(AppiumBy.id("card_view")).click();
        Thread.sleep(TIMEOUT * 2);

        eyes.open(driver, getApplicationName(), "Policy details");
        eyes.check(Target.window().fully().scrollRootElement("policyDetailsScrollView")
                .withName("Nested fully"));
        eyes.close();
    }

    @Test
    public void testInsurances() throws InterruptedException {
        login();

//        eyes.setScaleRatio(123.3);
        Configuration config = eyes.getConfiguration();
        config.setScaleRatio(123.3);

        eyes.open(driver, getApplicationName(), "Insurance");
        eyes.check(Target.window()
                .scrollRootElement(AppiumBy.id("insuranceTabNestedScrollView"))
                .fully(true).withName("Android Insurance Screen on SunCorp App"));
        eyes.close();
    }

    private void scrollTo(int x0, int y0, int x1, int y1) {
        PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
        Sequence sequence = new Sequence(finger, 1);

        // move to (x0, y0)
        sequence.addAction(finger.createPointerMove(Duration.ofMillis(0),
                PointerInput.Origin.viewport(), x0, y0));
        // pointer down
        sequence.addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));
        // move to (x1, y1)
        sequence.addAction(finger.createPointerMove(Duration.ofMillis(700),
                PointerInput.Origin.viewport(), x1, y1));
        // release
        sequence.addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));

        driver.perform(Arrays.asList(sequence));
    }

    private void login() throws InterruptedException {
        Thread.sleep(TIMEOUT * 2);

        driver.findElement(AppiumBy.id("loginButton")).click();
        Thread.sleep(TIMEOUT);

        driver.findElement(AppiumBy.id("passwordField")).sendKeys("a");
        driver.findElement(AppiumBy.id("loginButton")).click();
        Thread.sleep(TIMEOUT * 5);
    }

    @Override
    protected void initDriver() throws MalformedURLException {
        driver = new AndroidDriver(new URL("http://0.0.0.0:4723/wd/hub"), capabilities);
        driver.setSetting(Setting.ALLOW_INVISIBLE_ELEMENTS, true);
    }

    @Override
    protected void setAppCapability() {
        capabilities.setCapability(AndroidMobileCapabilityType.APP_ACTIVITY,"au.com.suncorp.marketplace.base.presentation.startup.view.SplashActivity" );
        capabilities.setCapability(AndroidMobileCapabilityType.APP_PACKAGE, "au.com.aami.marketplace.qa");
        capabilities.setCapability("noReset", true);
        capabilities.setCapability("fullReset", false);
//        capabilities.setCapability(AndroidMobileCapabilityType.DONT_STOP_APP_ON_RESET,true);
        capabilities.setCapability("appium:settings[allowInvisibleElements]", true);

//        capabilities.setCapability("appium:app", "/Users/idos/Downloads/app-androidx-debug_v4.16.4.apk");
    }
}
