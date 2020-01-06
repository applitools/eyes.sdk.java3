package com.applitools.eyes.selenium;

import com.applitools.eyes.TestResults;
import com.applitools.eyes.selenium.fluent.Target;
import com.applitools.eyes.utils.CommUtils;
import com.applitools.eyes.utils.PassedResult;
import com.applitools.eyes.utils.TestUtils;
import com.applitools.utils.GeneralUtils;
import org.openqa.selenium.ScreenOrientation;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.SessionId;
import org.testng.Assert;
import org.testng.ITest;
import org.testng.annotations.DataProvider;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.applitools.eyes.selenium.TestDataProvider.*;


public class TestMobileDevices implements ITest {
    public final String deviceName;
    public final String platformVersion;
    public final ScreenOrientation deviceOrientation;
    public final boolean fully;
    public final String page;
    private final String testName;

    public TestMobileDevices(String deviceName, String platformVersion, ScreenOrientation deviceOrientation, boolean fully, String page) {
        this.deviceName = deviceName;
        this.platformVersion = platformVersion;
        this.deviceOrientation = deviceOrientation;
        this.fully = fully;
        this.page = page;
        this.testName = initTestName(deviceName, platformVersion, deviceOrientation, fully, this.page);
    }

    @DataProvider(name = "IOSDevices", parallel = true)
    public static Object[][] IOSDevices() {
        List<Object[]> devices = new ArrayList<Object[]>(Arrays.asList(new Object[][]{
                {"iPad Pro (9.7 inch) Simulator", "12.0", ScreenOrientation.LANDSCAPE, false},
                {"iPhone XR Simulator", "12.2", ScreenOrientation.PORTRAIT, true}
        }));
        if (TestUtils.runOnCI) {
            devices.addAll(Arrays.asList(new Object[][]{
                    {"iPad Air 2 Simulator", "12.0", ScreenOrientation.LANDSCAPE, false},
                    {"iPad Air 2 Simulator", "11.3", ScreenOrientation.LANDSCAPE, false},
                    {"iPad Air 2 Simulator", "11.0", ScreenOrientation.LANDSCAPE, false},
                    {"iPad Air 2 Simulator", "10.3", ScreenOrientation.LANDSCAPE, false},
                    {"iPad Air 2 Simulator", "12.0", ScreenOrientation.PORTRAIT, false},
                    {"iPad Air 2 Simulator", "11.3", ScreenOrientation.PORTRAIT, false},
                    {"iPad Air 2 Simulator", "11.0", ScreenOrientation.PORTRAIT, false},
                    {"iPad Air 2 Simulator", "10.3", ScreenOrientation.PORTRAIT, false},
                    {"iPad Air Simulator", "12.0", ScreenOrientation.LANDSCAPE, false},
                    {"iPad Air Simulator", "11.0", ScreenOrientation.PORTRAIT, true},
                    {"iPad Simulator", "11.0", ScreenOrientation.LANDSCAPE, true},
                    {"iPad Simulator", "11.0", ScreenOrientation.PORTRAIT, false},
                    {"iPad (5th generation) Simulator", "11.0", ScreenOrientation.PORTRAIT, false},
                    {"iPad Pro (9.7 inch) Simulator", "11.0", ScreenOrientation.LANDSCAPE, false},
                    {"iPad Pro (9.7 inch) Simulator", "11.0", ScreenOrientation.LANDSCAPE, true},
                    {"iPad Pro (12.9 inch) (2nd generation) Simulator", "11.0", ScreenOrientation.LANDSCAPE, false},
                    {"iPad Pro (12.9 inch) (2nd generation) Simulator", "11.0", ScreenOrientation.PORTRAIT, true},
                    {"iPad Pro (12.9 inch) (2nd generation) Simulator", "12.0", ScreenOrientation.PORTRAIT, true},
                    {"iPad Pro (10.5 inch) Simulator", "11.0", ScreenOrientation.PORTRAIT, false},
                    {"iPad Pro (10.5 inch) Simulator", "11.0", ScreenOrientation.LANDSCAPE, true},
                    {"iPhone XS Max Simulator", "12.2", ScreenOrientation.LANDSCAPE, false},
                    {"iPhone XS Max Simulator", "12.2", ScreenOrientation.LANDSCAPE, true},
                    {"iPhone XS Max Simulator", "12.2", ScreenOrientation.PORTRAIT, false},
                    {"iPhone XS Max Simulator", "12.2", ScreenOrientation.PORTRAIT, true},
                    {"iPhone XS Simulator", "12.2", ScreenOrientation.PORTRAIT, false},
                    {"iPhone XS Simulator", "12.2", ScreenOrientation.LANDSCAPE, false},
                    {"iPhone XS Simulator", "12.2", ScreenOrientation.PORTRAIT, true},
                    {"iPhone XS Simulator", "12.2", ScreenOrientation.LANDSCAPE, true},
                    {"iPhone XR Simulator", "12.2", ScreenOrientation.PORTRAIT, false},
                    {"iPhone XR Simulator", "12.2", ScreenOrientation.LANDSCAPE, false},
                    {"iPhone XR Simulator", "12.2", ScreenOrientation.LANDSCAPE, true},
                    {"iPhone X Simulator", "11.2", ScreenOrientation.PORTRAIT, false},
                    {"iPhone X Simulator", "11.2", ScreenOrientation.PORTRAIT, true},
                    {"iPhone 7 Simulator", "10.3", ScreenOrientation.PORTRAIT, true},
                    {"iPhone 7 Plus Simulator", "10.3", ScreenOrientation.LANDSCAPE, false},
                    {"iPhone 6 Plus Simulator", "11.0", ScreenOrientation.PORTRAIT, false},
                    {"iPhone 6 Plus Simulator", "11.0", ScreenOrientation.LANDSCAPE, true},
                    {"iPhone 5s Simulator", "10.3", ScreenOrientation.LANDSCAPE, false},
                    {"iPhone 5s Simulator", "10.3", ScreenOrientation.LANDSCAPE, true}
            }));
        }
        devices = addPageType(devices);
        return devices.toArray(new Object[0][]);
    }

