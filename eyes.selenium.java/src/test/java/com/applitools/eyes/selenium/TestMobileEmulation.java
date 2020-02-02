package com.applitools.eyes.selenium;

import com.applitools.eyes.selenium.fluent.Target;
import com.applitools.eyes.utils.ChromeMobileEmulationDeviceSettings;
import com.applitools.eyes.utils.SeleniumUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

@Listeners(TestListener.class)
public class TestMobileEmulation {

    @Test
    public void TestCheckRegion_LoadPageAfterOpen()
    {
        Eyes eyes = null;
        WebDriver webDriver = null;
        try
        {
            ChromeMobileEmulationDeviceSettings mobileSettings = new ChromeMobileEmulationDeviceSettings(
                    "Mozilla/5.0 (Linux; Android 8.0.0; Android SDK built for x86_64 Build/OSR1.180418.004) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/69.0.3497.100 Mobile Safari/537.36",
                    384, 512, 2);

            ChromeOptions chromeOptions = new ChromeOptions();
            chromeOptions.setExperimentalOption("mobileEmulation", mobileSettings.toMap());
            chromeOptions.setExperimentalOption("w3c", false);
            webDriver = SeleniumUtils.createChromeDriver(chromeOptions);


            eyes = new Eyes();
            Configuration configuration = eyes.getConfiguration();
            configuration.setAppName("TestMobileEmulation").setTestName("TestCheckRegion_LoadPageAfterOpen");
            eyes.setConfiguration(configuration);
            eyes.open(webDriver);

            webDriver.get("https://applitools.github.io/demo/TestPages/SpecialCases/hero.html");

            eyes.check(Target.region(By.cssSelector("img")).fully().withName("Element outside the viewport"));
            eyes.close();
        }
        finally
        {
            if (eyes != null) eyes.abortIfNotClosed();
            if (webDriver != null) webDriver.quit();
        }
    }
}
