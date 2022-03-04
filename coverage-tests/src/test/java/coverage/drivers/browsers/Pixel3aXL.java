package coverage.drivers.browsers;

import coverage.drivers.SELENIUM;
import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.MutableCapabilities;
import org.openqa.selenium.WebDriver;

import java.net.MalformedURLException;
import java.net.URL;

import static coverage.drivers.CapabilitiesHelper.getIphoneXS;
import static coverage.drivers.CapabilitiesHelper.getPixel3aXL;

public class Pixel3aXL implements DeviceBuilder {

    private String browser;

    public void browser(String browser) {
        this.browser = browser;
    }

    public WebDriver build(boolean headless, boolean legacy, boolean executionGrid) throws MalformedURLException {
        Capabilities caps = getPixel3aXL();
        MutableCapabilities appCap = new MutableCapabilities();
        appCap.setCapability("browserName", browser);
        caps = caps.merge(appCap);
        return new AndroidDriver<>(new URL(SELENIUM.SAUCE.url), caps);
    }
}