    @DataProvider(name = "androidDevices", parallel = true)
    public static Object[][] androidDevices() {
        List<Object[]> devices = Arrays.asList(new Object[][]{
                {"Android Emulator", "8.0", ScreenOrientation.PORTRAIT, false},
                {"Android Emulator", "8.0", ScreenOrientation.LANDSCAPE, true}
        });
        devices = addPageType(devices);
        return devices.toArray(new Object[0][]);
    }

    protected void initEyes(String deviceName, String platformVersion, ScreenOrientation deviceOrientation, boolean fully,
                          String platformName, String browserName, String page) {
        Eyes eyes = new Eyes();

        eyes.setBatch(TestDataProvider.batchInfo);

        DesiredCapabilities caps = new DesiredCapabilities();

        caps.setCapability("deviceName", deviceName);
        caps.setCapability("deviceOrientation", deviceOrientation.toString().toLowerCase());
        caps.setCapability("platformVersion", platformVersion);
        caps.setCapability("platformName", platformName);
        caps.setCapability("browserName", browserName);

        caps.setCapability("username", SAUCE_USERNAME);
        caps.setCapability("accesskey", SAUCE_ACCESS_KEY);

        caps.setCapability("name", testName + " (" + eyes.getFullAgentId() + ")");

        String sauceUrl = SAUCE_SELENIUM_URL;
        WebDriver driver = null;
        try {
            driver = new RemoteWebDriver(new URL(sauceUrl), caps);
        } catch (MalformedURLException e) {
            GeneralUtils.logExceptionStackTrace(eyes.getLogger(), e);
        }
        if (driver != null) {
            driver.manage().timeouts().implicitlyWait(3, TimeUnit.SECONDS);

            TestUtils.setupLogging(eyes, testName);

            eyes.setStitchMode(StitchMode.CSS);

            eyes.addProperty("Orientation", deviceOrientation.toString());
            eyes.addProperty("Stitched", fully ? "True" : "False");
            runTest(fully, eyes, testName, driver, page);
        } else {
            Assert.fail("failed to initialize web driver.");
        }
    }

    private static List<Object[]> addPageType(List<Object[]> devices)
    {
        List<Object[]> devicesWithPage = new ArrayList<Object[]>();
        for(String page : Arrays.asList(new String[]{"mobile", "desktop", "scrolled_mobile"}))
        {
            for(Object[] device : devices)
            {
                Object[] params = new Object[device.length + 1];
                for(int i=0; i < device.length; i++) params[i] = device[i];
                params[device.length] = page;
                devicesWithPage.add(params);
            }
        }
        return devicesWithPage;
    }

    private void runTest(boolean fully, Eyes eyes, String testName, WebDriver driver, String page) {
        try {
            driver.get("https://applitools.github.io/demo/TestPages/DynamicResolution/" + page + ".html");
            eyes.open(driver, "Eyes Selenium SDK - iOS Safari Cropping", testName);
            //eyes.Check("Initial view", Target.Region(By.CssSelector("div.page")).Fully(fully).SendDom(false));
            eyes.check(Target.window().fully(fully));
            TestResults result = eyes.close();

            SessionId session = ((RemoteWebDriver) driver).getSessionId();
            CommUtils.putTestResultJsonToSauceLabs(new PassedResult(result.isPassed()), session.toString());
        } finally {
            eyes.abort();
            driver.quit();
        }
    }

    private static String initTestName(String deviceName, String platformVersion, ScreenOrientation deviceOrientation, boolean fully, String page) {
        String deviceOrientationStr = (deviceOrientation.toString().equals("LANDSCAPE")) ? "Landscape" : "Portrait";
        String testName = deviceName + " " + platformVersion + " " + deviceOrientationStr + " " + page;

        if (fully) {
            testName += " fully";
        }

        return testName;
    }

    @Override
    public String getTestName() {
        return this.testName;
    }

}
