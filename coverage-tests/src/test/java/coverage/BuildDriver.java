package coverage;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.ie.InternetExplorerOptions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.safari.SafariOptions;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class BuildDriver {
    protected RemoteWebDriver driver;
    protected WebDriver eyesDriver;
    private final String BROWSER_PROPERTY = "browser";
    private final String DEVICE_PROPERTY = "device";
    private final String APP_PROPERTY = "app";
    private final String HEADLESS_PROPERTY = "headless";
    private final String LEGACY_PROPERTY = "legacy";
    private final String SELENIUM_CHROME_URL = getChromeUrl();
    private final String SELENIUM_FIREFOX_URL = "http://localhost:4445/wd/hub";
    private final String SAUCE_URL = "https://ondemand.saucelabs.com:443/wd/hub";


    private static String getChromeUrl() {
        String url;
        String token = System.getenv("EXECUTION_GRID_TOKEN");
        if (token != null) {
            url = "https://exec-wus.applitools.com/" + token;
        } else {
            url = "http://localhost:4444/wd/hub";
        }
        return url;
    }

    public WebDriver getDriver() {
        return eyesDriver == null ? driver : eyesDriver;
    }

    // Driver configuration

    public void buildDriver(Capabilities capabilities, String url) {
        try {
            driver = new RemoteWebDriver(new URL(url), capabilities);
        } catch (MalformedURLException ignored) {
            ignored.printStackTrace();
        }
    }

    public void buildDriver(Capabilities capabilities) {
        buildDriver(capabilities, SELENIUM_CHROME_URL);
    }

    public void buildDriver() {
        buildDriver(getChromeCaps(true));
    }

    public void buildDriver(String json) {
        JsonElement root = new JsonParser().parse(json);
        Capabilities caps;
        String url;
        boolean headless = !isHeadless(root) || getHeadless(root);
        boolean legacy = isLegacy(root) && getLegacy(root);
        if (isBrowser(root)) {
            String browserName = getBrowserName(root);
            switch (browserName) {
                case "chrome":
                    caps = getChromeCaps(headless);
                    url = SELENIUM_CHROME_URL;
                    break;
                case "firefox":
                    caps = getFirefoxCaps(headless);
                    url = SELENIUM_FIREFOX_URL;
                    break;
                case "safari-11":
                    caps = getSafari11(legacy);
                    url = SAUCE_URL;
                    break;
                case "safari-12":
                    caps = getSafari12(legacy);
                    url = SAUCE_URL;
                    break;
                case "ie-11":
                    caps = getIE11(legacy);
                    url = SAUCE_URL;
                    break;
                case "edge-18":
                    caps = getEdge18();
                    url = SAUCE_URL;
                    break;
                default:
                    throw new RuntimeException("Unsupported browser type was set for the test");
            }
        } else if (isDevice(root)) {
            url = SAUCE_URL;
            String device = getDevice(root);
            switch (device) {
                case "Android 8.0 Chrome Emulator":
                    caps = getAndroid8ChromeEmulator(false);
                    url = SELENIUM_CHROME_URL;
                    break;
                case "Samsung Galaxy S8":
                    caps = getSamsungGalaxyS8();
                    break;
                case "iPhone XS":
                    caps = getIphoneXS();
                    break;
                default:
                    throw new RuntimeException("Unsupported device type was set for the test");
            }
            if (isApp(root)) {
                MutableCapabilities app = new MutableCapabilities();
                app.setCapability("app", getApp(root));
                caps = caps.merge(app);
            }
        } else {
            throw new RuntimeException("Generated test env options contain no browser and no device property");
        }
        buildDriver(caps, url);
    }


    public void buildBrowser(String browserName, boolean headless) {
        DesiredCapabilities browser = new DesiredCapabilities();
        browser.setBrowserName(browserName);
        browser.setCapability(HEADLESS_PROPERTY, headless);
        buildDriver(browser);
    }

    public Capabilities getChromeCaps(boolean headless) {
        return new ChromeOptions().setHeadless(headless);
    }

    public Capabilities getFirefoxCaps(boolean headless) {
        return new FirefoxOptions().setHeadless(headless);
    }

    public Capabilities getSafari11(boolean legacy) {
        SafariOptions safari = new SafariOptions();
        if (legacy) {
            safari.setCapability("version", "11.0");
            safari.setCapability("platform", "macOS 10.12");
        } else {
            safari.setCapability("browserVersion", "11.0");
            safari.setCapability("platformName", "macOS 10.12");
        }
        MutableCapabilities options = new MutableCapabilities();
        options.setCapability("name", "Safari 11");
        options.setCapability("seleniumVersion", "3.4.0");
        return setSauceCredentials(safari, legacy, options);
    }

    public Capabilities getSafari12(boolean legacy) {
        SafariOptions safari = new SafariOptions();
        if (legacy) {
            safari.setCapability("version", "12.1");
            safari.setCapability("platform", "macOS 10.13");
        } else {
            safari.setCapability("browserVersion", "12.1");
            safari.setCapability("platformName", "macOS 10.13");
        }
        MutableCapabilities options = new MutableCapabilities();
        options.setCapability("name", "Safari 12");
        options.setCapability("seleniumVersion", "3.4.0");
        return setSauceCredentials(safari, legacy, options);
    }

    public Capabilities getEdge18() {
        EdgeOptions edge = new EdgeOptions();
        edge.setCapability("browserVersion", "18.17763");
        edge.setCapability("platformName", "Windows 10");
        MutableCapabilities options = new MutableCapabilities();
        options.setCapability("name", "Edge 18");
        options.setCapability("avoidProxy", true);
        options.setCapability("screenResolution", "1920x1080");
        return setSauceCredentials(edge, false, options);
    }

    public Capabilities getIE11(boolean legacy) {
        InternetExplorerOptions ie = new InternetExplorerOptions();
        if (legacy) {
            ie.setCapability("version", "11.285");
            ie.setCapability("platform", "Windows 10");
        } else {
            ie.setCapability("browserVersion", "11.285");
            ie.setCapability("platformName", "Windows 10");
        }
        MutableCapabilities options = new MutableCapabilities();
        options.setCapability("screenResolution", "1920x1080");
        options.setCapability("name", "IE 11");
        return setSauceCredentials(ie, legacy, options);
    }

    public Capabilities getSamsungGalaxyS8() {
        DesiredCapabilities device = new DesiredCapabilities();
        device.setCapability("browserName", "");
        device.setCapability("name", "Android Demo");
        device.setCapability("platformName", "Android");
        device.setCapability("platformVersion", "7.0");
        device.setCapability("appiumVersion", "1.9.1");
        device.setCapability("deviceName", "Samsung Galaxy S8 FHD GoogleAPI Emulator");
        device.setCapability("automationName", "uiautomator2");
        device.setCapability("newCommandTimeout", 600);
        return setSauceCredentials(device);
    }

    public Capabilities getIphoneXS() {
        DesiredCapabilities device = new DesiredCapabilities();
        device.setCapability("browserName", "");
        device.setCapability("name", "iOS Native Demo");
        device.setCapability("platformName", "iOS");
        device.setCapability("platformVersion", "13.0");
        device.setCapability("appiumVersion", "1.17.1");
        device.setCapability("deviceName", "iPhone XS Simulator");
        return setSauceCredentials(device);
    }

    public Capabilities getAndroid8ChromeEmulator(boolean headless) {
        ChromeOptions androidEmulator = new ChromeOptions().setHeadless(headless);
        androidEmulator.addArguments("hide-scrollbars");
        Map<String, Object> deviceMetrics = new HashMap<>();
        deviceMetrics.put("width", 384);
        deviceMetrics.put("height", 512);
        deviceMetrics.put("pixelRation", 2);
        Map<String, Object> mobileEmulation = new HashMap<>();
        mobileEmulation.put("deviceMetrics", deviceMetrics);
        mobileEmulation.put("userAgent", "Mozilla/5.0 (Linux; Android 8.0.0; Android SDK built for x86_64 Build/OSR1.180418.004) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/69.0.3497.100 Mobile Safari/537.36");
        androidEmulator.setExperimentalOption("mobileEmulation", mobileEmulation);
        return androidEmulator;
    }

    public Capabilities setSauceCredentials(Capabilities caps, boolean legacy, Capabilities options) {
        MutableCapabilities sauceOpts = new MutableCapabilities();
        sauceOpts.setCapability("username", System.getenv("SAUCE_USERNAME"));
        sauceOpts.setCapability("accessKey", System.getenv("SAUCE_ACCESS_KEY"));
        sauceOpts = sauceOpts.merge(options);
        if (legacy) {
            return caps.merge(sauceOpts);
        } else {
            MutableCapabilities sauce = new MutableCapabilities();
            sauce.setCapability("sauce:options", sauceOpts);
            return caps.merge(sauce);
        }
    }

    public Capabilities setSauceCredentials(Capabilities caps) {
        return setSauceCredentials(caps, true, new MutableCapabilities());
    }

    // Method to parse env properties for the test permutation
    // is methods checks if the property present in the json
    // get methods retrieve the property value from the json
    private boolean isBrowser(JsonElement root) {
        return root.getAsJsonObject().get(BROWSER_PROPERTY) != null;
    }

    private String getBrowserName(JsonElement root) {
        return root.getAsJsonObject().get(BROWSER_PROPERTY).getAsString();
    }

    private boolean isDevice(JsonElement root) {
        return root.getAsJsonObject().get(DEVICE_PROPERTY) != null;
    }

    private String getDevice(JsonElement root) {
        return root.getAsJsonObject().get(DEVICE_PROPERTY).getAsString();
    }

    private boolean isApp(JsonElement root) {
        return root.getAsJsonObject().get(APP_PROPERTY) != null;
    }

    private String getApp(JsonElement root) {
        return root.getAsJsonObject().get(APP_PROPERTY).getAsString();
    }

    private boolean isHeadless(JsonElement root) {
        return root.getAsJsonObject().get(HEADLESS_PROPERTY) != null;
    }

    private boolean getHeadless(JsonElement root) {
        return root.getAsJsonObject().get(HEADLESS_PROPERTY).getAsBoolean();
    }

    private boolean isLegacy(JsonElement root) {
        return root.getAsJsonObject().get(LEGACY_PROPERTY) != null;
    }

    private boolean getLegacy(JsonElement root) {
        return root.getAsJsonObject().get(LEGACY_PROPERTY).getAsBoolean();
    }
}
