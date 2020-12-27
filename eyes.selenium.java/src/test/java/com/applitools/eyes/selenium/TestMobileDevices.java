package com.applitools.eyes.selenium;

import com.applitools.eyes.TestResults;
import com.applitools.eyes.selenium.fluent.Target;
import com.applitools.eyes.utils.*;
import org.openqa.selenium.ScreenOrientation;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.ITest;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static com.applitools.eyes.selenium.TestDataProvider.*;


public class TestMobileDevices extends ReportingTestSuite implements ITest {

    public final String deviceName;
    public final String platformVersion;
    public final ScreenOrientation deviceOrientation;
    public final String page;
    private final String testName;

    public TestMobileDevices(String deviceName, String platformVersion, ScreenOrientation deviceOrientation, String page) {
        super.setGroupName("selenium");
        this.deviceName = deviceName;
        this.platformVersion = platformVersion;
        this.deviceOrientation = deviceOrientation;
        this.page = page;
        this.testName = initTestName(deviceName, platformVersion, deviceOrientation, this.page);
    }

    protected static void initEyes(String deviceName, String platformVersion, ScreenOrientation deviceOrientation, String platformName, String browserName, String page) {
        Eyes eyes = new Eyes();

        eyes.setBatch(TestDataProvider.batchInfo);
        eyes.setSaveNewTests(false);
        eyes.setStitchMode(StitchMode.CSS);

        eyes.addProperty("Orientation", deviceOrientation.toString());
        eyes.addProperty("Page", page);

        String testName = getTestName(deviceName, platformVersion, deviceOrientation, page);

        SeleniumTestUtils.setupLogging(eyes, testName);
        WebDriver driver = initEyesSimulation(deviceName, platformVersion, deviceOrientation, testName + " " + eyes.getFullAgentId(), platformName, browserName);

        if (driver != null) {
            driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
            runTest(true, eyes, testName, driver, page);
        }
    }

    private static WebDriver initOnSaucelabs(String deviceName, String platformVersion, ScreenOrientation deviceOrientation, String testName, String platformName, String browserName) throws MalformedURLException {
        DesiredCapabilities caps = new DesiredCapabilities();

        caps.setCapability("deviceName", deviceName);
        caps.setCapability("deviceOrientation", deviceOrientation.toString().toLowerCase());
        caps.setCapability("platformVersion", platformVersion);
        caps.setCapability("platformName", platformName);
        caps.setCapability("browserName", browserName);

        caps.setCapability("username", SAUCE_USERNAME);
        caps.setCapability("accesskey", SAUCE_ACCESS_KEY);

        caps.setCapability("name", testName);

        String sauceUrl = SAUCE_SELENIUM_URL;
        WebDriver driver = new RemoteWebDriver(new URL(sauceUrl), caps);
        return driver;
    }

    private static WebDriver initEyesSimulation(String deviceName, String platformVersion, ScreenOrientation deviceOrientation, String testName, String platformName, String browserName) {
        if (chromeSimulationData == null) {
            initChromeSimulationData();
        }
        ChromeMobileEmulationDeviceSettings mobileSettings = chromeSimulationData.get(deviceName + ";" + platformVersion + ";" + deviceOrientation);
        WebDriver driver = null;
        if (mobileSettings != null) {
            ChromeOptions options = new ChromeOptions();
            options.setExperimentalOption("mobileEmulation", mobileSettings.toMap());
            options.setExperimentalOption("w3c", false);
            driver = SeleniumUtils.createChromeDriver(options);
        }
        return driver;
    }

    private static Map<String, ChromeMobileEmulationDeviceSettings> chromeSimulationData;

