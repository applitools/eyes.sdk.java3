package coverage.drivers;

import org.openqa.selenium.Capabilities;
import org.openqa.selenium.MutableCapabilities;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.ie.InternetExplorerOptions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.safari.SafariOptions;

import java.util.HashMap;
import java.util.Map;

public class CapabilitiesHelper {
    public static Capabilities getChromeCaps(boolean headless) {
        return new ChromeOptions().setHeadless(headless);
    }

    public static Capabilities getFirefoxCaps(boolean headless) {
        return new FirefoxOptions().setHeadless(headless);
    }

    public static Capabilities getSafari11(boolean legacy) {
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

    public static Capabilities getSafari12(boolean legacy) {
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

    public static Capabilities getEdge18() {
        EdgeOptions edge = new EdgeOptions();
        edge.setCapability("browserVersion", "18.17763");
        edge.setCapability("platformName", "Windows 10");
        MutableCapabilities options = new MutableCapabilities();
        options.setCapability("name", "Edge 18");
        options.setCapability("avoidProxy", true);
        options.setCapability("screenResolution", "1920x1080");
        return setSauceCredentials(edge, false, options);
    }

    public static Capabilities getIE11(boolean legacy) {
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

    public static Capabilities getSamsungGalaxyS8() {
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

    public static Capabilities getIphoneXS() {
        DesiredCapabilities device = new DesiredCapabilities();
        device.setCapability("browserName", "");
        device.setCapability("name", "iOS Native Demo");
        device.setCapability("platformName", "iOS");
        device.setCapability("platformVersion", "13.0");
        device.setCapability("appiumVersion", "1.17.1");
        device.setCapability("deviceName", "iPhone XS Simulator");
        return setSauceCredentials(device);
    }

    public static ChromeOptions getAndroid8ChromeEmulator(boolean headless) {
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

    public static Capabilities setSauceCredentials(Capabilities caps, boolean legacy, Capabilities options) {
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

    public static Capabilities setSauceCredentials(Capabilities caps) {
        return setSauceCredentials(caps, true, new MutableCapabilities());
    }
}
