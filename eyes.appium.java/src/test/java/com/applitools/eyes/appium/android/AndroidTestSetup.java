package com.applitools.eyes.appium.android;

import com.applitools.eyes.appium.TestSetup;
import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.MutableCapabilities;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.net.MalformedURLException;
import java.net.URL;

public abstract class AndroidTestSetup extends TestSetup {

    @Override
    public void setCapabilities() {
        capabilities = new MutableCapabilities();
        capabilities.setCapability("platformName", "Android");
        capabilities.setCapability("appium:app", "https://applitools.jfrog.io/artifactory/Examples/androidx/helper_lib/1.8.6/app-androidx-debug.apk");
        capabilities.setCapability("appium:deviceName", "Google Pixel 5 GoogleAPI Emulator");
        capabilities.setCapability("appium:platformVersion", "11.0");
        capabilities.setCapability("appium:automationName", "UiAutomator2");
        MutableCapabilities sauceOptions = new MutableCapabilities();
        sauceOptions.setCapability("appiumVersion", "1.22.1");

        capabilities.setCapability("sauce:options", sauceOptions);
    }

    @Override
    protected void initDriver() throws MalformedURLException {
        driver = new AndroidDriver(new URL(SL_URL), capabilities);

    }

    @Override
    protected void setAppCapability() {
        // To run locally use https://applitools.jfrog.io/artifactory/Examples/android/1.2/app_android.apk
        capabilities.setCapability("app", "app_android");
    }

    @Override
    protected String getApplicationName() {
        return "Java Appium - Android";
    }
}
