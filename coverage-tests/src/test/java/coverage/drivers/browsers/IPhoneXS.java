package coverage.drivers.browsers;

import coverage.drivers.SELENIUM;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.MutableCapabilities;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.net.MalformedURLException;
import java.net.URL;

import static coverage.drivers.CapabilitiesHelper.getIphoneXS;

public class IPhoneXS implements DeviceBuilder {

    private String browser;

    public void browser(String browser) {
        this.browser = browser;
    }

    public WebDriver build(boolean headless, boolean legacy, boolean executionGrid) throws MalformedURLException {
        Capabilities caps = getIphoneXS();
        MutableCapabilities appCap = new MutableCapabilities();
        appCap.setCapability("browserName", browser);
        caps = caps.merge(appCap);
        return new RemoteWebDriver(new URL(SELENIUM.SAUCE.url), caps);
    }
}
