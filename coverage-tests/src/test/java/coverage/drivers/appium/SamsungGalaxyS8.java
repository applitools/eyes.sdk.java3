package coverage.drivers.appium;

import coverage.drivers.SELENIUM;
import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.MutableCapabilities;
import org.openqa.selenium.WebDriver;

import java.net.MalformedURLException;
import java.net.URL;
import static coverage.drivers.CapabilitiesHelper.getSamsungGalaxyS8;

public class SamsungGalaxyS8 implements NativeBuilder {
    public WebDriver build(String app) throws MalformedURLException {
        Capabilities caps = getSamsungGalaxyS8();
        MutableCapabilities appCap = new MutableCapabilities();
        appCap.setCapability("app", app);
        caps = caps.merge(appCap);
        return new AndroidDriver<>(new URL(SELENIUM.SAUCE.url), caps);
    }
}