    private static void initChromeSimulationData() {
        chromeSimulationData = new HashMap<>();

        // Android
        chromeSimulationData.put("Android Emulator;8.0;PORTRAIT", new ChromeMobileEmulationDeviceSettings(
                "Mozilla/5.0 (Linux; Android 8.0.0; Android SDK built for x86_64 Build/OSR1.180418.004) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/69.0.3497.100 Mobile Safari/537.36",
                384, 512, 2));

        chromeSimulationData.put("Android Emulator;8.0;LANDSCAPE", new ChromeMobileEmulationDeviceSettings(
                "Mozilla/5.0 (Linux; Android 8.0.0; Android SDK built for x86_64 Build/OSR1.180418.004) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/69.0.3497.100 Mobile Safari/537.36",
                512, 384, 2));

        // iPads, Landscape

        // resolution: 2048 x 1536 ; viewport: 2048 x 1408
        // [iPad Air 2, 10.3]
        chromeSimulationData.put("iPad Air 2 Simulator;10.3;LANDSCAPE", new ChromeMobileEmulationDeviceSettings(
                "Mozilla/5.0 (iPad; CPU OS 11_0_1 like Mac OS X) AppleWebKit/604.2.10 (KHTML, like Gecko) Version/11.0 Mobile/15A8401 Safari/604.1",
                512, 352, 4));

        // resolution: 2048 x 1536 ; viewport: 2048 x 1396
        // [iPad Air 2, 12.0] [iPad Air 2, 11.0] [iPad Air, 12.0] [iPad Air, 11.0] [iPad Pro (9.7 inch), 11.0] [iPad, 11.0]
        chromeSimulationData.put("iPad Air 2 Simulator;12.0;LANDSCAPE", new ChromeMobileEmulationDeviceSettings(
                "Mozilla/5.0 (iPad; CPU OS 11_0_1 like Mac OS X) AppleWebKit/604.2.10 (KHTML, like Gecko) Version/11.0 Mobile/15A8401 Safari/604.1",
                512, 349, 4));

        // resolution: 2048 x 1536 ; viewport: 2048 x 1331
        // [iPad Air 2, 11.3] [iPad (5th generation), 11.0]
        chromeSimulationData.put("iPad Air 2 Simulator;11.3;LANDSCAPE", new ChromeMobileEmulationDeviceSettings(
                "Mozilla/5.0 (iPad; CPU OS 11_0_1 like Mac OS X) AppleWebKit/604.2.10 (KHTML, like Gecko) Version/11.0 Mobile/15A8401 Safari/604.1",
                512, 333, 4));

        // resolution: 2732 x 2048 ; viewport: 2732 x 1908
        // [iPad Pro (12.9 inch) (2nd generation), 11.0] [iPad Pro (12.9 inch) (2nd generation), 12.0]
        chromeSimulationData.put("iPad Pro (12.9 inch) (2nd generation) Simulator;11.0;LANDSCAPE", new ChromeMobileEmulationDeviceSettings(
                "Mozilla/5.0 (iPad; CPU OS 11_0_1 like Mac OS X) AppleWebKit/604.2.10 (KHTML, like Gecko) Version/11.0 Mobile/15A8401 Safari/604.1",
                683, 477, 4));

        // resolution: 2224 x 1668 ; viewport: 2224 x 1528
        // [iPad Pro (10.5 inch), 11.0]
        chromeSimulationData.put("iPad Pro (10.5 inch) Simulator;11.0;LANDSCAPE", new ChromeMobileEmulationDeviceSettings(
                "Mozilla/5.0 (iPad; CPU OS 11_0_1 like Mac OS X) AppleWebKit/604.2.10 (KHTML, like Gecko) Version/11.0 Mobile/15A8401 Safari/604.1",
                556, 382, 4));

        // iPads, Portrait

        // resolution: 1536 x 2048; viewport: 1536 x 1843
        // [iPad Air 2, 11.3] [iPad (5th generation), 11.0]
        chromeSimulationData.put("iPad (5th generation) Simulator;11.0;PORTRAIT", new ChromeMobileEmulationDeviceSettings(
                "Mozilla/5.0 (iPad; CPU OS 10_3 like Mac OS X) AppleWebKit/603.1.30 (KHTML, like Gecko) Version/10.0 Mobile/14E269 Safari/602.1",
                768, 922, 2));

        // resolution: 1536 x 2048; viewport: 1536 x 1920
        // [iPad Air 2, 10.3]
        chromeSimulationData.put("iPad Air 2 Simulator;10.3;PORTRAIT", new ChromeMobileEmulationDeviceSettings(
                "Mozilla/5.0 (iPad; CPU OS 10_3 like Mac OS X) AppleWebKit/603.1.30 (KHTML, like Gecko) Version/10.0 Mobile/14E269 Safari/602.1",
                768, 960, 2));

        // resolution: 1536 x 2048; viewport: 1536 x 1908
        // [iPad Air 2, 11.0] [iPad Air 2, 12.0] [iPad Air, 11.0] [iPad, 11.0] [iPad Pro (9.7 inch), 12.0] [iPad Pro (9.7 inch), 11.0]
        chromeSimulationData.put("iPad Air 2 Simulator;11.0;PORTRAIT", new ChromeMobileEmulationDeviceSettings(
                "Mozilla/5.0 (iPad; CPU OS 10_3 like Mac OS X) AppleWebKit/603.1.30 (KHTML, like Gecko) Version/10.0 Mobile/14E269 Safari/602.1",
                768, 954, 2));

        // resolution: 2048 x 2732 ; viewport: 2048 x 2592
        // [iPad Pro (12.9 inch) (2nd generation), 11.0] [iPad Pro (12.9 inch) (2nd generation), 12.0]
        chromeSimulationData.put("iPad Pro (12.9 inch) (2nd generation) Simulator;11.0;PORTRAIT", new ChromeMobileEmulationDeviceSettings(
                "Mozilla/5.0 (iPad; CPU OS 10_3 like Mac OS X) AppleWebKit/603.1.30 (KHTML, like Gecko) Version/10.0 Mobile/14E269 Safari/602.1",
                1024, 1296, 2));

        // resolution: 1668 x 2224 ; viewport: 1668 x 2084
        // [iPad Pro (10.5 inch), 11.0]
        chromeSimulationData.put("iPad Pro (10.5 inch) Simulator;11.0;PORTRAIT", new ChromeMobileEmulationDeviceSettings(
                "Mozilla/5.0 (iPad; CPU OS 10_3 like Mac OS X) AppleWebKit/603.1.30 (KHTML, like Gecko) Version/10.0 Mobile/14E269 Safari/602.1",
                834, 1042, 2));


        // iPhones, Landscape

        // resolution: 2436 x 1125 ; viewport: 2172 x 912
        // [iPhone XS, 12.2] [iPhone X, 11.2]
        chromeSimulationData.put("iPhone XS Simulator;12.2;LANDSCAPE", new ChromeMobileEmulationDeviceSettings(
                "Mozilla/5.0 (iPhone; CPU iPhone OS 13_0 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/13.0 Mobile/15E148 Safari/604.1",
                724, 304, 3));

        // resolution: 2436 x 1125 ; viewport: 2172 x 813
        // [iPhone 11 Pro, 13.0]
        chromeSimulationData.put("iPhone 11 Pro Simulator;13.0;LANDSCAPE", new ChromeMobileEmulationDeviceSettings(
                "Mozilla/5.0 (iPhone; CPU iPhone OS 13_0 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/13.0 Mobile/15E148 Safari/604.1",
                724, 271, 3));


        // resolution: 2688 x 1242 ; viewport: 2424 x 1030
        // [iPhone XS Max, 12.2]
        chromeSimulationData.put("iPhone XS Max Simulator;12.2;LANDSCAPE", new ChromeMobileEmulationDeviceSettings(
                "Mozilla/5.0 (iPhone; CPU iPhone OS 13_0 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/13.0 Mobile/15E148 Safari/604.1",
                808, 344, 3));

        // resolution: 2688 x 1242 ; viewport: 2424 x 922
        // [iPhone 11 Pro Max, 13.0]
        chromeSimulationData.put("iPhone 11 Pro Max Simulator;13.0;LANDSCAPE", new ChromeMobileEmulationDeviceSettings(
                "Mozilla/5.0 (iPhone; CPU iPhone OS 13_0 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/13.0 Mobile/15E148 Safari/604.1",
                808, 307, 3));

        // resolution: 1792 x 828 ; viewport: 1616 x 686
        // [iPhone XR, 12.2]
        chromeSimulationData.put("iPhone XR Simulator;12.2;LANDSCAPE", new ChromeMobileEmulationDeviceSettings(
                "Mozilla/5.0 (iPhone; CPU iPhone OS 13_0 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/13.0 Mobile/15E148 Safari/604.1",
                808, 343, 2));

        // resolution: 1792 x 828 ; viewport: 1616 x 620
        // [iPhone 11, 13.0]
        chromeSimulationData.put("iPhone 11 Simulator;13.0;LANDSCAPE", new ChromeMobileEmulationDeviceSettings(
                "Mozilla/5.0 (iPhone; CPU iPhone OS 13_0 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/13.0 Mobile/15E148 Safari/604.1",
                808, 310, 2));

        // resolution: 2208 x 1242 ; viewport: 2208 x 1092
        // [iPhone 6 Plus, 11.0]
        chromeSimulationData.put("iPhone 6 Plus Simulator;11.0;LANDSCAPE", new ChromeMobileEmulationDeviceSettings(
                "Mozilla/5.0 (iPhone; CPU iPhone OS 13_0 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/13.0 Mobile/15E148 Safari/604.1",
                736, 364, 3));

        // resolution: 1334 x 750 ; viewport: 1334 x 662
        // [iPhone 7, 10.3]
        chromeSimulationData.put("iPhone 7 Simulator;10.3;LANDSCAPE", new ChromeMobileEmulationDeviceSettings(
                "Mozilla/5.0 (iPhone; CPU iPhone OS 13_0 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/13.0 Mobile/15E148 Safari/604.1",
                667, 331, 2));

        // resolution: 2208 x 1242 ; viewport: 2208 x 1110
        // [iPhone 7 Plus, 10.3]
        chromeSimulationData.put("iPhone 7 Plus Simulator;10.3;LANDSCAPE", new ChromeMobileEmulationDeviceSettings(
                "Mozilla/5.0 (iPhone; CPU iPhone OS 13_0 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/13.0 Mobile/15E148 Safari/604.1",
                736, 370, 3));

        // resolution: 1136 x 640 ; viewport: 1136 x 464
        // [iPhone 5s, 10.3]
        chromeSimulationData.put("iPhone 5s Simulator;10.3;LANDSCAPE", new ChromeMobileEmulationDeviceSettings(
                "Mozilla/5.0 (iPhone; CPU iPhone OS 13_0 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/13.0 Mobile/15E148 Safari/604.1",
                568, 232, 2));

        // iPhones, Portrait

        // resolution: 1125 x 2436 ; viewport: 1125 x 1905
        // [iPhone XS, 12.2] [iPhone X, 11.2] [iPhone 11 Pro, 13.0]
        chromeSimulationData.put("iPhone XS Simulator;12.2;PORTRAIT", new ChromeMobileEmulationDeviceSettings(
                "Mozilla/5.0 (iPhone; CPU iPhone OS 13_0 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/13.0 Mobile/15E148 Safari/604.1",
                375, 635, 3));

        // resolution: 1242 x 2688 ; viewport: 1242 x 2157
        // [iPhone XS Max, 12.2] [iPhone 11 Pro Max, 13.0]
        chromeSimulationData.put("iPhone XS Max Simulator;12.2;PORTRAIT", new ChromeMobileEmulationDeviceSettings(
                "Mozilla/5.0 (iPhone; CPU iPhone OS 13_0 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/13.0 Mobile/15E148 Safari/604.1",
                414, 719, 3));

        // resolution: 828 x 1792 ; viewport: 828 x 1438
        // [iPhone XR, 12.2] [iPhone 11, 13.0]
        chromeSimulationData.put("iPhone XR Simulator;12.2;PORTRAIT", new ChromeMobileEmulationDeviceSettings(
                "Mozilla/5.0 (iPhone; CPU iPhone OS 13_0 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/13.0 Mobile/15E148 Safari/604.1",
                414, 719, 2));

        // resolution: 1242 x 2208 ; viewport: 1242 x 1866
        // [iPhone 6 Plus, 11.0]
        chromeSimulationData.put("iPhone 6 Plus Simulator;11.0;PORTRAIT", new ChromeMobileEmulationDeviceSettings(
                "Mozilla/5.0 (iPhone; CPU iPhone OS 13_0 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/13.0 Mobile/15E148 Safari/604.1",
                414, 622, 3));

        // resolution: 750 x 1334 ; viewport: 750 x 1118
        // [iPhone 7, 10.3]
        chromeSimulationData.put("iPhone 7 Simulator;10.3;PORTRAIT", new ChromeMobileEmulationDeviceSettings(
                "Mozilla/5.0 (iPhone; CPU iPhone OS 13_0 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/13.0 Mobile/15E148 Safari/604.1",
                375, 559, 2));

        // resolution: 640 x 1136 ; viewport: 640 x 920
        // [iPhone 5s, 10.3]
        chromeSimulationData.put("iPhone 5s Simulator;10.3;PORTRAIT", new ChromeMobileEmulationDeviceSettings(
                "Mozilla/5.0 (iPhone; CPU iPhone OS 13_0 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/13.0 Mobile/15E148 Safari/604.1",
                320, 460, 2
        ));
    }

