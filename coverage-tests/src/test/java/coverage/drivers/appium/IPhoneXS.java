package coverage.drivers.appium;

import coverage.drivers.SELENIUM;
import io.appium.java_client.ios.IOSDriver;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.MutableCapabilities;
import org.openqa.selenium.WebDriver;

import java.net.MalformedURLException;
import java.net.URL;

import static coverage.drivers.CapabilitiesHelper.getIphoneXS;

public class IPhoneXS implements NativeBuilder {
    public WebDriver build(String app) throws MalformedURLException {
        Capabilities caps = getIphoneXS();
        MutableCapabilities appCap = new MutableCapabilities();
        appCap.setCapability("app", app);
        caps = caps.merge(appCap);
        return new IOSDriver(new URL(SELENIUM.SAUCE.url), caps);
    }
}
