package com.applitools.eyes.appium;

import com.applitools.eyes.ApplitoolsLibMode;
import com.applitools.eyes.ProxySettings;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.testng.Assert;
import org.testng.annotations.Test;

public class TestNMGCapabilities {
    private final String API_KEY = "asd123";
    private final String SERVER_URL = "https://eyesapi.applitools.com";
    private final ProxySettings proxySettings = new ProxySettings("http://127.0.0.1", 8888);

    @Test
    public void testAndroidLibMode() {
        DesiredCapabilities caps = getCaps();

        Eyes.setNMGCapabilities(caps, ApplitoolsLibMode.ANDROID_APP, API_KEY, SERVER_URL, proxySettings);

        String args = (String) caps.getCapability("optionalIntentArguments");
        Assert.assertNotNull(args);

        String[] intentArguments = args.split(" ", 3);
        Assert.assertEquals(intentArguments[0], "--es");
        Assert.assertEquals(intentArguments[1], "APPLITOOLS");
        Assert.assertEquals(intentArguments[2], String.format("\\'{\\\"NML_API_KEY\\\":\\\"%s\\\",\\\"NML_SERVER_URL\\\":\\\"%s\\\",\\\"NML_PROXY_URL\\\":\\\"%s\\\",}\\'", API_KEY, SERVER_URL, proxySettings));
    }

    @Test
    public void testIosBuiltInLibMode() {
        DesiredCapabilities caps = getCaps();

        Eyes.setNMGCapabilities(caps, ApplitoolsLibMode.IOS_APP_BUILT_IN, API_KEY, SERVER_URL, proxySettings);

        String args = (String) caps.getCapability("processArguments");
        Assert.assertNotNull(args);
        String[] intentArguments = args.split(" ", 3);
        Assert.assertEquals(intentArguments[0], "{\\\"args\\\":");
        Assert.assertEquals(intentArguments[1], "[],");
        Assert.assertEquals(intentArguments[2], String.format("\\\"env\\\":{\\\"NML_API_KEY\\\":\\\"%s\\\",\\\"NML_SERVER_URL\\\":\\\"%s\\\",\\\"NML_PROXY_URL\\\":\\\"%s\\\",}", API_KEY, SERVER_URL, proxySettings));
    }

    @Test
    public void testIosInstrumentedRealDeviceLibMode() {
        DesiredCapabilities caps = getCaps();
        String REAL_DEVICE_PATH = "@executable_path/Frameworks/UFG_lib.xcframework/ios-arm64/UFG_lib.framework/UFG_lib";

        Eyes.setNMGCapabilities(caps, ApplitoolsLibMode.IOS_APP_INSTRUMENTED_REAL_DEVICE, API_KEY, SERVER_URL, proxySettings);

        String args = (String) caps.getCapability("processArguments");
        Assert.assertNotNull(args);

        String[] intentArguments = args.split(" ", 3);
        Assert.assertEquals(intentArguments[0], "{\\\"args\\\":");
        Assert.assertEquals(intentArguments[1], "[],");
        Assert.assertEquals(intentArguments[2], String.format("\\\"env\\\":{\\\"DYLD_INSERT_LIBRARIES\\\":\\\"%s\\\",\\\"NML_API_KEY\\\":\\\"%s\\\",\\\"NML_SERVER_URL\\\":\\\"%s\\\",\\\"NML_PROXY_URL\\\":\\\"%s\\\",}", REAL_DEVICE_PATH, API_KEY, SERVER_URL, proxySettings));
    }

    @Test
    public void testIosInstrumentedSimulatorLibMode() {
        DesiredCapabilities caps = getCaps();
        String SIMULATOR_PATH = "@executable_path/Frameworks/UFG_lib.xcframework/ios-arm64_x86_64-simulator/UFG_lib";

        Eyes.setNMGCapabilities(caps, ApplitoolsLibMode.IOS_APP_INSTRUMENTED_SIMULATOR, API_KEY, SERVER_URL, proxySettings);

        String args = (String) caps.getCapability("processArguments");
        Assert.assertNotNull(args);

        String[] intentArguments = args.split(" ", 3);
        Assert.assertEquals(intentArguments[0], "{\\\"args\\\":");
        Assert.assertEquals(intentArguments[1], "[],");
        Assert.assertEquals(intentArguments[2], String.format("\\\"env\\\":{\\\"DYLD_INSERT_LIBRARIES\\\":\\\"%s\\\",\\\"NML_API_KEY\\\":\\\"%s\\\",\\\"NML_SERVER_URL\\\":\\\"%s\\\",\\\"NML_PROXY_URL\\\":\\\"%s\\\",}", SIMULATOR_PATH, API_KEY, SERVER_URL, proxySettings));
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
