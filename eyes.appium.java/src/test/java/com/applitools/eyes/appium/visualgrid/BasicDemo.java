package com.applitools.eyes.appium.visualgrid;

import com.applitools.eyes.ProxySettings;
import com.applitools.eyes.StdoutLogHandler;
import com.applitools.eyes.appium.Eyes;
import com.applitools.eyes.appium.Target;
import com.applitools.eyes.visualgrid.model.AndroidDeviceInfo;
import com.applitools.eyes.visualgrid.model.AndroidDeviceName;
import com.applitools.eyes.visualgrid.model.DeviceAndroidVersion;
import com.applitools.eyes.visualgrid.services.VisualGridRunner;
import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.testng.annotations.Test;

import java.net.URL;

public class BasicDemo {

    @Test
    public void test() throws Exception {
        DesiredCapabilities capabilities = new DesiredCapabilities();
        capabilities.setCapability("platformName", "Android");
        capabilities.setCapability("fullReset", false);
        capabilities.setCapability("noReset", true);
        AndroidDriver<WebElement> driver = new AndroidDriver<>(new URL("http://127.0.0.1:4723/wd/hub"), capabilities);

        VisualGridRunner runner = new VisualGridRunner();
        Eyes eyes = new Eyes(runner);
//        eyes.setConfiguration(eyes.getConfiguration().addMobileDevice(new AndroidDeviceInfo(AndroidDeviceName.Galaxy_Note_10, DeviceAndroidVersion.LATEST)));
        eyes.setLogHandler(new StdoutLogHandler(true));
        eyes.setProxy(new ProxySettings("http://localhost:8888"));
        eyes.setApiKey("TKYcQIFGLNd0KxaoV72Ruw5CDPRUGEWmy1qqF9mTtqw110");
        eyes.open(driver, "test native", "test native");
        try {
            eyes.check(Target.window().fully(false));
            eyes.closeAsync();
        } finally {
            driver.quit();
            eyes.abortAsync();
            runner.getAllTestResults();
        }
    }
}