    private static String getTestName(String deviceName, String platformVersion, ScreenOrientation deviceOrientation, String page) {
        String orientation = deviceOrientation == ScreenOrientation.LANDSCAPE ? "Landscape" : "Portrait";
        return deviceName + " " + platformVersion + " " + orientation + " " + page + " fully";
    }

    protected static List<Object[]> addPageType(List<Object[]> devices) {
        List<Object[]> devicesWithPage = new ArrayList<Object[]>();
        for (String page : Arrays.asList(new String[]{"mobile", "desktop", "scrolled_mobile"})) {
            for (Object[] device : devices) {
                Object[] params = new Object[device.length + 1];
                for (int i = 0; i < device.length; i++) params[i] = device[i];
                params[device.length] = page;
                devicesWithPage.add(params);
            }
        }
        return devicesWithPage;
    }

    private static TestResults runTest(boolean fully, Eyes eyes, String testName, WebDriver driver, String page) {
        try {
            driver.get("https://applitools.github.io/demo/TestPages/DynamicResolution/" + page + ".html");
            eyes.open(driver, "Eyes Selenium SDK - Mobile Devices", testName);
            //eyes.Check("Initial view", Target.Region(By.CssSelector("div.page")).Fully(fully).SendDom(false));
            eyes.check(Target.window().fully(fully));
            TestResults result = eyes.close();
            return result;
            //SessionId session = ((RemoteWebDriver) driver).getSessionId();
            //CommUtils.putTestResultJsonToSauceLabs(new PassedResult(result.isPassed()), session.toString());
        } finally {
            eyes.abort();
            driver.quit();
        }
    }

    private static String initTestName(String deviceName, String platformVersion, ScreenOrientation deviceOrientation, String page) {
        String deviceOrientationStr = (deviceOrientation.toString().equals("LANDSCAPE")) ? "Landscape" : "Portrait";
        String testName = deviceName + " " + platformVersion + " " + deviceOrientationStr + " " + page + " fully";
        return testName;
    }

    @Override
    public String getTestName() {
        return this.testName;
    }
}
