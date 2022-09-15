package com.applitools.eyes.appium;

import com.applitools.eyes.ProxySettings;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.testng.Assert;
import org.testng.annotations.Test;

public class TestNMGCapabilities {
    private final String API_KEY = "asd123";
    private final String SERVER_URL = "https://eyesapi.applitools.com";
    private final ProxySettings proxySettings = new ProxySettings("http://127.0.0.1", 8888);
    private final String LIBRARY_PATH = "@executable_path/Frameworks/UFG_lib.xcframework/ios-arm64/UFG_lib.framework/UFG_lib:@executable_path/Frameworks/UFG_lib.xcframework/ios-arm64_x86_64-simulator/UFG_lib.framework/UFG_lib";

    @Test
    public void testAndroidNMGCaps() {
        DesiredCapabilities caps = getCaps();

        Eyes.setNMGCapabilities(caps, API_KEY, SERVER_URL, proxySettings);

        String androidArgs = (String) caps.getCapability("optionalIntentArguments");
        Assert.assertNotNull(androidArgs);

        String[] androidIntentArguments = androidArgs.split(" ", 3);
        Assert.assertEquals(androidIntentArguments[0], "--es");
        Assert.assertEquals(androidIntentArguments[1], "APPLITOOLS");
        Assert.assertEquals(androidIntentArguments[2], String.format("'{\"NML_API_KEY\":\"%s\",\"NML_SERVER_URL\":\"%s\",\"NML_PROXY_URL\":\"%s\",}'", API_KEY, SERVER_URL, proxySettings));
    }

    @Test
    public void testIosNMGCaps() {
        DesiredCapabilities caps = getCaps();

        Eyes.setNMGCapabilities(caps, API_KEY, SERVER_URL, proxySettings);

        String iosArgs = (String) caps.getCapability("processArguments");
        Assert.assertNotNull(iosArgs);

        String[] iosIntentArguments = iosArgs.split(" ", 3);
        Assert.assertEquals(iosIntentArguments[0], "{\"args\":");
        Assert.assertEquals(iosIntentArguments[1], "[],");
        Assert.assertEquals(iosIntentArguments[2], String.format("\"env\":{\"DYLD_INSERT_LIBRARIES\":\"%s\",\"NML_API_KEY\":\"%s\",\"NML_SERVER_URL\":\"%s\",\"NML_PROXY_URL\":\"%s\",}}", LIBRARY_PATH, API_KEY, SERVER_URL, proxySettings));
    }

    private DesiredCapabilities getCaps() {
        DesiredCapabilities caps = new DesiredCapabilities();
        caps.setCapability("deviceName","Google Pixel 5 GoogleAPI Emulator");
        caps.setCapability("deviceOrientation", "portrait");
        caps.setCapability("platformVersion","11.0");
        caps.setCapability("platformName", "Android");
        caps.setCapability("automationName", "UiAutomator2");

        return caps;
    }
}
